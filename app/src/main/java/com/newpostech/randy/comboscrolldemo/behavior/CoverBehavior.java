package com.newpostech.randy.comboscrolldemo.behavior;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import static com.newpostech.randy.comboscrolldemo.util.Cons.TAG;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/9
 * Time: 11:10
 * Description: TODO
 */

public class CoverBehavior extends CoordinatorLayout.Behavior<View> {

    private final int mHeaderInitOffset;
    private final int mHeaderEndOffset;
    private int mHeaderCurrentOffset;

    public CoverBehavior(int headerInitOffset, int headerEndOffset) {
        mHeaderInitOffset = headerInitOffset;
        mHeaderEndOffset = headerEndOffset;
        mHeaderCurrentOffset = mHeaderInitOffset;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        int width = parent.getWidth();
        int childWidth = child.getMeasuredWidth();
        int childHeight = child.getMeasuredHeight();
        child.layout(width / 2 - childWidth / 2,
                mHeaderCurrentOffset, width / 2 + childWidth / 2, mHeaderCurrentOffset + childHeight);
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        Log.i(TAG, "layoutDependsOn");
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
        if (lp.getBehavior() instanceof TargetBehavior) {
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
        if (lp.getBehavior() instanceof TargetBehavior) {
            TargetBehavior behavior = (TargetBehavior) lp.getBehavior();
            moveHeaderView(behavior, child);
            return true;
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }


    private void moveHeaderView(TargetBehavior behavior, View view) {
        int targetInitOffset = behavior.getTargetInitOffset();
        int targetEndOffset = behavior.getTargetEndOffset();
        int targetCurrentOffset = behavior.getTargetCurrentOffset();
        Log.i(TAG, "moveHeaderView: targetCurrentOffset = " + targetCurrentOffset);
        int target;
        if (targetCurrentOffset >= targetInitOffset) {
            target = mHeaderInitOffset;
        } else if (targetCurrentOffset <= targetEndOffset) {
            target = mHeaderEndOffset;
        } else {
            float percent = (targetCurrentOffset - targetEndOffset) * 1.0f / targetInitOffset - targetEndOffset;
            target = (int) (mHeaderEndOffset + percent * (mHeaderInitOffset - mHeaderEndOffset));
        }
        ViewCompat.offsetTopAndBottom(view, target - mHeaderCurrentOffset);
        mHeaderCurrentOffset = target;
    }
}