package com.example.hp.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.myapplication.Database.Database;
import com.example.hp.myapplication.Model.Order;
import com.example.hp.myapplication.ViewHolder.CartAdapter;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;

    private TextView txtTotalPrice;
    private Button btnPlace;

    private List<Order> cart = new ArrayList<>();
    private CartAdapter adapter;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        database = FirebaseDatabase.getInstance();

        initRecycleView();

        txtTotalPrice = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(v -> {
            if (price.equals("0.0")) {
                Toast.makeText(Cart.this, "Ваша корзина порожня",
                        Toast.LENGTH_LONG).show();
                return;
            }
            showOrderActivity();
        });
        loadListFood();
    }

    private void showOrderActivity() {
        Intent order = new Intent(Cart.this, OrderActivity.class);
        startActivity(order);
        finish();
    }

    private void initRecycleView() {
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void loadListFood() {
        cart = new Database(this).getCart();
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                setTotalPrice();
            }
        };
        adapter.registerAdapterDataObserver(observer);
        setTotalPrice();
    }

    private void setTotalPrice() {
        cart = new Database(this).getCart();
        double total = 0;
        for (Order order : cart) {
            total += (Double.parseDouble(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("ua", "UA");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        price = String.valueOf(total).trim();

        txtTotalPrice.setText(fmt.format(total));
    }
}
