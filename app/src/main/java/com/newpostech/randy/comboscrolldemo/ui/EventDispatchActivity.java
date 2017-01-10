package com.newpostech.randy.comboscrolldemo.ui;

import android.support.v7.widget.Toolbar;

import com.newpostech.randy.comboscrolldemo.R;

import butterknife.BindView;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/3
 * Time: 11:28
 * Description: TODO
 */

public class EventDispatchActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void initData() {
        setSupportActionBar(mToolbar);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event_dispatch;
    }

}
