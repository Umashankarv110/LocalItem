package com.umashankar.localitem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class FinalOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_order);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.finalOrder_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void MyOrder(View view) {
        startActivity(new Intent(getApplicationContext(), MyOrdersActivity.class));
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            startActivity(new Intent(getApplicationContext(),HomeUserActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),HomeUserActivity.class));
        finish();
    }
}
