package com.newpostech.randy.comboscrolldemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newpostech.randy.comboscrolldemo.R;
import com.newpostech.randy.comboscrolldemo.holder.ViewHolder;
import com.newpostech.randy.comboscrolldemo.util.Util;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2017/1/6
 * Time: 16:59
 * Description: TODO
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , Util.dp2px(parent.getContext(), 50)));
        textView.setBackgroundResource(R.drawable.list_item_bg_with_border_bottom);
        int paddingHor = Util.dp2px(parent.getContext(), 16);
        textView.setPadding(paddingHor, 0, paddingHor, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(16);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setText("item " + position);
    }

    @Override
    public int getItemCount() {
        return 60;
    }
}
