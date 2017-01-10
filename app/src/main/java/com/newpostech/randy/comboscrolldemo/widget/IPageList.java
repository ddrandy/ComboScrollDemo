package com.newpostech.randy.comboscrolldemo.widget;

import android.view.View;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/10
 * Time: 8:43
 * Description: TODO
 */

public interface IPageList {

    View getPageView(int position);

    void setPagerAdapter();
}
