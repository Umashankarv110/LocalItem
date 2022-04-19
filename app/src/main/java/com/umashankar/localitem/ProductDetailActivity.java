package com.umashankar.localitem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.umashankar.localitem.Model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class ProductDetailActivity extends AppCompatActivity {

    private Button buynow;
    private LinearLayout linearLayoutCartBtn;
    private ConstraintLayout notifCount;
    private ImageView productImageView;
    private ElegantNumberButton numberButton;
    private TextView productPrice,Prices,productDetails, productDescription,productName,cart_btn_text,textView;
    private String productID = "", state ="Normal",cartItemID;
    private Spinner spinState;
    private String downloadImageUrl,rateFor="",SelectedState,Ship_price;
    private int size=0;
    private String productRandomeKey, saveCurrentDate,saveCurrentTime;  //store product key value in date n time formate

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentUser;
    private DatabaseReference cartListRef;
    private ProgressDialog loadingDialog;

    ArrayAdapter<String> stateAdapter;
    ArrayList<String> spinnerStateList;
    private ArrayList<String> AddState = new ArrayList<>();
    private DatabaseReference stateRef,stateReference,chargeRef;
    private String  addState,State;
    private int shipCharge =0;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.productDetail_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        stateReference = FirebaseDatabase.getInstance().getReference("AdminData").child("States");

        productID = getIntent().getStringExtra("pid");
        productName = findViewById(R.id.product_name_details);
        productDetails = findViewById(R.id.product_description_details);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price_detail);
        Prices = findViewById(R.id.product_price_details);
        productImageView = findViewById(R.id.product_image_details);
        numberButton = findViewById(R.id.number_btn);
        linearLayoutCartBtn = findViewById(R.id.add_to_cart_btn);
        buynow = findViewById(R.id.buy_now_btn);
        cart_btn_text = findViewById(R.id.add_to_cart_txt);

        getProductDetsils(productID);

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        while (salt.length() < 10) {
            int index = (int) (random.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        cartItemID = salt.toString();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setCanceledOnTouchOutside(false);

        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    AddToCart();
                    Intent intent = new Intent(ProductDetailActivity.this, OrderSummaryActivity.class);
                    startActivity(intent);
                }else{
                    SigninDialog();
                }
            }
        });

        linearLayoutCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    AddToCart();
                }else{
                    SigninDialog();
                }
            }
        });

    }

    private void getProductDetsils(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);
                    productName.setText(products.getPname());
                    productDetails.setText(products.getDetails());
                    productDescription.setText(products.getDescription());
                    productPrice.setText("₹"+products.getPrice());
                    Prices.setText(products.getPrice());
                    downloadImageUrl = products.getImage();
                    Glide.with(getApplicationContext()).load(products.getImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(productImageView);
                    rateFor = products.getRate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void AddToCart() {
        currentUser = firebaseUser.getUid();
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("mmm dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("UserData");
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("itemID", cartItemID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("description", productDescription.getText().toString());
        cartMap.put("price", Prices.getText().toString());
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("image", downloadImageUrl);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("rateFor", rateFor);
        cartMap.put("discount", "");
        cartListRef.child(currentUser).child("CartList").child(cartItemID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                            }
                        }
                });
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
                    finish();
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

    public void AddProducts(View view) {
        startActivity(new Intent(getApplicationContext(),HomeUserActivity.class));
    }

    private void SigninDialog() {
        if (firebaseUser == null){
            final Dialog signInDialog = new Dialog(ProductDetailActivity.this);
            signInDialog.setContentView(R.layout.signin_dialog);
            signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            signInDialog.setCancelable(true);
            Button signin = signInDialog.findViewById(R.id.cancel_btn);
            Button signup = signInDialog.findViewById(R.id.ok_btn);

            final Intent intent = new Intent(ProductDetailActivity.this, LoginAuthActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
