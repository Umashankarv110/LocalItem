package com.umashankar.localitem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.umashankar.localitem.HelperClass.FileUtils;
import com.umashankar.localitem.Model.Cart;
import com.umashankar.localitem.Model.Order;
import com.umashankar.localitem.ViewHolder.OrderProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.umashankar.localitem.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.umashankar.localitem.permission.PermissionsChecker.REQUIRED_PERMISSION;

import com.umashankar.localitem.permission.PermissionsActivity;
import com.umashankar.localitem.permission.PermissionsChecker;

public class ViewOrderDetailsActivity extends AppCompatActivity {
    Context mContext;
    PermissionsChecker checker;

    private String orderID = "";
    private DatabaseReference buyListRef,productRef;
    private ConstraintLayout shiipingLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentUser;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private LinearLayout cartItemLayout,checkoutLayout, invoiceLayout;

    Context context;
    private TextView orderId,orderDate,name,address,state,landmark,pincode,contact,totalProduct,subTotal,deliveryFee,orderTotalPrice;

    private TextView placedDate,packedDate,shipedDate,deliverDate,orderState;
    private CheckBox placedCheckBox,packedCheckBox,shippedCheckBox,deliveredCheckBox;

    private ImageView orderImage;

    private String orderPlaced="",orderPacked="",orderShipped="",orderDelivered="";
    private String placedD="",packedD="",shippedD="",deliverD="",pSubTotal="", pSubQty;

    private String printSrNo="", printProductName="", printProductPrice="", printQty="";

    int finalSubTotal, finalPQty;

    private static final String TAG = "PdfCreatorActivity";
    private String invoice,pdfName;

    private Bitmap bmp,scaledBmp;
    private int pageWidth = 1200;

    private Date dateObject;
    private DateFormat dateFormat;

    private LinearLayout linearLayout;
    private Snackbar snackbar;

    private ConstraintLayout couponConstraintLayout,priceConstraintLayout;
    private RelativeLayout nxtRelativeLayout;
    private TextView txtCouponApplied,txtCouponDetail,txtCouponValue,couponText,afterApplyCouponText, afterCoupon,buttonApply;
    private ImageView couponCancel;

    private List<String> pId = new ArrayList<String>();
    private List<String> pName = new ArrayList<String>();
    private List<String> pPrice = new ArrayList<String>();
    private List<String> pQty = new ArrayList<String>();
    private List<String> pSubPrice = new ArrayList<String>();


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_details);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.myOrderView_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();
        checker = new PermissionsChecker(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUser = firebaseUser.getUid();

        cartItemLayout = findViewById(R.id.cartLinearLayout);
        checkoutLayout = findViewById(R.id.checkOutLayout);

        orderID = getIntent().getStringExtra("oid");
        orderId = findViewById(R.id.orderDetailId);
        orderDate = findViewById(R.id.oderDetailDate);
        orderImage = findViewById(R.id.orderDetailImg);
        name = findViewById(R.id.address_fullname);
        address = findViewById(R.id.orderAddress);
        state = findViewById(R.id.orderState);
        pincode = findViewById(R.id.orderPincode);
        contact = findViewById(R.id.orderContact);
        landmark = findViewById(R.id.orderLandmark);
        totalProduct = findViewById(R.id.order_TotalItem);
        subTotal = findViewById(R.id.orderSubTotal);
        deliveryFee = findViewById(R.id.orderDeliveryFee);
        orderTotalPrice = findViewById(R.id.orderTotal);
        linearLayout = findViewById(R.id.orderView);
        invoiceLayout = findViewById(R.id.invoiceLayout);

        orderState = findViewById(R.id.textView122);
        placedDate = findViewById(R.id.tvPlacedDateTime);
        packedDate = findViewById(R.id.tvPackedDateTime);
        shipedDate = findViewById(R.id.tvShippedDateTime);
        deliverDate = findViewById(R.id.tvDeliveredDateTime);
        placedCheckBox = findViewById(R.id.cbPlacedOrder);
        packedCheckBox = findViewById(R.id.cbPacked);
        shippedCheckBox = findViewById(R.id.cbShipped);
        deliveredCheckBox = findViewById(R.id.cbDelivered);
        shiipingLayout = findViewById(R.id.shippingDetailsLayout);


        couponText = findViewById(R.id.applyCodeText);
        txtCouponApplied = findViewById(R.id.CodeText);
        txtCouponDetail = findViewById(R.id.couponAppliedDetail);
        txtCouponValue = findViewById(R.id.txtCouponValue);
        couponCancel = findViewById(R.id.imageView7);
        afterApplyCouponText = findViewById(R.id.textViewAppliedValue);
        afterCoupon = findViewById(R.id.textViewApplied);
        nxtRelativeLayout = findViewById(R.id.relativ);
//        priceConstraintLayout = findViewById(R.id.constraintLayout5);
        couponConstraintLayout = findViewById(R.id.couponAppliedLayout);

        buyListRef = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUser);

        recyclerView = findViewById(R.id.orderDetailRecy);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        placedCheckBox.setClickable(false);
        packedCheckBox.setClickable(false);
        shippedCheckBox.setClickable(false);
        deliveredCheckBox.setClickable(false);
        invoiceLayout.setVisibility(View.GONE);
        couponText.setVisibility(View.GONE);
        couponConstraintLayout.setVisibility(View.GONE);

        //Invoice
        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
        scaledBmp = Bitmap.createScaledBitmap(bmp,260,120,false);

        getRecyclerProductData();
        getOrderDetails(orderID);
    }

    private void getRecyclerProductData() {
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(buyListRef.child("BuyNowList").child(orderID).child("Products"),
                                Cart.class).build();

        FirebaseRecyclerAdapter<Cart, OrderProductViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, OrderProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderProductViewHolder orderProductViewHolder, int i, @NonNull Cart cart) {
                orderProductViewHolder.txtProductName.setText(cart.getPname());
                orderProductViewHolder.txtProductPrice.setText("₹" + cart.getPrice());
                orderProductViewHolder.txtProductQty.setText("Qty: " + cart.getQuantity());

                Glide.with(getApplicationContext()).load(cart.getImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(orderProductViewHolder.cartProductImage);

                Log.i(cart.getPname(), "Qty:"+cart.getQuantity()+" | Price: "+cart.getPrice());

                String count = 1+i+".";
                printSrNo = printSrNo+"\n"+count;
                Log.i("printSrNo---------",printSrNo);

                pId.add(count);
                pName.add(cart.getPname());
                pPrice.add(cart.getPrice());
                pQty.add(cart.getQuantity());

                int pSubTotal = Integer.parseInt(cart.getPrice()) * Integer.parseInt(cart.getQuantity());
                pSubPrice.add(""+pSubTotal);

                String name = cart.getPname();
                printProductName = printProductName +"\n"+ name;
                Log.i("ProductName---------",printProductName);

                String pPrice = cart.getPrice();
                printProductPrice = printProductPrice +"\n"+ pPrice;
                Log.i("ProductPrice---------",printProductPrice);
                finalSubTotal = finalSubTotal + Integer.parseInt(pPrice);
                subTotal.setText(""+finalSubTotal);

                String pQty = cart.getQuantity();
                printQty = printQty +"\n"+ pQty;
                Log.i("ProductQty---------",printQty);
                finalPQty = finalPQty + Integer.parseInt(pQty);
                totalProduct.setText(finalPQty+"(Item)");

            }

            @NonNull
            @Override
            public OrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_product_layout,parent,false);
                OrderProductViewHolder holder = new OrderProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void getOrderDetails(final String orderID) {
        buyListRef.child("BuyNowList").child(orderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Order orderData = dataSnapshot.getValue(Order.class);

                orderId.setText("Order id: "+orderID);

                Log.i("Receipt: ", ""+orderData.getInvoice());
                Log.i("ShipComp: ", ""+orderData.getoTrackComp());

                invoice = "LCLITEM/APP/2022/00"+orderData.getInvoice();
                pdfName = "LCLITEM-APP-2022-"+orderID;

                if(orderData.getOrderStatus().equalsIgnoreCase("Cancelled")){
                    shiipingLayout.setVisibility(View.GONE);
                    invoiceLayout.setVisibility(View.GONE);
                    orderState.setText("Order\n"+orderData.getOrderStatus());
                    orderState.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }

                if (orderData.getOplaced()!=""){
                    if (orderData.getOrderStatus().equalsIgnoreCase("Confirm")){
                        orderState.setText("Order\n"+orderData.getOrderStatus());
                        orderState.setTextColor(getResources().getColor(R.color.successGreen));
                        placedCheckBox.setChecked(true);
                        placedCheckBox.setClickable(false);
                        placedDate.setVisibility(View.VISIBLE);
                        placedDate.setText(orderData.getOrder_date_time());
                    }
                }else{
                    placedCheckBox.setChecked(false);
                }

                if (orderData.getOpacked() != ""){
                    if (orderData.getOpacked().equalsIgnoreCase("Packed")){
                        packedCheckBox.setChecked(true);
                        packedCheckBox.setClickable(false);
                        packedDate.setVisibility(View.VISIBLE);
                        packedDate.setText(orderData.getPackedDate());
                    }
                } else if(orderData.getOpacked() != ""){
                    packedCheckBox.setChecked(false);
                }

                if (orderData.getOshipped() != ""){
                    if (orderData.getOshipped().equalsIgnoreCase("Shipped")){
                        shippedCheckBox.setChecked(true);
                        shippedCheckBox.setClickable(false);
                        shipedDate.setVisibility(View.VISIBLE);
                        if (orderData.getoTrackComp() != "") {
                            shipedDate.setText(orderData.getShippedDate() + "\nShipped By: " + orderData.getoTrackComp()+"\nDocket Number: "+orderData.getOtracking_id());
                        }else{
                            shipedDate.setText(orderData.getShippedDate());
                        }
                    }
                }else if(orderData.getOshipped() == ""){
                    shippedCheckBox.setChecked(false);
                }

                if (orderData.getOdelivered() != ""){
                    if (orderData.getOdelivered().equalsIgnoreCase("Delivered")){
                        deliveredCheckBox.setChecked(true);
                        deliverDate.setVisibility(View.VISIBLE);
                        invoiceLayout.setVisibility(View.VISIBLE);
                        deliverDate.setText(orderData.getDeliverDate());

                        placedCheckBox.setClickable(false);
                        packedCheckBox.setClickable(false);
                        shippedCheckBox.setClickable(false);
                        deliveredCheckBox.setClickable(false);
                    }
                }else if (orderData.getOdelivered() == ""){
                    deliveredCheckBox.setChecked(false);
                }

                if (!orderData.getCouponText().equals("Not Applied")){
                    couponConstraintLayout.setVisibility(View.VISIBLE);
                    txtCouponApplied.setText(orderData.getCouponText());
                    txtCouponValue.setText(orderData.getCouponValue());
                    txtCouponDetail.setText(orderData.getCouponDetails());
                    afterCoupon.setVisibility(View.VISIBLE);
                    couponCancel.setVisibility(View.GONE);
                    afterApplyCouponText.setVisibility(View.VISIBLE);
                    afterApplyCouponText.setText(orderData.getAfterCouponValue());
                }else{
                    couponConstraintLayout.setVisibility(View.GONE);
                    afterApplyCouponText.setVisibility(View.GONE);
                    afterCoupon.setVisibility(View.GONE);
                }

                orderDate.setText("Order on: "+orderData.getDate());
                name.setText(orderData.getName());
                contact.setText("Contact: "+orderData.getContact());
                address.setText(orderData.getAddress());
                state.setText("State: "+orderData.getState());
                pincode.setText("Pincode: "+orderData.getPincode());


                deliveryFee.setText(orderData.getDelivery());
                orderTotalPrice.setText(orderData.getFinalTotal()+"/-");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void DownloadReceipt(View view) {
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            PermissionsActivity.startActivityForResult(ViewOrderDetailsActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
        }else {
            dateObject = new Date();
            PdfDocument myPdfDocument = new PdfDocument();
            Paint myPaint = new Paint();
            Paint titlePaint = new Paint();

            PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
            PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
            Canvas canvas = myPage1.getCanvas();

            canvas.drawBitmap(scaledBmp, 40, 40, myPaint);   //for Image

            canvas.drawLine(20, 160, pageWidth - 20, 160, myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(50);
            titlePaint.setColor(Color.BLACK);
            canvas.drawText("RECEIPT", pageWidth / 2, 220, titlePaint);

            titlePaint.setTextAlign(Paint.Align.LEFT);
            titlePaint.setTextSize(30f);
            titlePaint.setColor(Color.BLACK);
            canvas.drawText("From, ", 50, 300, titlePaint);
            myPaint.setTextSize(25f);
            canvas.drawText("LocalItem", 100, 340, myPaint);

            titlePaint.setTextAlign(Paint.Align.LEFT);
            titlePaint.setTextSize(30f);
            titlePaint.setColor(Color.BLACK);
            canvas.drawText("Deliver To, ", 50, 420, titlePaint);

            myPaint.setTextSize(25f);
            canvas.drawText(name.getText().toString(), 100, 460, myPaint);
            canvas.drawText("Address : " + address.getText().toString(), 100, 500, myPaint);
            canvas.drawText("City : " + address.getText().toString() + " Pincode : " + pincode.getText().toString(), 100, 540, myPaint);
            canvas.drawText(state.getText().toString(), 100, 580, myPaint);
            canvas.drawText(contact.getText().toString(), 100, 620, myPaint);

            dateFormat = new SimpleDateFormat("dd/mm/yyyy");

            myPaint.setTextAlign(Paint.Align.RIGHT);
            myPaint.setColor(Color.rgb(0, 113, 188));
            myPaint.setTextSize(28f);
            canvas.drawText("Order Id: " + orderID, pageWidth - 20, 340, myPaint);
            canvas.drawText("" + orderDate.getText().toString(), pageWidth - 140, 380, myPaint);

            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(2);
            canvas.drawRect(20, 810, pageWidth - 20, 860, myPaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setStyle(Paint.Style.FILL);
            canvas.drawText("Sr. No. ", 40, 845, myPaint);
            canvas.drawText("Item Descriptiom ", 200, 845, myPaint);
            canvas.drawText("Price", 700, 845, myPaint);
            canvas.drawText("Qty. ", 900, 845, myPaint);
            canvas.drawText("Total ", 1050, 845, myPaint);

            canvas.drawLine(180, 820, 180, 855, myPaint);
            canvas.drawLine(680, 820, 680, 855, myPaint);
            canvas.drawLine(880, 820, 880, 855, myPaint);
            canvas.drawLine(1030, 820, 1030, 855, myPaint);

            for (int i = 0; i < pId.size(); i++) {
                Log.i("ProductSize", "" + pId.size());
                if (pId.get(i).equalsIgnoreCase("1.")) {
                    canvas.drawText(pId.get(i), 40, 910, myPaint);
                    canvas.drawText(pName.get(i), 200, 910, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 910, myPaint);
                    canvas.drawText(pQty.get(i), 900, 910, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 910, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                } else if (pId.get(i).equalsIgnoreCase("2.")) {
                    canvas.drawText(pId.get(i), 40, 940, myPaint);
                    canvas.drawText(pName.get(i), 200, 940, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 940, myPaint);
                    canvas.drawText(pQty.get(i), 900, 940, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 940, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                } else if (pId.get(i).equalsIgnoreCase("3.")) {
                    canvas.drawText(pName.get(i), 200, 970, myPaint);
                    canvas.drawText(pId.get(i), 40, 970, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 970, myPaint);
                    canvas.drawText(pQty.get(i), 900, 970, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 970, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                } else if (pId.get(i).equalsIgnoreCase("4.")) {
                    canvas.drawText(pName.get(i), 200, 1000, myPaint);
                    canvas.drawText(pId.get(i), 40, 1000, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 1000, myPaint);
                    canvas.drawText(pQty.get(i), 900, 1000, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 1000, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                } else if (pId.get(i).equalsIgnoreCase("5.")) {
                    canvas.drawText(pName.get(i), 200, 1030, myPaint);
                    canvas.drawText(pId.get(i), 40, 1030, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 1030, myPaint);
                    canvas.drawText(pQty.get(i), 900, 1030, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 1030, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                } else if (pId.get(i).equalsIgnoreCase("6.")) {
                    canvas.drawText(pName.get(i), 200, 1060, myPaint);
                    canvas.drawText(pId.get(i), 40, 1060, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 1060, myPaint);
                    canvas.drawText(pQty.get(i), 900, 1060, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 1060, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                } else if (pId.get(i).equalsIgnoreCase("7.")) {
                    canvas.drawText(pName.get(i), 200, 1090, myPaint);
                    canvas.drawText(pId.get(i), 40, 1090, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 1090, myPaint);
                    canvas.drawText(pQty.get(i), 900, 1090, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 1090, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                } else if (pId.get(i).equalsIgnoreCase("8.")) {
                    canvas.drawText(pName.get(i), 200, 1120, myPaint);
                    canvas.drawText(pId.get(i), 40, 1120, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 1120, myPaint);
                    canvas.drawText(pQty.get(i), 900, 1120, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 1120, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                } else if (pId.get(i).equalsIgnoreCase("9.")) {
                    canvas.drawText(pName.get(i), 200, 1150, myPaint);
                    canvas.drawText(pId.get(i), 40, 1150, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 1150, myPaint);
                    canvas.drawText(pQty.get(i), 900, 1150, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 1150, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                } else if (pId.get(i).equalsIgnoreCase("10.")) {
                    canvas.drawText(pName.get(i), 200, 1180, myPaint);
                    canvas.drawText(pId.get(i), 40, 1180, myPaint);
                    canvas.drawText(pPrice.get(i), 700, 1180, myPaint);
                    canvas.drawText(pQty.get(i), 900, 1180, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(pSubPrice.get(i), pageWidth - 40, 1180, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                }
            }

            if (pId.size() <= 5) {
                canvas.drawLine(800, 1070, pageWidth - 20, 1070, myPaint);
                canvas.drawText("Sub-Total ", 850, 1110, myPaint);
                canvas.drawText(":", pageWidth - 190, 1110, myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(String.valueOf(subTotal.getText()), pageWidth - 40, 1110, myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);

                canvas.drawText("Delivery Fee", 850, 1145, myPaint);
                canvas.drawText(":", pageWidth - 190, 1145, myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(deliveryFee.getText().toString(), pageWidth - 40, 1145, myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);

                canvas.drawLine(800, 1160, pageWidth - 20, 1160, myPaint);

                myPaint.setColor(Color.BLACK);
                myPaint.setTextSize(28f);
                myPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Total", 850, 1190, myPaint);
                canvas.drawText(":", pageWidth - 190, 1190, myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(orderTotalPrice.getText().toString(), pageWidth - 40, 1190, myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);

                myPaint.setStyle(Paint.Style.STROKE);
                myPaint.setStrokeWidth(2);
                canvas.drawRect(40, 1300, pageWidth - 40, 1370, myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);
                myPaint.setStyle(Paint.Style.FILL);
                canvas.drawText("               Comments: Thanks for ordering with us, and we hope to see you soon! ", 40, 1340, myPaint);

            } else {

                canvas.drawLine(800, 1200, pageWidth - 20, 1200, myPaint);
                canvas.drawText("Sub-Total ", 850, 1230, myPaint);
                canvas.drawText(":", pageWidth - 190, 1230, myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(String.valueOf(subTotal.getText()), pageWidth - 40, 1230, myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);

                canvas.drawText("Delivery Fee", 850, 1270, myPaint);
                canvas.drawText(":", pageWidth - 190, 1270, myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(deliveryFee.getText().toString(), pageWidth - 40, 1270, myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);

                canvas.drawLine(800, 1290, pageWidth - 20, 1290, myPaint);

                myPaint.setColor(Color.BLACK);
                myPaint.setTextSize(28f);
                myPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Total", 850, 1320, myPaint);
                canvas.drawText(":", pageWidth - 190, 1320, myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(orderTotalPrice.getText().toString(), pageWidth - 40, 1320, myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);

                myPaint.setStyle(Paint.Style.STROKE);
                myPaint.setStrokeWidth(2);
                canvas.drawRect(40, 1380, pageWidth - 40, 1460, myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);
                myPaint.setStyle(Paint.Style.FILL);
                canvas.drawText("               Comments: Thanks for ordering with us, and we hope to see you soon! ", 40, 1425, myPaint);

            }
            canvas.drawLine(20, 1860, pageWidth - 20, 1860, myPaint);
//            canvas.drawText("Address :", 40, 1890, myPaint);
            canvas.drawText("", 40, 1925, myPaint);
            canvas.drawText("", 20, 1930, myPaint);


            myPdfDocument.finishPage(myPage1);

            String path1 = Environment.getExternalStorageDirectory() + "/Documents/LocalItem";
            File dir = new File(path1);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, "Receipt-" + pdfName + ".pdf");
            Toast.makeText(getApplicationContext(), "Location: " + file.getPath(), Toast.LENGTH_SHORT).show();

            try {
                myPdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }

            myPdfDocument.close();//To Close
            OpenPDF();
        }
    }

    private void OpenPDF() {
        snackbar = Snackbar.make(linearLayout,"Receipt Downloaded",Snackbar.LENGTH_INDEFINITE)
                .setAction("View", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/LocalItem/"+"Receipt-"+pdfName+".pdf";
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FileUtils.openFile(ViewOrderDetailsActivity.this, new File(url));
                                } catch (Exception e) {
                                    Log.d("TAG", "run: ERror");
                                }
                            }
                        }, 1000);
                    }
                });
        snackbar.setDuration(5000);
        snackbar.show();
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
