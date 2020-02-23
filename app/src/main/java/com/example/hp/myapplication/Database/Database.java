package com.example.hp.myapplication.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.hp.myapplication.Model.Order;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final int DB_VER = 1;
    private static final String DB_NAME = "cart";
    private static final String TABLE_NAME = "OrderDetail";

    private static final String ID = "_id";
    private static final String PRODUCT_ID = "ProductId";
    private static final String PRODUCT_NAME = "ProductName";
    private static final String QUANTITY = "Quantity";
    private static final String PRICE = "Price";
    private static final String DISCOUNT = "Discount";

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCart() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {PRODUCT_NAME, PRODUCT_ID, QUANTITY, PRICE, DISCOUNT};

        qb.setTables(TABLE_NAME);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Order(c.getString(c.getColumnIndex(PRODUCT_ID)),
                        c.getString(c.getColumnIndex(PRODUCT_NAME)),
                        c.getString(c.getColumnIndex(QUANTITY)),
                        c.getString(c.getColumnIndex(PRICE)),
                        c.getString(c.getColumnIndex(DISCOUNT))
                ));
            } while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PRODUCT_ID + " = '" + order.getProductId() + "'", null);
        String query;
        if (c.moveToFirst()) {
            query = String.format("UPDATE " + TABLE_NAME + " SET " + QUANTITY + "=" + QUANTITY +
                            "+'%s' WHERE +" + PRODUCT_ID + "=" + order.getProductId() + ";",
                    order.getQuantity());
        } else {
            query = String.format("INSERT INTO " + TABLE_NAME +
                            "(" + PRODUCT_ID + "," + PRODUCT_NAME + "," + QUANTITY + "," + PRICE + "," + DISCOUNT + ")" +
                            " VALUES ('%s','%s','%s','%s','%s');",
                    order.getProductId(),
                    order.getProductName(),
                    order.getQuantity(),
                    order.getPrice(),
                    order.getDiscount());
        }
        db.execSQL(query);
    }

    public void removeItem(String orderId) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + PRODUCT_ID + "=" + orderId;
        db.execSQL(query);
    }

    public void cleanCart() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "DELETE FROM " + TABLE_NAME;
        db.execSQL(query);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY," +
                PRODUCT_ID + " INTEGER," +
                PRODUCT_NAME + " TEXT," +
                QUANTITY + " INTEGER," +
                PRICE + " REAL," +
                DISCOUNT + " REAL" + ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
