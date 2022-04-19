package com.umashankar.localitem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.os.Build;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umashankar.localitem.Model.Coupon;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewOffersActivity extends AppCompatActivity {

    private static final String NOTIFICATION_SHOWN = "1" ;
    private static final String NUMBER_VISITS = "0";
    private String CHANNEL_ID = "channel";
    public TextView txtCouponCode,txtCouponDescription,txtCouponexpDate,txtCouponValue,txtCouponName;
    public ImageView CouponImage;
    String CouponId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offers);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.myOfferView_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Offer Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CouponId = getIntent().getStringExtra("offerCode");

        CouponImage = findViewById(R.id.view_coupon_Image);
        txtCouponName = findViewById(R.id.view_coupon_name);
        txtCouponCode = findViewById(R.id.view_coupon_Code);
        txtCouponValue = findViewById(R.id.view_coupon_Value);
        txtCouponDescription = findViewById(R.id.view_coupon_Detail);
        txtCouponexpDate = findViewById(R.id.view_coupon_Exp_Date);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        getCouponDetails(CouponId);
    }

    private void getCouponDetails(String couponId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("CouponsCode").child("Activate");
        productRef.child(couponId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Coupon coupon = dataSnapshot.getValue(Coupon.class);

                    txtCouponName.setText(coupon.getCouponName());
                    txtCouponCode.setText(coupon.getCouponCode());
                    txtCouponDescription.setText(coupon.getDescription());
                    txtCouponValue.setText("Rs."+coupon.getCouponValue()+" OFF");
                    txtCouponexpDate.setText("Valid "+coupon.getStartDate()+" to "+coupon.getEndDate());
                    Picasso.get().load(coupon.getCouponTheme()).into(CouponImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void OfferActivate(View view) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)  {

            NotificationManager manager = getSystemService(NotificationManager.class);
            Log.i("notify","1");
            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Offer Unlocked").setContentText(txtCouponCode.getText().toString()).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
            manager.notify(1,notification);
        }
    }

}
