package com.umashankar.localitem;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.umashankar.localitem.HelperClass.PhpLink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HomeUserActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private int size=0;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private DatabaseReference cartListRef;
    private String currentUser;
    private ConstraintLayout notifCount;

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        CheckInternet();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_shop, R.id.navigation_notifications, R.id.navigation_account,R.id.navigation_offer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null){
            //Popup Notification
            createNotificationChannel();
            getToken();
        }
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    //Token
                    String token = task.getResult();
                    Log.i("tokens___", token);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_INSERT_FCM,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("tokenResponse", response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(HomeUserActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("Error", error.toString());
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("customerId", ""+firebaseUser.getUid());
                            map.put("Token", token);
                            return map;
                        }

                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(HomeUserActivity.this);
                    requestQueue.add(stringRequest);

                }else{
                    Log.d(TAG, "onComplete: Failed to get the Token");
                }
            }
        });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifyChannel";
            String description = "Receive Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_cart_menu, menu);
        MenuItem item = menu.findItem(R.id.product_action_cart);
        MenuItemCompat.setActionView(item, R.layout.badge_layout);
        notifCount = (ConstraintLayout)   MenuItemCompat.getActionView(item);

        if (firebaseUser != null){
            currentUser = firebaseUser.getUid();
            cartListRef = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUser);
            cartListRef.child("CartList")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 0) {
                                TextView tv = (TextView) notifCount.findViewById(R.id.badge_count);
                                tv.setText("0");
                            }else{
                                size = (int) dataSnapshot.getChildrenCount();
                                TextView tv = (TextView) notifCount.findViewById(R.id.badge_count);
                                tv.setText(""+size);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

            notifCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),AddToCartActivity.class);
                    intent.putExtra("cartCount",String.valueOf(size));
                    startActivity(intent);
                }
            });
        }else{
            notifCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SigninDialog();
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void CheckInternet() {
        if (networkInfo != null ) {
            if (networkInfo.isConnected()) {
            }
        }else{
            Toast.makeText(this, "Internet Not Available", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),OfflineActivity.class));
        }
    }


    private void SigninDialog() {
        if (firebaseUser == null){
            final Dialog signInDialog = new Dialog(HomeUserActivity.this);
            signInDialog.setContentView(R.layout.signin_dialog);
            signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            signInDialog.setCancelable(true);
            Button signin = signInDialog.findViewById(R.id.cancel_btn);
            Button signup = signInDialog.findViewById(R.id.ok_btn);

            final Intent intent = new Intent(HomeUserActivity.this, LoginAuthActivity.class);
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInDialog.dismiss();
                    LoginAuthActivity.setSignUpFragment = false;
                    startActivity(intent);
                }
            });
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInDialog.dismiss();
                    LoginAuthActivity.setSignUpFragment = true;
                    startActivity(intent);
                }
            });

            signInDialog.show();
        }
    }
    public void ShareAppLink(View view) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage= "";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.umashankar.localitem";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share with"));
        } catch(Exception e) {
            //e.toString();
        }

//        startActivity(new Intent(getApplicationContext(), InvoiceActivity.class));

//        String path = "/Documents/Kirat Broadband";
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
//                +  File.separator + path + File.separator);
//        intent.setDataAndType(uri, "text/csv");
//        startActivity(Intent.createChooser(intent, "Open folder"));
    }


}
