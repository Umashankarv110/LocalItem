package com.umashankar.localitem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.umashankar.localitem.Model.DataObj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    private TableLayout mTableLayout;
    private List<List<String>> dataLists = new ArrayList<List<String>>();
    private List<String> row;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("record");

    Button saveAndPrint, print;
    EditText etName, etQty;
    Spinner spinner;

    long invoiceNo = 0;
    String[] itemList;
    double[] itemPrice;
    ArrayAdapter<String> adapter;

    DataObj dataObj = new DataObj();

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");

    long invoiceNum, databaseDate;
    double fuelQty, amount;
    String customerName, fuelType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        saveAndPrint = findViewById(R.id.btnSaveAndPrint);
        print = findViewById(R.id.btnPrint);
        saveAndPrint = findViewById(R.id.btnSaveAndPrint);
        etName = findViewById(R.id.editTextName);
        etQty = findViewById(R.id.editTextQty);
        spinner = findViewById(R.id.spinner);

        mTableLayout = (TableLayout) findViewById(R.id.tableCustomers);
        mTableLayout.setStretchAllColumns(true);

        itemList = new String[]{"Petrol", "Diesel"};
        itemPrice = new double[]{72.00,52.00};

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemList);
        spinner.setAdapter(adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invoiceNo = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveAndPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dataObj.invoiceNo = invoiceNo + 1;

                dataObj.customerName = String.valueOf(etName.getText());
                dataObj.date = new Date().getTime();
                dataObj.fuelType = spinner.getSelectedItem().toString();
                dataObj.fuleQty = Double.parseDouble(String.valueOf(etQty.getText()));
                dataObj.amount = Double.parseDouble(decimalFormat.format(dataObj.getFuleQty() * itemPrice[spinner.getSelectedItemPosition()]));

                myRef.child(String.valueOf(invoiceNo + 1)).setValue(dataObj);

                printPdf();

            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
        loadTable();

    }

    private void loadTable() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    List<String> temp = new ArrayList<String>();//[0id,1invoiceNo,2customerName,3date,4Time,5amount]
                    temp.add(" "+dataSnapshot.child("invoiceNo").getValue()+" ");
                    temp.add(" "+dataSnapshot.child("customerName").getValue()+" ");
                    temp.add(" "+simpleDateFormat1.format(dataSnapshot.child("date").getValue())+" ");
                    temp.add(" "+simpleTimeFormat.format(dataSnapshot.child("date").getValue())+" ");
                    temp.add(" "+dataSnapshot.child("amount").getValue()+" ");
                    dataLists.add(temp);
                }
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void printPdf() {
        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint forLinePaint = new Paint();

        PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(250, 350, 1).create();
        PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
        Canvas canvas = myPage1.getCanvas();

        paint.setTextSize(15.5f);
        paint.setColor(Color.rgb(0,50,250));

        canvas.drawText("Umashankar Vishwakarma",20,20, paint);
        paint.setTextSize(8.5f);
        canvas.drawText("G04 Shree tirupati balaji",20,40, paint);
        canvas.drawText("Sahar Road",20,55, paint);

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{6,5},0));
        forLinePaint.setStrokeWidth(2);
        canvas.drawLine(20,65,230,65, forLinePaint);

        canvas.drawText("Customer Name: "+etName.getText(), 20,80, paint);
        canvas.drawLine(20,90,230,90, forLinePaint);

        canvas.drawText("Purchase: ", 20,105, paint);


        canvas.drawText(spinner.getSelectedItem().toString(), 20,135, paint);
        canvas.drawText(etQty.getText()+" ltr", 120,135, paint);

        double amount = itemPrice[spinner.getSelectedItemPosition()] * Double.parseDouble(etQty.getText().toString());
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(decimalFormat.format(amount)), 230,135,paint);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("+%", 20,175, paint);
        canvas.drawText("Tax 5%", 120,175, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(amount * 5/100), 230,175,paint);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawLine(20,210,230,210, forLinePaint);

        paint.setTextSize(10f);
        canvas.drawText("Total ", 120,225, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format((amount * 5/100)+amount), 230,225,paint);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(8.5f);

        canvas.drawText("Date: "+simpleDateFormat.format(new Date().getTime()), 20,260, paint);

        canvas.drawText(String.valueOf(invoiceNo+1), 20,275, paint);
        canvas.drawText("Payment Method: Cash", 20,290, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);
        canvas.drawText("Thank You! ", canvas.getWidth()/2,320, paint);

        myPdfDocument.finishPage(myPage1);
        File file = new File(this.getExternalFilesDir("/"), "Invoice1.pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        }catch (IOException e){
            e.printStackTrace();
        }

        myPdfDocument.close();
    }

    private void loadData() {
        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin = 0;
        int textSize = 0, smallTextSize =0, mediumTextSize = 0;


        textSize = (int) getResources().getDimension(R.dimen.font_size_verysmall);
        smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);
        mediumTextSize = (int) getResources().getDimension(R.dimen.font_size_medium);

        int rows = dataLists.size();
        TextView textSpacer = null;

        mTableLayout.removeAllViews();

        for(int i = -1; i < rows; i ++) {

            if (i > -1)
                row = dataLists.get(i);
            else {
                textSpacer = new TextView(InvoiceActivity.this);
                textSpacer.setText("");
            }

            // dataLists columns
            final TextView tv = new TextView(InvoiceActivity.this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.LEFT);
            tv.setPadding(10, 15, 0, 15);
            if (i == -1) {
                tv.setText("   SrNo.   ");
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv.setTextColor(Color.parseColor("#000000"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setTextColor(Color.parseColor("#000000"));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setText(String.valueOf(row.get(0)));//[0(Date),1(Quantity),2(Burfi),3(Paneer),4(Dahi)]
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            final TextView tv3 = new TextView(InvoiceActivity.this);
            if (i == -1) {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv3.setGravity(Gravity.LEFT);

            tv3.setPadding(10, 15, 0, 15);
            if (i == -1) {
                tv3.setText("     Customer Name     ");
                tv3.setGravity(Gravity.CENTER_HORIZONTAL);
                tv3.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv3.setTextColor(Color.parseColor("#000000"));
            }else {
                tv3.setBackgroundColor(Color.parseColor("#ffffff"));
                tv3.setTextColor(Color.parseColor("#000000"));
                tv3.setGravity(Gravity.CENTER_HORIZONTAL);
                tv3.setText(String.valueOf(row.get(1)));//[0(Date),1(Quantity),2(Burfi),3(Paneer),4(Dahi)]
            }

            final TextView tv4 = new TextView(InvoiceActivity.this);
            if (i == -1) {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv4.setGravity(Gravity.LEFT);

            tv4.setPadding(10, 15, 0, 15);
            if (i == -1) {
                tv4.setText("     Date     ");
                tv4.setGravity(Gravity.CENTER_HORIZONTAL);
                tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv4.setTextColor(Color.parseColor("#000000"));
            }else {
                tv4.setBackgroundColor(Color.parseColor("#ffffff"));
                tv4.setTextColor(Color.parseColor("#000000"));
                tv4.setGravity(Gravity.CENTER_HORIZONTAL);
                tv4.setText(String.valueOf(row.get(2)));//[0(Date),1(Quantity),2(Burfi),3(Paneer),4(Dahi)]
            }

            final TextView tv5 = new TextView(InvoiceActivity.this);
            if (i == -1) {
                tv5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tv5.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                tv5.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv5.setGravity(Gravity.LEFT);

            tv5.setPadding(10, 15, 0, 15);
            if (i == -1) {
                tv5.setText("     Time     ");
                tv5.setGravity(Gravity.CENTER_HORIZONTAL);
                tv5.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv5.setTextColor(Color.parseColor("#000000"));
            }else {
                tv5.setBackgroundColor(Color.parseColor("#ffffff"));
                tv5.setTextColor(Color.parseColor("#000000"));
                tv5.setGravity(Gravity.CENTER_HORIZONTAL);
                tv5.setText(String.valueOf(row.get(3)));//[0(Date),1(Quantity),2(Burfi),3(Paneer),4(Dahi)]
            }

            final TextView tv6 = new TextView(InvoiceActivity.this);
            if (i == -1) {
                tv6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv6.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv6.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv6.setGravity(Gravity.LEFT);

            tv6.setPadding(10, 15, 0, 15);
            if (i == -1) {
                tv6.setText("     Amount     ");
                tv6.setGravity(Gravity.CENTER_HORIZONTAL);
                tv6.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv6.setTextColor(Color.parseColor("#000000"));
            }else {
                tv6.setBackgroundColor(Color.parseColor("#ffffff"));
                tv6.setTextColor(Color.parseColor("#000000"));
                tv6.setGravity(Gravity.CENTER_HORIZONTAL);
                tv6.setText(String.valueOf(row.get(4)));//[0D,1Q,2B,3P,4Da]
            }

            final TableRow tr = new TableRow(InvoiceActivity.this);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0,0,0,0);
            tr.setLayoutParams(trParams);

            tr.addView(tv);
            tr.addView(tv3);
            tr.addView(tv4);
            tr.addView(tv5);
            tr.addView(tv6);

            if (i > -1) {

                tr.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TableRow tr = (TableRow) v;
                    }
                });
            }
            mTableLayout.addView(tr, trParams);

            if (i > -1) {

                // add separator row
                final TableRow trSep = new TableRow(InvoiceActivity.this);
                TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                trSep.setLayoutParams(trParamsSep);
                TextView tvSep = new TextView(InvoiceActivity.this);
                TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span = 11;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);

                trSep.addView(tvSep);
                mTableLayout.addView(trSep, trParamsSep);
            }


        }

    }
}