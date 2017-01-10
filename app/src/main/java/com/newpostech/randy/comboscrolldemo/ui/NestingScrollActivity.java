package com.newpostech.randy.comboscrolldemo.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.newpostech.randy.comboscrolldemo.R;
import com.newpostech.randy.comboscrolldemo.adapter.MyRecyclerViewAdapter;

import static com.newpostech.randy.comboscrolldemo.util.Cons.TAG;

import butterknife.BindView;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/6
 * Time: 11:51
 * Description: TODO
 */

public class NestingScrollActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected void initData() {
        setSupportActionBar(mToolbar);

        setPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nesting_scroll;
    }

}
