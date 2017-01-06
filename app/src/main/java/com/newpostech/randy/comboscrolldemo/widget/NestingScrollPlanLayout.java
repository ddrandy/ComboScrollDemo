package com.newpostech.randy.comboscrolldemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.view.NestedScrollingParent;

import com.newpostech.randy.comboscrolldemo.R;

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

    public NestingScrollPlanLayout(Context context) {
        this(context, null);
    }

    public NestingScrollPlanLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NestingScrollPlanLayout);
        mHeaderViewId = typedArray.getResourceId(R.styleable.NestingScrollPlanLayout_header_view, 0);
        mTargetViewId = typedArray.getResourceId(R.styleable.NestingScrollPlanLayout_target_view, 0);
        mHeaderInitOffset = typedArray
                .getDimensionPixelSize(R.styleable.NestingScrollPlanLayout_header_init_offset, 0);
        mTargetInitOffset = typedArray
                .getDimensionPixelSize(R.styleable.NestingScrollPlanLayout_target_init_offset, 0);
        mHeaderCurrentOffset = mHeaderInitOffset;
        mTargetCurrentOffset = mTargetInitOffset;
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }
}
