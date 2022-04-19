package com.umashankar.localitem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.umashankar.localitem.Model.Cart;
import com.umashankar.localitem.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddToCartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextButton,DeleteBtn;
    private TextView txtTotalAmt,subTotal,txtTotalProduct;
    private ImageView imageView;
    private int size=0;
    private int qty = 0, price=0, oneTypeProductPrice = 0,backupQty;
    private int OverAllTotalPrice =0,OverAllTotalQty=0;
    private String productID = "",count="",rateFor="";
    private DatabaseReference cartListRef;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentUser;
    private ConstraintLayout cartEmpty,cartNotEmpty;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        productID = getIntent().getStringExtra("pid");

        Toolbar toolbar = findViewById(R.id.myCart_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUser = firebaseUser.getUid();

        cartListRef = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUser);

        recyclerView = findViewById(R.id.recyclerCartList);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextButton = findViewById(R.id.next_btn);
        txtTotalAmt = findViewById(R.id.cart_total_price);
        imageView = findViewById(R.id.cart_product_image);
        subTotal = findViewById(R.id.cart_sub_Total);
        txtTotalProduct=findViewById(R.id.cart_TotalProduct);
        cartEmpty = findViewById(R.id.emptyCartLayout);
        cartNotEmpty = findViewById(R.id.cartLayout);

        AllCartProduct();

        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),OrderSummaryActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void AllCartProduct() {
        cartListRef.child("CartList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    cartEmpty.setVisibility(View.VISIBLE);
                    cartNotEmpty.setVisibility(View.GONE);
                    getSupportActionBar().setTitle("My Cart");
                }else{
                    size = (int) dataSnapshot.getChildrenCount();
                    count = String.valueOf(size);
                    cartEmpty.setVisibility(View.GONE);
                    getSupportActionBar().setTitle("Cart ("+count+" Item)");
                    cartNotEmpty.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getCartData();
    }

    private void getCartData() {
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("CartList")
                                , Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, final Cart cart) {
                        cartViewHolder.txtProductName.setText(cart.getPname());
                        cartViewHolder.txtProductPrice.setText("₹" + cart.getPrice());
                        cartViewHolder.txtProductQty.setText("Qty: " + cart.getQuantity());
                        Glide.with(getApplicationContext()).load(cart.getImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(cartViewHolder.cartProductImage);

                        rateFor = cart.getRateFor();
                        qty = Integer.valueOf(cart.getQuantity());
                        price = Integer.valueOf(cart.getPrice());
                        oneTypeProductPrice = (price * qty);

                        cartViewHolder.txtTotalProductPrice.setText("Total: " + oneTypeProductPrice + "/-");

                        OverAllTotalPrice = OverAllTotalPrice + oneTypeProductPrice;
                        OverAllTotalQty = OverAllTotalQty + qty;
                        backupQty = qty;

                        txtTotalProduct.setText(OverAllTotalQty + "(Item)");
                        subTotal.setText("" + OverAllTotalPrice);
                        txtTotalAmt.setText("Total: " + OverAllTotalPrice + "/-");

                        cartViewHolder.cartEdit.setVisibility(View.GONE);
                        cartViewHolder.cartItemDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                new AlertDialog.Builder(AddToCartActivity.this)
                                        .setMessage("Are you sure you want to Delete?")
                                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        cartListRef.child("CartList").child(cart.getItemID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    OverAllTotalPrice = OverAllTotalPrice - oneTypeProductPrice;
                                                    OverAllTotalQty = OverAllTotalQty - qty;
                                                    txtTotalProduct.setText(OverAllTotalQty + "(Item)");
                                                    subTotal.setText("" + OverAllTotalPrice);
                                                    txtTotalAmt.setText("Total: " + OverAllTotalPrice + "/-");
                                                    Toast.makeText(AddToCartActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }).setNegativeButton("No",null).show();

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cart_layout, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void AddMoreProducts(View view) {
        startActivity(new Intent(getApplicationContext(),HomeUserActivity.class));
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
