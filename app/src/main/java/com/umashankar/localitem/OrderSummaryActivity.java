package com.umashankar.localitem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.umashankar.localitem.HelperClass.PhpLink;
import com.umashankar.localitem.Model.Cart;
import com.umashankar.localitem.Model.Charges;
import com.umashankar.localitem.Model.Coupon;
import com.umashankar.localitem.Model.MyAddressModel;

import com.umashankar.localitem.ViewHolder.CartViewHolder;
import com.umashankar.localitem.ViewHolder.CouponViewHolder;
import com.umashankar.localitem.ViewHolder.MyAddressViewHolder;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSummaryActivity extends AppCompatActivity implements PaymentResultListener {

    private RecyclerView recyclerView,addressRecyclerView;
    private RecyclerView.LayoutManager layoutManager,addressLayoutManager;
    private Button NextButton,addreessBtn,buttonApply;
    private TextView txtTotalAmt,subTotal,txtTotalProduct,txtDeliveryFee,layoutTotalAmt,layoutTotalText;
    private ImageView imageView;

    public TextView txt_Name, txt_Mobile, txt_City, txt_Address, txt_Landmark, txt_Pincode, txt_State, txt_Type;
    private String Name, AddresssText, City, Pincode, Landmark, State, Mobile, Type;

    private int size= 0;
    private int qty = 0, price=0, oneTypeProductPrice = 0, oneTypeProductCharges = 0 ,Coupons=0, delivery = 0;
    private int OverAllSubTotalPrice = 0,OverAllSubCharges=0,OverAllProductCharges = 0,OverAllTotalPrice =0;

    String pName,pPrice,pQty,fulladdress,pSubTotal,pFinalTotal,ProductOrderAmtBackUP, Ship_price;
    private DatabaseReference adminOrderCount,orderRef,adminListRef,couponReference,couponReferenceNot,oldRef,newRef,chargeRef,newAdminRef,newInvoiceRef;

    private ConstraintLayout priceDetailLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentUser,userEmail,orderStatus;

    private Dialog paymentMethodDialog;
    private ProgressDialog loadingDialog;
    private ConstraintLayout paytmLayout;

    private String orderRandomKey;
    private String date_time, saveCurrentDate, saveCurrentTime;

    private ConstraintLayout couponConstraintLayout,couponLayout,emptyConstraintLayout;
    private TextView txtCouponApplied,txtCouponDetail,txtCouponValue,couponText,afterApplyCouponText,afterCoupon,deliverTextView;
    private ImageView couponCancel;

    private LinearLayout addLayout;
    private ScrollView notEmptyScrollView;
    private RelativeLayout notEmptyRelativeLayout;

    private List<String> productName = new ArrayList<String>();
    private List<Integer> productPrice;
    private List<String> productQ = new ArrayList<String>();
    private String rateFor="",invoiceId="",orderSrNo="";
    private int shipCharge =0,deleteOneTypeProduct=0,deleteOneTypeCharge=0;
    private FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;

    String payNote = "LocaItem Order";
    String name = "LocalItem";
    String razorpayId="";

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override//-----------------------------------------------------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.myOrderSummaryView_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUser = firebaseUser.getUid();
        userEmail = firebaseUser.getEmail();

        //LoadingDialog
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Please wait");
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_bg));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        //Payment Dialog
        paymentMethodDialog = new Dialog(OrderSummaryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_option_layout);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_bg));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paytmLayout = paymentMethodDialog.findViewById(R.id.paymentLayout);
//        paytm = paymentMethodDialog.findViewById(R.id.paytm_btn);
//        cod = paymentMethodDialog.findViewById(R.id.cod_btn);
        addreessBtn = findViewById(R.id.address_BTN);
        //Coupons
        couponReference = FirebaseDatabase.getInstance().getReference().child("CouponsCode").child("Activate");
        couponText = findViewById(R.id.applyCodeText);
        txtCouponApplied = findViewById(R.id.CodeText);
        txtCouponDetail = findViewById(R.id.couponAppliedDetail);
        txtCouponValue = findViewById(R.id.txtCouponValue);
        couponCancel = findViewById(R.id.imageView7);
        afterApplyCouponText = findViewById(R.id.textView37);
        afterCoupon = findViewById(R.id.textView36);
        couponConstraintLayout = findViewById(R.id.couponAppliedLayout);
        couponLayout = findViewById(R.id.constraintLayout2);
        deliverTextView = findViewById(R.id.textView20);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd MMM,yyyy");
        SimpleDateFormat date1 = new SimpleDateFormat("yyMMddHHmmss");
        saveCurrentDate = date.format(calendar.getTime());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime = time.format(calendar.getTime());
        date_time = saveCurrentDate + " | " + saveCurrentTime;

        String ID = date1.format(calendar.getTime());
        orderRandomKey = "LCLITM"+ID;

        adminOrderCount = FirebaseDatabase.getInstance().getReference().child("AdminOrders");
        orderRef = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUser);
        chargeRef = FirebaseDatabase.getInstance().getReference().child("Shipper Data").child("Shipper").child("Professional courier");
        oldRef = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUser).child("CartList");
        newRef = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUser).child("BuyNowList").child(orderRandomKey).child("Products");
        newAdminRef = FirebaseDatabase.getInstance().getReference().child("AdminOrders").child(orderRandomKey).child("Products");
        newInvoiceRef = FirebaseDatabase.getInstance().getReference().child("AdminData").child("Invoice").child(orderRandomKey).child("Products");
        adminListRef = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerOrderList);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextButton = findViewById(R.id.place_order_btn);
        txtTotalAmt = findViewById(R.id.total_price);
        imageView = findViewById(R.id.cart_product_image);
        subTotal = findViewById(R.id.order_sub_Total);
        txtTotalProduct=findViewById(R.id.order_TotalProduct);
        txtDeliveryFee = findViewById(R.id.DeliveryFee);
        priceDetailLayout = findViewById(R.id.linear);
        layoutTotalText = findViewById(R.id.textAmt);
        layoutTotalAmt = findViewById(R.id.layoutTotalAmt);

        txt_Name = findViewById(R.id.user_Name);
        txt_Address = findViewById(R.id.user_Address);
        txt_City = findViewById(R.id.user_City);
        txt_Pincode = findViewById(R.id.user_Pincode);
        txt_Landmark = findViewById(R.id.user_Landmark);
        txt_Mobile = findViewById(R.id.user_Mobile);
        txt_State = findViewById(R.id.user_State);


        notEmptyRelativeLayout = findViewById(R.id.relativ);
        notEmptyScrollView = findViewById(R.id.scrollView2);
        emptyConstraintLayout = findViewById(R.id.empty_layout);

        addLayout = findViewById(R.id.address_linear_layout);
        addLayout.setVisibility(View.GONE);
        couponLayout.setVisibility(View.GONE);
        deliverTextView.setVisibility(View.VISIBLE);
        txtDeliveryFee.setVisibility(View.VISIBLE);
        priceDetailLayout.setVisibility(View.GONE);
        txtTotalAmt.setVisibility(View.GONE);
        afterCoupon.setVisibility(View.GONE);
        afterApplyCouponText.setVisibility(View.GONE);
        emptyConstraintLayout.setVisibility(View.GONE);
        NextButton.setVisibility(View.GONE);
        addreessBtn.setVisibility(View.GONE);
        couponText.setVisibility(View.VISIBLE);

        couponText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCoupons();
            }
        });

        getAddress();

        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(couponLayout.getVisibility() == View.VISIBLE){
                    PlaceOrder();
                }else{
                    Toast.makeText(OrderSummaryActivity.this, "Add Address", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void EditMyAddress(View view) {
        Intent intent = new Intent(OrderSummaryActivity.this, MyAddressBook.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("layout_code", 3);
        intent.putExtra("layout_state", State);
        startActivity(intent);
        finish();
        couponLayout.setVisibility(View.GONE);
        addLayout.setVisibility(View.GONE);
        couponText.setVisibility(View.GONE);
        deliverTextView.setVisibility(View.GONE);
        txtDeliveryFee.setVisibility(View.GONE);
        priceDetailLayout.setVisibility(View.GONE);
        txtTotalAmt.setVisibility(View.GONE);
        NextButton.setVisibility(View.GONE);
    }
    private void getAddress() {
        final Dialog addressDialog = new Dialog(OrderSummaryActivity.this);
        addressDialog.setContentView(R.layout.address_layout);
        addressDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addressDialog.setCancelable(false);

        Button addNew = addressDialog.findViewById(R.id.button_add);
        addNew.setText("Add or Edit Address");
        addressRecyclerView = addressDialog.findViewById(R.id.my_address_book);
        addressLayoutManager = new LinearLayoutManager(this);
        addressRecyclerView.setLayoutManager(addressLayoutManager);
        FirebaseRecyclerOptions<MyAddressModel> options =
                new FirebaseRecyclerOptions.Builder<MyAddressModel>()
                        .setQuery(orderRef.child("Address"),MyAddressModel.class).build();

        FirebaseRecyclerAdapter<MyAddressModel, MyAddressViewHolder> adapter =
                new FirebaseRecyclerAdapter<MyAddressModel, MyAddressViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final MyAddressViewHolder viewHolder, int i, @NonNull final MyAddressModel address) {

                        viewHolder.txtName.setText(address.getName());
                        viewHolder.txtAddress.setText(address.getAddress());
                        viewHolder.txtCity.setText(address.getCity());
                        viewHolder.txtPincode.setText(address.getPincode());
                        viewHolder.txtState.setText(address.getState());
                        viewHolder.txtLandmark.setText(address.getLandmark());
                        viewHolder.txtContact.setText(address.getMobile());
                        viewHolder.SelectBTN.setVisibility(View.VISIBLE);
                        viewHolder.deleteAddress.setVisibility(View.GONE);
                        viewHolder.editAddress.setVisibility(View.GONE);

                        viewHolder.SelectBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(OrderSummaryActivity.this, "State: " + address.getState(), Toast.LENGTH_SHORT).show();
                                txt_Name.setText(address.getName());
                                txt_Address.setText(address.getAddress());
                                txt_City.setText("City: "+address.getCity());
                                txt_State.setText("State: "+address.getState());
                                txt_Pincode.setText("Pincode:"+address.getPincode());
                                txt_Landmark.setText("Landmark: "+address.getLandmark());
                                txt_Mobile.setText("Contact: "+address.getMobile());


                                //Address Details
                                Name =address.getName();
                                AddresssText =address.getAddress();
                                City = address.getCity();
                                State = address.getState();
                                Pincode = address.getPincode();
                                Mobile = address.getMobile();

                                AllCartProduct();

                                fulladdress = "" + address.getAddress() + "\nCity: " + address.getCity() + "\tPincode: " + address.getPincode()+"\nState: "+address.getState() + "\nLandmark: " + address.getLandmark();
                                addressDialog.dismiss();

                                addreessBtn.setText("Change Address");
                                addreessBtn.setVisibility(View.GONE);
                                couponLayout.setVisibility(View.VISIBLE);
                                addLayout.setVisibility(View.VISIBLE);
                                priceDetailLayout.setVisibility(View.GONE);
                                txtTotalAmt.setVisibility(View.VISIBLE);
                                NextButton.setVisibility(View.VISIBLE);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public MyAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_layout,parent,false);
                        MyAddressViewHolder holder = new MyAddressViewHolder(view);
                        return holder;
                    }
                };

        addressRecyclerView.setAdapter(adapter);
        adapter.startListening();

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderSummaryActivity.this, MyAddressBook.class);
                intent.putExtra("layout_code", 3);
                startActivity(intent);
            }
        });

        addressDialog.show();
    }

    private void AllCartProduct() {
        orderRef.child("CartList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    size = (int) dataSnapshot.getChildrenCount();
                    emptyConstraintLayout.setVisibility(View.VISIBLE);
                    notEmptyRelativeLayout.setVisibility(View.GONE);
                    notEmptyScrollView.setVisibility(View.GONE);
                }else{
                    emptyConstraintLayout.setVisibility(View.GONE);
                    notEmptyRelativeLayout.setVisibility(View.VISIBLE);
                    notEmptyScrollView.setVisibility(View.VISIBLE);
                    size = (int) dataSnapshot.getChildrenCount();
                    txtTotalProduct.setText(size + "(Item)");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getOrderData();
    }
    private void getOrderData() {
        loadingDialog.show();
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(orderRef.child("CartList")
                                , Cart.class).build();

        adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, int i, final Cart cart) {

                cartViewHolder.txtProductName.setText(cart.getPname());
                cartViewHolder.txtProductPrice.setText("₹" + cart.getPrice());
                cartViewHolder.txtProductQty.setText("Qty: " + cart.getQuantity());
                Glide.with(getApplicationContext()).load(cart.getImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(cartViewHolder.cartProductImage);

                pName = cart.getPname();
                pPrice = cart.getPrice();
                pQty = cart.getQuantity();

                chargeRef = FirebaseDatabase.getInstance().getReference().child("Shipper Data").child("Shipper").child("Professional courier").child(State);
                chargeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Charges charges = dataSnapshot.getValue(Charges.class);

                            if(State.equals(charges.getState_name())){
                                qty = Integer.valueOf(cart.getQuantity());
                                ArrayList<String> list = new ArrayList();
                                list.add(cart.getRateFor());
                                Log.i("list.size()", String.valueOf(list.size()));
                                Log.i("list", String.valueOf(list));

                                for(int i =0; i < list.size(); i++) {
                                    if (list.get(i).equals("1 Kg")) {
                                        price = Integer.valueOf(cart.getPrice());
                                        oneTypeProductPrice = (price * qty);
                                        cartViewHolder.txtTotalProductPrice.setText("Total: " + oneTypeProductPrice + "/-");
                                        OverAllSubTotalPrice = OverAllSubTotalPrice + oneTypeProductPrice;
                                        subTotal.setText("" + OverAllSubTotalPrice);

                                        if(State.equals("Haryana") && City.toLowerCase().equals("assandh")){
                                            couponText.setVisibility(View.GONE);
                                            priceDetailLayout.setVisibility(View.VISIBLE);
                                            txtDeliveryFee.setText("Free");
                                            OverAllTotalPrice = OverAllSubTotalPrice + 00;
                                            pFinalTotal = String.valueOf(OverAllTotalPrice);
                                            txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
                                            layoutTotalAmt.setText("" + pFinalTotal);
                                            loadingDialog.dismiss();

                                        }else if(State.equals("Haryana") && City.toLowerCase().equals("safidon")){
                                            couponText.setVisibility(View.GONE);
                                            priceDetailLayout.setVisibility(View.VISIBLE);
                                            txtDeliveryFee.setText("Free");
                                            OverAllTotalPrice = OverAllSubTotalPrice + 00;
                                            pFinalTotal = String.valueOf(OverAllTotalPrice);
                                            txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
                                            layoutTotalAmt.setText("" + pFinalTotal);
                                            loadingDialog.dismiss();

                                        }else {
                                            priceDetailLayout.setVisibility(View.VISIBLE);
                                            couponText.setVisibility(View.VISIBLE);
                                            Ship_price = charges.getCharge_one();
                                            shipCharge = Integer.valueOf(Ship_price);
                                            oneTypeProductCharges = shipCharge * qty;
                                            deleteOneTypeCharge = oneTypeProductCharges;

                                            Log.i("deleteOneTypeProduct", String.valueOf(deleteOneTypeProduct));
                                            Log.i("Price list", String.valueOf(productPrice));
                                            Log.i("rateFor", String.valueOf(list.get(i)));
                                            Log.i("qty", String.valueOf(qty));
                                            Log.i("Ship_price", String.valueOf(Ship_price));
                                            Log.i("shipCharge", String.valueOf(shipCharge));
                                            Log.i("oneTypeProductCharges", String.valueOf(oneTypeProductCharges));

                                            AllProductPriceDetails();
                                            loadingDialog.dismiss();
                                        }
                                    } else if (list.get(i).equals("1/2 Kg")) {
                                        price = Integer.valueOf(cart.getPrice());
                                        oneTypeProductPrice = (price * qty);
                                        cartViewHolder.txtTotalProductPrice.setText("Total: " + oneTypeProductPrice + "/-");
                                        OverAllSubTotalPrice = OverAllSubTotalPrice + oneTypeProductPrice;
                                        subTotal.setText("" + OverAllSubTotalPrice);

                                        if(State.equals("Haryana") && City.toLowerCase().equals("assandh")){
                                            priceDetailLayout.setVisibility(View.VISIBLE);
                                            couponText.setVisibility(View.GONE);
                                            txtDeliveryFee.setText("Free");
                                            OverAllTotalPrice = OverAllSubTotalPrice + 00;
                                            pFinalTotal = String.valueOf(OverAllTotalPrice);
                                            txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
                                            layoutTotalAmt.setText("" + pFinalTotal);
                                            loadingDialog.dismiss();
                                        }else if(State.equals("Haryana") && City.toLowerCase().equals("safidon")){
                                            priceDetailLayout.setVisibility(View.VISIBLE);
                                            couponText.setVisibility(View.GONE);
                                            txtDeliveryFee.setText("Free");
                                            OverAllTotalPrice = OverAllSubTotalPrice + 00;
                                            pFinalTotal = String.valueOf(OverAllTotalPrice);
                                            txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
                                            layoutTotalAmt.setText("" + pFinalTotal);
                                            loadingDialog.dismiss();
                                        }else {
                                            priceDetailLayout.setVisibility(View.VISIBLE);
                                            couponText.setVisibility(View.VISIBLE);
                                            Ship_price = charges.getCharge_one();
                                            shipCharge = Integer.valueOf(Ship_price);
                                            oneTypeProductCharges = shipCharge * qty;
                                            deleteOneTypeCharge = oneTypeProductCharges;

                                            Log.i("deleteOneTypeProduct", String.valueOf(deleteOneTypeProduct));
                                            Log.i("Price list", String.valueOf(productPrice));
                                            Log.i("rateFor", String.valueOf(list.get(i)));
                                            Log.i("qty", String.valueOf(qty));
                                            Log.i("Ship_price", String.valueOf(Ship_price));
                                            Log.i("shipCharge", String.valueOf(shipCharge));
                                            Log.i("oneTypeProductCharges", String.valueOf(oneTypeProductCharges));

                                            AllProductPriceDetails();
                                            loadingDialog.dismiss();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                cartViewHolder.cartEdit.setVisibility(View.GONE);
                cartViewHolder.cartItemDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(OrderSummaryActivity.this)
                                .setMessage("Are you sure you want to Delete?")
                                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                orderRef.child("CartList").child(cart.getItemID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if(size!=0){
                                                loadingDialog.show();
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finish();
                                                        overridePendingTransition(0, 0);
                                                        startActivity(getIntent());
                                                        overridePendingTransition(0, 0);
                                                        loadingDialog.dismiss();
                                                        Toast.makeText(OrderSummaryActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, 1000);
                                            }

//                                                    startActivity(new Intent(getApplicationContext(),OrderSummaryActivity.class));
//
//                                                    int deletePrice = Integer.parseInt(cart.getPrice());
//                                                    OverAllSubTotalPrice = OverAllSubTotalPrice - deletePrice;
//                                                    subTotal.setText("" + OverAllSubTotalPrice);
//
//                                                    Log.i("Deleted Product Price", String.valueOf(deletePrice));
//                                                    Log.i("OverAllProductCharges", String.valueOf(OverAllProductCharges));
//                                                    Log.i("deleteOneTypeCharge", String.valueOf(deleteOneTypeCharge));
//                                                    OverAllProductCharges = OverAllProductCharges - deleteOneTypeCharge;
//                                                    Log.i("AfterDelete Charges", String.valueOf(OverAllProductCharges));
//                                                    txtDeliveryFee.setText("" + OverAllProductCharges);
//                                                    OverAllTotalPrice = OverAllSubTotalPrice + OverAllProductCharges;
//
//                                                    pFinalTotal = String.valueOf(OverAllTotalPrice);
//                                                    ProductOrderAmtBackUP = pFinalTotal;
//
//                                                    txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
//                                                    layoutTotalAmt.setText("" + pFinalTotal);


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
    private void AllProductPriceDetails() {

        Log.i("Price list", String.valueOf(productPrice));
        OverAllSubCharges = OverAllSubCharges + oneTypeProductCharges;
        OverAllProductCharges = OverAllSubCharges;
        OverAllTotalPrice = OverAllSubTotalPrice + OverAllProductCharges;

        pSubTotal = String.valueOf(OverAllSubTotalPrice);
        pFinalTotal = String.valueOf(OverAllTotalPrice);
        Log.i("OverAllSubCharges", String.valueOf(OverAllSubCharges));
        Log.i("OverAllTotalPrice", String.valueOf(OverAllTotalPrice));
        Log.i("pSubTotal", String.valueOf(pSubTotal));
        Log.i("pFinalTotal", String.valueOf(pFinalTotal));

        ProductOrderAmtBackUP = pFinalTotal;

        txtDeliveryFee.setText("" + OverAllProductCharges);
        txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
        layoutTotalAmt.setText("" + pFinalTotal);
    }
    private void ShowCoupons() {
        final Dialog couponDialog = new Dialog(OrderSummaryActivity.this);
        couponDialog.setContentView(R.layout.view_coupons);
        couponDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        couponDialog.setTitle("Apply Coupons");
        couponDialog.setCancelable(true);

        couponReference = FirebaseDatabase.getInstance().getReference().child("CouponsCode").child("Activate");
        couponReferenceNot = FirebaseDatabase.getInstance().getReference().child("CouponsCode").child("Not Activate");

        recyclerView = couponDialog.findViewById(R.id.recycler_view_coupon);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(OrderSummaryActivity.this);
        recyclerView.setLayoutManager(layoutManager);



        ImageView cancel = couponDialog.findViewById(R.id.imageCancel);
        final TextView couponTextView = couponDialog.findViewById(R.id.etCoupon);
        buttonApply = couponDialog.findViewById(R.id.buttoApply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponReferenceNot.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            if(dataSnapshot.exists()) {
                                if (child.child("couponCode").getValue().equals(couponTextView.getText().toString())) {
                                    txtCouponValue.setText(child.child("couponValue").getValue() + " Rs. OFF");
                                    txtCouponApplied.setText(child.child("couponCode").getValue() + " Applied");
                                    txtCouponDetail.setText(child.child("description").getValue() + "");
                                    afterApplyCouponText.setText("-" + child.child("couponValue").getValue());
                                    couponDialog.dismiss();

                                    couponConstraintLayout.setVisibility(View.VISIBLE);
                                    afterCoupon.setVisibility(View.VISIBLE);
                                    afterApplyCouponText.setVisibility(View.VISIBLE);
                                    couponText.setVisibility(View.GONE);

                                    Coupons = Integer.parseInt(child.child("couponValue").getValue().toString());

                                    OverAllTotalPrice = OverAllSubTotalPrice + OverAllProductCharges - Coupons;
                                    pSubTotal = String.valueOf(OverAllSubTotalPrice);
                                    pFinalTotal = String.valueOf(OverAllTotalPrice);

                                    subTotal.setText("" + OverAllSubTotalPrice);
                                    txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
                                    layoutTotalAmt.setText("" + pFinalTotal);

                                    couponCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            new AlertDialog.Builder(OrderSummaryActivity.this)
                                                    .setMessage("Are you sure you want to cancel?")
                                                    .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    afterCoupon.setVisibility(View.GONE);
                                                    afterApplyCouponText.setVisibility(View.GONE);
                                                    couponConstraintLayout.setVisibility(View.GONE);
                                                    couponText.setVisibility(View.VISIBLE);
                                                    pFinalTotal = ProductOrderAmtBackUP;
                                                    txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
                                                    layoutTotalAmt.setText("" + pFinalTotal);
                                                }
                                            }).setNegativeButton("No", null).show();
                                        }
                                    });

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {couponDialog.dismiss();}
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponDialog.dismiss();
            }
        });

        FirebaseRecyclerOptions<Coupon> options =
                new FirebaseRecyclerOptions.Builder<Coupon>()
                        .setQuery(couponReference, Coupon.class)
                        .build();

        FirebaseRecyclerAdapter<Coupon, CouponViewHolder> adapter = new FirebaseRecyclerAdapter<Coupon, CouponViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CouponViewHolder couponHolder, int i, @NonNull final Coupon coupon) {
                couponHolder.txtCouponCode.setText(coupon.getCouponCode());
                couponHolder.txtCouponDescription.setText(coupon.getDescription());
                couponHolder.txtCouponexpDate.setText("Exp Date: " + coupon.getEndDate());
                couponHolder.txtCouponValue.setText("₹"+coupon.getCouponValue() + " Off");
                couponHolder.apply.setVisibility(View.VISIBLE);
                couponConstraintLayout.setVisibility(View.GONE);
                couponText.setVisibility(View.VISIBLE);


                couponHolder.apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        couponConstraintLayout.setVisibility(View.VISIBLE);
                        afterCoupon.setVisibility(View.VISIBLE);
                        afterApplyCouponText.setVisibility(View.VISIBLE);
                        couponText.setVisibility(View.GONE);

                        txtCouponValue.setText(coupon.getCouponValue()+" Rs. OFF");
                        txtCouponApplied.setText(coupon.getCouponCode()+" Applied");
                        txtCouponDetail.setText(coupon.getDescription());
                        afterApplyCouponText.setText("-"+coupon.getCouponValue());
                        couponDialog.dismiss();

                        Coupons = Integer.parseInt(coupon.getCouponValue());
                        OverAllTotalPrice = OverAllSubTotalPrice + OverAllProductCharges - Coupons;
                        pSubTotal = String.valueOf(OverAllSubTotalPrice);
                        pFinalTotal = String.valueOf(OverAllTotalPrice);

                        subTotal.setText("" + OverAllSubTotalPrice);
                        txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
                        layoutTotalAmt.setText(""+pFinalTotal);

                        couponCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(OrderSummaryActivity.this)
                                        .setMessage("Are you sure you want to cancel?")
                                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        afterCoupon.setVisibility(View.GONE);
                                        afterApplyCouponText.setVisibility(View.GONE);
                                        couponConstraintLayout.setVisibility(View.GONE);
                                        couponText.setVisibility(View.VISIBLE);
                                        pFinalTotal=ProductOrderAmtBackUP;
                                        txtTotalAmt.setText("Total: " + pFinalTotal + "/-");
                                        layoutTotalAmt.setText(""+pFinalTotal);
                                    }
                                }).setNegativeButton("No",null).show();
                            }
                        });

                    }
                });

                couponHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderSummaryActivity.this, ViewOffersActivity.class);
                        intent.putExtra("offerCode",coupon.getCouponCode());
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_coupon_item_layout, parent,false);
                CouponViewHolder holder = new CouponViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        couponDialog.show();
    }
    // Payments

    private void getInvoiceNo(){
        adminOrderCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    invoiceId = "LCLITEM-APP-2021-001";
                }else{
                    int size = (int) dataSnapshot.getChildrenCount()+1;
                    invoiceId = "LCLITEM-APP-2021-00"+size;
                }
                Log.i("All Invoice",String.valueOf(dataSnapshot.getChildrenCount()));
                Log.i("After Invoice",String.valueOf(invoiceId));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }
    private void getSrNo(){
        orderRef.child("BuyNowList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    orderSrNo = "Order01";
                }else{
                    int size = (int) dataSnapshot.getChildrenCount()+1;
                    orderSrNo = "Order0"+size;
                }

                Log.i("All My Orders",String.valueOf(dataSnapshot.getChildrenCount()));
                Log.i("orderSrNo",String.valueOf(orderSrNo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void PlaceOrder() {
        final Dialog termDialog = new Dialog(OrderSummaryActivity.this);
        termDialog.setContentView(R.layout.order_terms_and_conditions);
        termDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        termDialog.setTitle("Apply Coupons");
        termDialog.setCancelable(true);

        CheckBox checkBox = termDialog.findViewById(R.id.termCheckBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        termDialog.dismiss();
                        paymentMethodDialog.show();
                        paytmLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PayWithRazorPay();
                            }
                        });
                    }
                }, 1000);


            }
        });

        termDialog.show();
    }
    private void PayWithRazorPay() {
        final Checkout co = new Checkout();
        int amount = Math.round(Float.parseFloat(pFinalTotal) * 100);
        try {
            JSONObject options = new JSONObject();
            options.put("name", name);
            options.put("description", payNote);
            options.put("send_sms_hash",true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://cdn.razorpay.com/logos/IJnwt4TchafuXp_medium.png");
            options.put("currency", "INR");
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", userEmail);
            preFill.put("contact", Mobile);

            options.put("prefill", preFill);
            co.open(OrderSummaryActivity.this, options);
        } catch (Exception e) {
            Toast.makeText(OrderSummaryActivity.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String response) {
        try {
            Log.e("PaymentSuccessResponse",  response); //Response: pay_IwdgbYhFxxMxfZ
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
            orderStatus = "Confirm";
            paymentMethodDialog.dismiss();
            int successCode = 12345678;
            razorpayResponse(response, successCode);
            OrderNow();


        } catch (Exception e) {
            Log.e("PaymentResponse", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Log.e("FailedResponse: Code-",  code+"Response-"+response);
            paymentMethodDialog.dismiss();
            if (code == 0){
                orderStatus = "Payment cancelled by user";
                razorpayResponse(response, code);
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Payment Status Pending \nPlease wait for the confirmation", Toast.LENGTH_SHORT).show();
                orderStatus = "Pending";
                razorpayResponse(response, code);
                OrderNow();
            }
        } catch (Exception e) {
            Log.e("PaymentResponse", "Exception in onPaymentError", e);
        }
    }

    private void razorpayResponse(String payResponse, int code) {

        if (orderStatus.equalsIgnoreCase("Confirm")){
            razorpayId = payResponse;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_INSERT_Response,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("razorpayResponse", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderSummaryActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("customerId", ""+firebaseUser.getUid());
                map.put("orderId", orderRandomKey);
                map.put("orderAmount", pFinalTotal);
                map.put("razorpayId", razorpayId);
                map.put("response", payResponse);
                map.put("responseCode", ""+code);
                map.put("status", orderStatus);
                return map;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(OrderSummaryActivity.this);
        requestQueue.add(stringRequest);

    }
    public void AddMoreProducts(View view) {
        startActivity(new Intent(OrderSummaryActivity.this,HomeUserActivity.class));
        finish();
    }

    private void OrderNow() {
        getSrNo();
        getInvoiceNo();

        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", currentUser);
        hashMap.put("orderSr", orderSrNo);
        hashMap.put("invoiceSr", invoiceId);
        hashMap.put("orderid", orderRandomKey);
        hashMap.put("orderStatus",orderStatus);
        hashMap.put("email", userEmail);

        //product Detail
        hashMap.put("buyType","cart");
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("order_date_time", date_time);

        hashMap.put("product_name", pName);

        hashMap.put("couponValue",txtCouponValue.getText().toString());
        hashMap.put("couponText",txtCouponApplied.getText().toString());
        hashMap.put("couponDetails",txtCouponDetail.getText().toString());
        hashMap.put("afterCouponValue",afterApplyCouponText.getText().toString());

        hashMap.put("trackId","");
        hashMap.put("otracking_id","");
        hashMap.put("oTrackComp","");
        hashMap.put("shipping_id","");
        hashMap.put("oplaced","");
        hashMap.put("opacked","");
        hashMap.put("oshipped","");
        hashMap.put("odelivered","");
        hashMap.put("placedDate","");
        hashMap.put("packedDate","");
        hashMap.put("shippedDate","");
        hashMap.put("deliverDate","");

        //Address Details
        hashMap.put("fulladdress", fulladdress);
        hashMap.put("name", Name);
        hashMap.put("address", AddresssText);
        hashMap.put("city", City);
        hashMap.put("state", State);
        hashMap.put("pincode", Pincode);
        hashMap.put("contact", Mobile);

        //Price Details
        hashMap.put("pTotalPrice", pSubTotal);
        hashMap.put("delivery", txtDeliveryFee.getText().toString());
        hashMap.put("finalTotal", pFinalTotal);
        Log.i("Final Data",String.valueOf(hashMap));
        orderRef.child("BuyNowList").child(orderRandomKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ChangePath(oldRef,newRef);
                    ChangeAdminPath(oldRef,newAdminRef);
                    ChangeInvoicePath(oldRef,newInvoiceRef);
                    adminListRef.child("AdminOrders").child(orderRandomKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            adminListRef.child("AdminData").child("Invoice").child(orderRandomKey).setValue(hashMap);
                            orderRef.child("CartList").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (orderStatus.equalsIgnoreCase("Cancelled")){
                                            Log.i("Invoice Id ",String.valueOf(invoiceId));
                                            Log.i("orderSrNo ",String.valueOf(orderSrNo));
                                            Intent intent = new Intent(getApplicationContext(), MyOrdersActivity.class);
                                            intent.putExtra("oid", orderRandomKey);
                                            startActivity(intent);
                                            finish();
                                        }else if(orderStatus.equalsIgnoreCase("Confirm")){
                                            sendConfirmNotification();
                                            Toast.makeText(OrderSummaryActivity.this, "Order Placed.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), MyOrdersActivity.class);
                                            intent.putExtra("oid", orderRandomKey);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }

        });

    }

    //ToSendNotification
    private void sendConfirmNotification() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_sendNotification,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("pushNotify", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderSummaryActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("title", "New Order");
                map.put("message", "Please check it now");
                return map;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(OrderSummaryActivity.this);
        requestQueue.add(stringRequest);
    }

    private void ChangeAdminPath(final DatabaseReference oldRef, final DatabaseReference newAdminRef) {
        oldRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newAdminRef.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void ChangeInvoicePath(final DatabaseReference oldRef, final DatabaseReference newInvoiceRef) {
        oldRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newInvoiceRef.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void ChangePath(final DatabaseReference oldRef, final DatabaseReference newRef) {
        oldRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newRef.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }



    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),HomeUserActivity.class));
        finish();
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
