package com.newpostech.randy.comboscrolldemo.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/6
 * Time: 17:09
 * Description: TODO
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    private final View mItemView;

    public ViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
    }

    public void setText(String string) {
        ((TextView) mItemView).setText(string);
    }
}
