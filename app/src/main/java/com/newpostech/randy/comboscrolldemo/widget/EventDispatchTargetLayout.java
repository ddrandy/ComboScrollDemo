package com.newpostech.randy.comboscrolldemo.widget;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.newpostech.randy.comboscrolldemo.R;

import java.util.ArrayList;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/4
 * Time: 9:26
 * Description: TODO
 */

public class EventDispatchTargetLayout extends LinearLayout implements EventDispatchPlanLayout.ITargetView {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ArrayList<String> mData = new ArrayList<>();
    private SparseArray<ListView> mPageMap = new SparseArray<>();
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getPageView(position);
            ViewGroup.LayoutParams params = new ViewGroup
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mData.get(position);
        }
    };

    private View getPageView(int position) {
        ListView view = mPageMap.get(position);
        if (view == null) {
            view = new ListView(getContext());
            view.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mData));
            mPageMap.put(position, view);
        }
        return view;
    }

    public EventDispatchTargetLayout(Context context) {
        this(context, null);
    }

    public EventDispatchTargetLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        for (int i = 0; i < 60; i++) {
            mData.add("item " + i);
        }
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean canChildScrollUp() {
        if (mViewPager == null) {
            return false;
        }
        int currentItem = mViewPager.getCurrentItem();
        ListView listView = mPageMap.get(currentItem);
        if (listView == null) {
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return listView.getChildCount() > 0
                    && (listView.getFirstVisiblePosition() > 0
                    || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        } else {
            return ViewCompat.canScrollVertically(listView, -1);
        }
    }

    @Override
    public void fling(float vy) {
        if (mViewPager == null) {
            return;
        }
        int currentItem = mViewPager.getCurrentItem();
        ListView listView = mPageMap.get(currentItem);
        if (listView == null) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            listView.fling((int) -vy);
        } else {
            // 可调用第三方实现
        }
    }
}
