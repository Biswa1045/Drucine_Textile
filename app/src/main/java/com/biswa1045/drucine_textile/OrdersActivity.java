package com.biswa1045.drucine_textile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        findViewById(R.id.back_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in =  new Intent(OrdersActivity.this,HomeActivity.class);
                startActivity(in);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(OrdersActivity.this,HomeActivity.class);
        startActivity(in);
        finish();
    }
}