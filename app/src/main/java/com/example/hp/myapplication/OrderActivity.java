package com.example.hp.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.myapplication.Common.Common;
import com.example.hp.myapplication.Database.Database;
import com.example.hp.myapplication.Model.Order;
import com.example.hp.myapplication.Model.Request;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationCallback mLocationCallback;
    private Button btnCurrentLocation, btnPlaceOrder;
    private TextView currentAddress;
    private TextView phoneNumber;
    private TextView price;
    private List<Order> cart;
    private DatabaseReference requests;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initComponents();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initComponents() {
        setToolbar();
        price = findViewById(R.id.price);
        setTotalPrice();
        requests = FirebaseDatabase.getInstance().getReference("Requests");
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder2);
        phoneNumber = findViewById(R.id.phone_number);
        currentAddress = findViewById(R.id.current_address);
        btnCurrentLocation = findViewById(R.id.btn_current_location);
        btnCurrentLocation.setOnClickListener(view -> callRequestLocation());
        btnPlaceOrder.setOnClickListener(view -> {
            if (phoneNumber == null || phoneNumber.getText() == null
                    || phoneNumber.getText().equals("") || phoneNumber.getText().length() < 10) {
                Toast.makeText(OrderActivity.this,
                        "Номер телефону повинен мiстити 10 символiв", Toast.LENGTH_LONG).show();
                return;
            }
            if (currentAddress == null || currentAddress.getText() == null
                    || currentAddress.getText().toString().equals("") ||
                    currentAddress.getText().toString().trim().length() < 1) {
                Toast.makeText(OrderActivity.this, "Адреса не може бути порожньою",
                        Toast.LENGTH_LONG).show();
                return;
            }
            processRequest();
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location mCurrentLocation = locationResult.getLastLocation();
                showLocationToScreen(mCurrentLocation);
            }
        };
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setTitle("Дякуємо за замовлення!");
        builder.setMessage("Ваше замовлення прийнято. Очiкуйте дзвiнка");
        builder.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        builder.setPositiveButton("Выхiд", (dialog, which) -> finish());
        builder.show();
    }


    private void processRequest() {
        Request request = new Request(
                phoneNumber.getText().toString(),
                currentAddress.getText().toString(),
                price.getText().toString(),
                cart
        );
        requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
        new Database(getBaseContext()).cleanCart();
        showInfoDialog();
    }

    private String getLocationAddress(LatLng myCoordinates) {
        String myCity = "";
        Geocoder geocoder = new Geocoder(OrderActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getThoroughfare() + " " +
                    addresses.get(0).getFeatureName();
            Log.d("mylog", "Complete Address: " + addresses.toString());
            Log.d("mylog", "Address: " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCity;
    }

    private void callRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("mylog", "Getting Location Permission");
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("mylog", "Not granted");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
            } else {
                requestLocation();
            }
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        Log.d("mylog", "In Requesting Location");
        if (location != null && (System.currentTimeMillis() - location.getTime()) <= 1000 * 2) {
            showLocationToScreen(location);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Log.d("mylog", "Last location too old getting new location!");
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        }
    }

    private void showLocationToScreen(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        String locationAddress = getLocationAddress(myCoordinates);
        currentAddress.setText(locationAddress);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Оформлення замовлення");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setTotalPrice() {
        cart = new Database(this).getCart();
        double total = 0;
        for (Order order : cart) {
            total += (Double.parseDouble(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("ua", "UA");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        price.setText("Сума замовлення: " + fmt.format(total));
    }
}
