package com.newpostech.randy.comboscrolldemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.support.v4.view.NestedScrollingParent;
import android.widget.Scroller;

import com.newpostech.randy.comboscrolldemo.R;
import com.newpostech.randy.comboscrolldemo.util.Util;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/6
 * Time: 11:39
 * Description: TODO
 */

public class NestingScrollPlanLayout extends ViewGroup implements NestedScrollingParent {

    private int mHeaderViewId;
    private int mTargetViewId;
    private int mHeaderInitOffset;
    private int mTargetInitOffset;
    private int mHeaderCurrentOffset;
    private int mTargetCurrentOffset;
    private int mHeaderEndOffset = 0;
    private int mTargetEndOffset = 0;
    private Scroller mScroller;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private View mHeaderView;
    private View mTargetView;
    private boolean mHasFling;
    private boolean mNeedScrollToEnd;
    private boolean mNeedScrollToInit;

    public NestingScrollPlanLayout(Context context) {
        this(context, null);
    }

    public NestingScrollPlanLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NestingScrollPlanLayout);
        mHeaderViewId = typedArray.getResourceId(R.styleable.NestingScrollPlanLayout_header_view, 0);
        mTargetViewId = typedArray.getResourceId(R.styleable.NestingScrollPlanLayout_target_view, 0);
        mHeaderInitOffset = typedArray.getDimensionPixelSize(
                R.styleable.NestingScrollPlanLayout_header_init_offset, Util.dp2px(getContext(), 30));
        mTargetInitOffset = typedArray.getDimensionPixelSize(
                R.styleable.NestingScrollPlanLayout_target_init_offset, Util.dp2px(getContext(), 70));
        mHeaderCurrentOffset = mHeaderInitOffset;
        mTargetCurrentOffset = mTargetInitOffset;
        typedArray.recycle();

        // Tells the ViewGroup whether to draw its children in the order
        // defined by the method ViewGroup.getChildDrawingOrder(int, int).
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        mScroller = new Scroller(getContext());
        mScroller.setFriction(0.98f);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mHeaderViewId != 0) {
            mHeaderView = findViewById(mHeaderViewId);
        }
        if (mTargetViewId != 0) {
            mTargetView = findViewById(mTargetViewId);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        ensureHeaderViewAndScrollerView();

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int childWidth = measuredWidth - paddingLeft - paddingRight;
        int childHeight = measuredHeight - paddingTop - paddingBottom;
        mTargetView.layout(paddingLeft, mTargetCurrentOffset + paddingTop
                , paddingLeft + childWidth, mTargetCurrentOffset + childHeight + paddingTop);
        int headerWidth = mHeaderView.getMeasuredWidth();
        int headerHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.layout(measuredWidth / 2 - headerWidth / 2, mHeaderCurrentOffset
                , measuredWidth / 2 + headerWidth / 2, mHeaderCurrentOffset + headerHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureHeaderViewAndScrollerView();
        int targetMeasureWidthSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        int targetMeasureHeightSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTargetView.measure(targetMeasureWidthSpec, targetMeasureHeightSpec);
        measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        ensureHeaderViewAndScrollerView();
        int headerIndex = indexOfChild(mHeaderView);
        int scrollIndex = indexOfChild(mTargetView);
        if (headerIndex < scrollIndex) {
            return i;
        }
        if (headerIndex == i) {
            return scrollIndex;
        } else if (scrollIndex == i) {
            return headerIndex;
        }
        return i;
    }

    private void ensureHeaderViewAndScrollerView() {
        if (mHeaderView != null && mTargetView != null) {
            return;
        }
        if (mHeaderView == null && mTargetView == null && getChildCount() >= 2) {
            mHeaderView = getChildAt(0);
            mTargetView = getChildAt(1);
            return;
        }
        throw new RuntimeException("ensure you add Header View and Scroller View");
    }

    /**
     * 是否接受NestingScroll
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        // accept vertical scroll
        return isEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    /**
     * 接受NestingScroll的hook
     */
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // NestedScrollingParentHelper record
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    /**
     * preScroll
     *
     * @param target   targetView
     * @param consumed tell sub view how much value consumed
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // NestingScroll 滚动前,先观察自己能不能消耗，消耗量记录在consumed
        // 上滑先看自己，下滑先看子view
        if (canViewScrollUp(target)) {
            return;
        }
        if (dy > 0) {
            //上滑
            int percentCanConsume = mTargetCurrentOffset - mTargetEndOffset;
            if (percentCanConsume > 0) {
                if (dy > percentCanConsume) {
                    // 自己消耗不完
                    consumed[1] = percentCanConsume;
                    moveTargetViewTo(mTargetEndOffset);
                } else {
                    // 自己全部消耗
                    consumed[1] = dy;
                    moveTargetView(-dy);
                }
            }
        }
    }


    /**
     * NestingScroll scrolling
     *
     * @param target       targetView
     * @param dxConsumed   消耗的滚动量x
     * @param dyConsumed   消耗的滚动量y
     * @param dxUnconsumed 未消耗的滚动量x
     * @param dyUnconsumed 未消耗的滚动量y
     */
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // 处理下滑，未消耗的滚动父view
        if (dyUnconsumed < 0 && !canViewScrollUp(target)) {
            int dy = -dyUnconsumed;
            moveTargetView(dy);
        }
    }

    /**
     * NestingScroll结束
     */
    @Override
    public void onStopNestedScroll(View child) {
        mNestedScrollingParentHelper.onStopNestedScroll(child);
        if (mHasFling) {
            mHasFling = false;
        } else {
            if (mTargetCurrentOffset <= (mTargetEndOffset + mTargetInitOffset) / 2) {
                mNeedScrollToEnd = true;
            } else {
                mNeedScrollToInit = true;
            }
            invalidate();
        }
    }

    /**
     * preFling
     * could consumed by parent View
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        super.onNestedPreFling(target, velocityX, velocityY);
        mHasFling = true;
        int vy = (int) -velocityY;
        if (velocityY < 0) {
            //向下
            if (canViewScrollUp(target)) {
                return false;
            }
            mNeedScrollToInit = true;
            mScroller.fling(0, mTargetCurrentOffset, 0, vy, 0, 0, mTargetEndOffset, Integer.MAX_VALUE);
            invalidate();
            return true;
        } else {
            // 向上
            if (mTargetCurrentOffset <= mTargetEndOffset) {
                return false;
            }
            mNeedScrollToEnd = true;
            mScroller.fling(0, mTargetCurrentOffset, 0, vy, 0, 0, mTargetEndOffset, Integer.MAX_VALUE);
            invalidate();
        }
        return false;
    }

    private void moveTargetView(float dy) {
        int target = (int) (mTargetCurrentOffset + dy);
        moveTargetViewTo(target);
    }

    private void moveTargetViewTo(int target) {
        target = Math.max(target, mTargetEndOffset);
        ViewCompat.offsetTopAndBottom(mTargetView, target - mTargetCurrentOffset);
        mTargetCurrentOffset = target;

        int headerTarget;
        if (mTargetCurrentOffset >= mTargetInitOffset) {
            headerTarget = mHeaderInitOffset;
        } else if (mTargetCurrentOffset <= mTargetEndOffset) {
            headerTarget = mHeaderEndOffset;
        } else {
            float percent = (mTargetCurrentOffset - mTargetEndOffset) * 1.0f / (mTargetInitOffset - mTargetEndOffset);
            headerTarget = (int) (mHeaderEndOffset + percent * (mHeaderInitOffset - mHeaderEndOffset));
        }
        ViewCompat.offsetTopAndBottom(mHeaderView, headerTarget - mHeaderCurrentOffset);
        mHeaderCurrentOffset = headerTarget;
    }

    private boolean canViewScrollUp(View target) {
        return ViewCompat.canScrollVertically(target, -1);
    }

    /**
     * fling
     */
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    /**
     * get axis
     */
    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int offsetY = mScroller.getCurrY();
            moveTargetViewTo(offsetY);
            invalidate();
        } else if (mNeedScrollToInit) {
            mNeedScrollToInit = false;
            if (mTargetCurrentOffset == mTargetInitOffset) {
                return;
            }
            mScroller.startScroll(0, mTargetCurrentOffset, 0, mTargetInitOffset - mTargetCurrentOffset);
            invalidate();
        } else if (mNeedScrollToEnd) {
            mNeedScrollToEnd = false;
            if (mTargetCurrentOffset == mTargetEndOffset) {
                return;
            }
            mScroller.startScroll(0, mTargetCurrentOffset, 0, mTargetEndOffset - mTargetCurrentOffset);
            invalidate();
        }
    }
}
