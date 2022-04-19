package com.umashankar.localitem;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;


public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private ProgressDialog progressDialog;

    String payNote = "LocaItem Order";
    String payAmount= "1";
    String userEmail= "umashankarv110@gmail.com";
    String userMobile= "9820481464";
    String name = "Kirat Broadbands";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

    }

    public void BillPayment(View view) {
        final Checkout co = new Checkout();
        int amount = Math.round(Float.parseFloat(payAmount) * 100);
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
            preFill.put("contact", userMobile);

            options.put("prefill", preFill);

            co.open(PaymentActivity.this, options);
        } catch (Exception e) {
            Toast.makeText(PaymentActivity.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        try {
            Toast.makeText(this, "Payment Successful: " + s, Toast.LENGTH_SHORT).show();
            Log.e("PaymentSuccessResponse",  s);
        } catch (Exception e) {
            Log.e("PaymentResponse", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Log.e("PaymentfailedResponse",  code+""+response);
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("PaymentResponse", "Exception in onPaymentError", e);
        }
    }
}