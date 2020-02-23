package com.example.hp.myapplication.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.myapplication.R;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cartName, orderPrice;
    public ImageView imgCartCount;
    public Button btnRemove;

    public CartViewHolder(View itemView) {
        super(itemView);

        cartName = itemView.findViewById(R.id.cart_item_name);
        orderPrice = itemView.findViewById(R.id.cart_item_price);
        imgCartCount = itemView.findViewById(R.id.cart_item_count);
        btnRemove = itemView.findViewById(R.id.btnRemove);

    }

    @Override
    public void onClick(View v) {

    }
}