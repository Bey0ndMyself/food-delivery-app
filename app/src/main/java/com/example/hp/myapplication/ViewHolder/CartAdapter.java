package com.example.hp.myapplication.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.hp.myapplication.Database.Database;
import com.example.hp.myapplication.Model.Order;
import com.example.hp.myapplication.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> orders;
    private Context context;
    private Database database;

    public CartAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
        database = new Database(context);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {
        TextDrawable drawable = TextDrawable.builder().buildRound(""+ orders.get(position).getQuantity(), Color.GRAY);
        holder.imgCartCount.setImageDrawable(drawable);
        Locale locale = new Locale("ua","UA");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        double price = (Double.parseDouble(orders.get(position).getPrice()))*(Integer.parseInt(orders.get(position).getQuantity()));
        holder.orderPrice.setText(fmt.format(price));
        holder.cartName.setText(orders.get(position).getProductName());

        Button btnRemove = holder.btnRemove;
        btnRemove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Order order = orders.remove(position);
                database.removeItem(order.getProductId());
                Toast.makeText(context, "Видалено з кошика", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}