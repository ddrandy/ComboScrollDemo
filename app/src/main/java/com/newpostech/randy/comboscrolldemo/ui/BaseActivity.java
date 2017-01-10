package com.newpostech.randy.comboscrolldemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.newpostech.randy.comboscrolldemo.R;
import com.newpostech.randy.comboscrolldemo.adapter.MyRecyclerViewAdapter;
import com.newpostech.randy.comboscrolldemo.widget.IPageList;

import butterknife.ButterKnife;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/5
 * Time: 16:56
 * Description: TODO
 */

public abstract class BaseActivity extends AppCompatActivity implements IPageList {

    protected SparseArray<RecyclerView> mPageMap = new SparseArray<>();
    protected PagerAdapter mPagerAdapter;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initData();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    protected abstract void initData();

    protected abstract int getLayoutId();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public final View getPageView(int position) {
        RecyclerView recyclerView = mPageMap.get(position);
        if (recyclerView == null) {
            recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MyRecyclerViewAdapter());
            mPageMap.put(position, recyclerView);
        }
        return recyclerView;
    }

    @Override
    public final void setPagerAdapter() {
        mPagerAdapter = new PagerAdapter() {
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
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Item " + position;
            }
        };
    }
}
