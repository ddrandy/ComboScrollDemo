package com.newpostech.randy.comboscrolldemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.newpostech.randy.comboscrolldemo.R;

import butterknife.ButterKnife;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/5
 * Time: 16:56
 * Description: TODO
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initData();
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
}
