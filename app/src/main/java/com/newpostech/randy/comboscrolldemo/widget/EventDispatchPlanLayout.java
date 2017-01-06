package com.newpostech.randy.comboscrolldemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import static com.newpostech.randy.comboscrolldemo.util.Cons.TAG;

import com.newpostech.randy.comboscrolldemo.R;
import com.newpostech.randy.comboscrolldemo.util.Util;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/3
 * Time: 13:56
 * Description: TODO
 */

public class EventDispatchPlanLayout extends ViewGroup {

    private static final int INVALID_POINTER = -1;

    private int mHeaderViewId;
    private int mTargetViewId;

    private int mHeaderInitOffset;
    private int mTargetInitOffset;

    private int mHeaderCurrentOffset;
    private int mTargetCurrentOffset;

    private int mTargetEndOffset = 0;
    private int mHeaderEndOffset = 0;

    private int mMaxVelocity;
    private int mTouchSlop;

    private Scroller mScroller;

    private View mHeaderView;
    private View mTargetView;
    private ITargetView mTarget;

    private int mActivePointerId = INVALID_POINTER;
    private boolean mIsDragging;
    private VelocityTracker mVelocityTracker;
    private float mLastMotionY;
    private float mInitialDownY;
    private float mInitialMotionY;
    private boolean mNeedScrollToInitPos;
    private boolean mNeedScrollToEndPos;

    public EventDispatchPlanLayout(Context context) {
        this(context, null);
    }

    public EventDispatchPlanLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EventDispatchPlanLayout, 0, 0);
        // get view id
        mHeaderViewId = array.getResourceId(R.styleable.EventDispatchPlanLayout_header_view, 0);
        mTargetViewId = array.getResourceId(R.styleable.EventDispatchPlanLayout_target_view, 0);
        // get init offset
        mHeaderInitOffset = array.getDimensionPixelSize(R.
                styleable.EventDispatchPlanLayout_header_init_offset, Util.dp2px(getContext(), 20));
        mTargetInitOffset = array.getDimensionPixelSize(R.
                styleable.EventDispatchPlanLayout_target_init_offset, Util.dp2px(getContext(), 40));
        mHeaderCurrentOffset = mHeaderInitOffset;
        mTargetCurrentOffset = mTargetInitOffset;

        array.recycle();

        // Tells the ViewGroup whether to draw its children in the order defined
        // by the method ViewGroup.getChildDrawingOrder(int, int).
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);

        ViewConfiguration viewConf = ViewConfiguration.get(getContext());
        mMaxVelocity = viewConf.getScaledMaximumFlingVelocity();
        // Distance in pixels a touch can wander before we think the user is scrolling
        mTouchSlop = Util.px2dp(getContext(), viewConf.getScaledTouchSlop());

        mScroller = new Scroller(getContext());
        mScroller.setFriction(0.98f);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mHeaderViewId != 0) {
            mHeaderView = findViewById(mHeaderViewId);
        }
        if (mTargetViewId != 0) {
            mTargetView = findViewById(mTargetViewId);
            ensureTarget();
        }
    }

    private void ensureTarget() {
        if (mTargetView instanceof ITargetView) {
            mTarget = ((ITargetView) mTargetView);
        } else {
            throw new RuntimeException("TargetView must implement ITargetView");
        }
    }

    private void ensureHeaderViewAndScrollView() {
        if (mHeaderView != null && mTargetView != null) {
            return;
        }
        if (mHeaderView == null && mTargetView == null && getChildCount() >= 2) {
            mHeaderView = getChildAt(0);
            mTargetView = getChildAt(1);
            ensureTarget();
            return;
        }
        throw new RuntimeException("ensure you add headerView and scrollView");
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        ensureHeaderViewAndScrollView();
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

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // 去掉默认行为，使得每个事件都会经过这个Layout
//        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureHeaderViewAndScrollView();
        int scrollMeasureWidthSpec = MeasureSpec.
                makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        int scrollMeasureHeightSpec = MeasureSpec.
                makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTargetView.measure(scrollMeasureWidthSpec, scrollMeasureHeightSpec);
        measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        ensureHeaderViewAndScrollView();

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int childWidth = measuredWidth - paddingLeft - paddingRight;
        int childHeight = measuredHeight - paddingTop - paddingBottom;

        mTargetView.layout(paddingLeft, mTargetCurrentOffset + paddingTop,
                paddingLeft + childWidth, paddingTop + mTargetCurrentOffset + childHeight);
        int refreshViewWidth = mHeaderView.getMeasuredWidth();
        int refreshViewHeight = mHeaderView.getMeasuredHeight();

        mHeaderView.layout(measuredWidth / 2 - refreshViewWidth / 2, mHeaderCurrentOffset,
                measuredWidth / 2 + refreshViewWidth / 2, mHeaderCurrentOffset + refreshViewHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureHeaderViewAndScrollView();
        final int action = MotionEventCompat.getActionMasked(ev);
        if (!isEnabled() || mTarget.canChildScrollUp()) {
            Log.d(TAG, "fast end onIntercept: isEnabled = " + isEnabled()
                    + "; canChildScrollUp = " + mTarget.canChildScrollUp());
            return false;
        }
        int pointerIndex;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsDragging = false;
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (checkPointerIndex(pointerIndex, "ACTION_MOVE")) return false;
                float y = ev.getY(pointerIndex);
                startDragging(y);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }
        return mIsDragging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        if (!isEnabled() || mTarget.canChildScrollUp()) {
            Log.d(TAG, "onTouchEvent: isEnable = " + isEnabled() +
                    "; canChildScrollUp = " + mTarget.canChildScrollUp());
            return false;
        }
        Log.d(TAG, "onTouchEvent: isEnable = " + isEnabled() +
                "; canChildScrollUp = " + mTarget.canChildScrollUp());

        acquireVelocityTracker(event);

        int pointerIndex;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                mIsDragging = false;
                break;
            case MotionEvent.ACTION_MOVE: {
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (checkPointerIndex(pointerIndex, "ACTION_MOVE")) return false;
                final float y = event.getY(pointerIndex);
                startDragging(y);

                if (mIsDragging) {
                    float dy = y - mLastMotionY;
                    if (dy >= 0) {
                        moveTargetView(dy);
                    } else {
                        if (mTargetCurrentOffset + dy <= mTargetEndOffset) {
                            moveTargetView(dy);
                            // 重新dispatch DOWN事件
                            int oldAction = event.getAction();
                            event.setAction(MotionEvent.ACTION_DOWN);
                            dispatchTouchEvent(event);
                            event.setAction(oldAction);
                        } else {
                            moveTargetView(dy);
                        }
                    }
                    mLastMotionY = y;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = MotionEventCompat.getActionIndex(event);
                if (checkPointerIndex(pointerIndex, "ACTION_POINTER_DOWN")) return false;
                mActivePointerId = event.getPointerId(pointerIndex);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_UP: {
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (checkPointerIndex(pointerIndex, "ACTION_UP")) return false;
                if (mIsDragging) {
                    mIsDragging = false;
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    final float vy = mVelocityTracker.getYVelocity(mActivePointerId);
                    finishDrag((int) vy);
                }
                mActivePointerId = INVALID_POINTER;
                releaseVelocityTracker();
//                mIsDragging = false;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                releaseVelocityTracker();
                return false;
        }
        return mIsDragging;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // there was our active pointer going up.
            // Choose a new active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex & 1 ^ 1;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void finishDrag(int velocity) {
        Log.d(TAG, "touchUp: vy = " + velocity);
        if (velocity > 0) {
            mNeedScrollToInitPos = true;
            mScroller.fling(0, mTargetCurrentOffset, 0, velocity, 0, 0, mTargetEndOffset, Integer.MAX_VALUE);
        } else if (velocity < 0) {
            mNeedScrollToInitPos = true;
            mScroller.fling(0, mTargetCurrentOffset, 0, velocity, 0, 0, mTargetEndOffset, Integer.MAX_VALUE);
        } else {
            if (mTargetCurrentOffset <= (mTargetInitOffset + mTargetEndOffset) / 2) {
                mNeedScrollToEndPos = true;
            } else {
                mNeedScrollToInitPos = true;
            }
        }
        invalidate();
    }

    private boolean checkPointerIndex(int pointerIndex, String str) {
        if (pointerIndex < 0) {
            Log.d(TAG, "got " + str + " event but have an invalid action index");
            return true;
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

    private void startDragging(float y) {
        Log.d(TAG, "startDragging: ");
        if (y > mInitialDownY || mTargetCurrentOffset > mTargetEndOffset) {
            final float yDiff = Math.abs(y - mInitialDownY);
            if (yDiff > mTouchSlop && !mIsDragging) {
                mInitialMotionY = mInitialDownY + mTouchSlop;
                mLastMotionY = mInitialMotionY;
                mIsDragging = true;
            }
        }
    }

    private void acquireVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    public interface ITargetView {

        boolean canChildScrollUp();

        void fling(float vy);
    }

    @Override
    public void computeScroll() {
//        Log.d(TAG, "computeScroll: ");
        if (mScroller.computeScrollOffset()) {
            final int currY = mScroller.getCurrY();
            moveTargetViewTo(currY);
        } else if (mNeedScrollToInitPos) {
            mNeedScrollToInitPos = false;
            if (mTargetCurrentOffset == mTargetInitOffset) return;
            mScroller.startScroll(0, mTargetCurrentOffset, 0, mTargetInitOffset - mTargetCurrentOffset);
        } else if (mNeedScrollToEndPos) {
            mNeedScrollToEndPos = false;
            if (mTargetCurrentOffset == mTargetEndOffset) {
                if (mScroller.getCurrVelocity() > 0) {
                    //如果有速度，传递给子view
                    mTarget.fling(-mScroller.getCurrVelocity());
                }
            }
            mScroller.startScroll(0, mTargetCurrentOffset, 0, mTargetEndOffset - mTargetCurrentOffset);
        }
        invalidate();
    }
}

