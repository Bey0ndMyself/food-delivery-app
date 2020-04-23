package com.example.hp.myapplication;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.hp.myapplication.Database.Database;
import com.example.hp.myapplication.Model.Food;
import com.example.hp.myapplication.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class FoodDetail extends AppCompatActivity {

    private TextView food_name, food_price, food_description, food_absent;
    private ImageView food_image;
    private ElegantNumberButton numberButton;
    private FloatingActionButton btnCart;
    private String foodId = "";

    private DatabaseReference foods;
    private Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        foods = FirebaseDatabase.getInstance().getReference("Foods");

        numberButton = findViewById(R.id.number_button);

        btnCart = findViewById(R.id.btncart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()));
                Toast.makeText(FoodDetail.this, "Додано в кошик", Toast.LENGTH_SHORT).show();
            }
        });

        food_description = findViewById(R.id.food_description);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_absent = findViewById(R.id.food_absent);
        food_image = findViewById(R.id.img_food);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("FoodId");
        }
        if (!foodId.isEmpty()) {
            getDetailFood(foodId);
        }
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);
                Glide.with(getBaseContext()).load(currentFood.getImage()).into(food_image);

                Locale locale = new Locale("ua", "UA");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                double price = Double.parseDouble(currentFood.getPrice());
                food_price.setText(fmt.format(price));
                if (!Boolean.parseBoolean(currentFood.getAvailable())) {
                    food_absent.setText("No product");
                    food_price.setPaintFlags(food_price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    btnCart.setEnabled(false);
                }
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
