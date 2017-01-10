package com.newpostech.randy.comboscrolldemo.ui;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.newpostech.randy.comboscrolldemo.R;
import com.newpostech.randy.comboscrolldemo.behavior.CoverBehavior;
import com.newpostech.randy.comboscrolldemo.behavior.TargetBehavior;
import com.newpostech.randy.comboscrolldemo.util.Util;

import butterknife.BindView;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/9
 * Time: 9:59
 * Description: TODO
 */

public class CoordinatorLayoutActivity extends BaseActivity {

    @BindView(R.id.header_view)
    View mHeaderView;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.target_view)
    LinearLayout mTargetView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void initData() {
        setSupportActionBar(mToolbar);
        CoordinatorLayout.LayoutParams headerLayoutParams = (CoordinatorLayout.LayoutParams) mHeaderView.getLayoutParams();
        headerLayoutParams.setBehavior(new CoverBehavior(Util.dp2px(this, 30), 0));
        CoordinatorLayout.LayoutParams targetLayoutParams = (CoordinatorLayout.LayoutParams) mTargetView.getLayoutParams();
        targetLayoutParams.setBehavior(new TargetBehavior(this, Util.dp2px(this, 70), 0));

        setPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_coordinator_layout;
    }

}
