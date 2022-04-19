package com.umashankar.localitem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umashankar.localitem.Model.MyAddressModel;
import com.umashankar.localitem.Model.StateName;
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
import com.umashankar.localitem.ViewHolder.StateAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MyAddressBook extends AppCompatActivity {

    ListView stateListView;
    public static ArrayList<StateName> stateArrayList = new ArrayList<>();
    StateAdapter stateAdapter;
    StateName stateDetails;

    private ArrayAdapter addressAdapter;
    private String State="";
    private TextView tv_state,fullname,mobile,address,city,landmark,pincode;
    private String Name="",Address="",City="",Landmark="",Contact="",Pincode="";
    private String iName,iAddress,iCity,iPincode,iState,iLandmark,iContact,addressId;
    private Button add_address ;
    private AlertDialog dialog1;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    LinearLayout state_layout;

    private ArrayList<String> AddState = new ArrayList<>();
    private DatabaseReference addressRef,stateReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private String currentUser;
    private String productID = "",Qty = "",Price ="",TotalPrice="";
    private int layout_code;
    private Button addressSelect;
    private TextView text_Address;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address_book);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.myAddressBook_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Address Book");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        currentUser = firebaseUser.getUid();

        addressRef = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUser);
        stateReference = FirebaseDatabase.getInstance().getReference("AdminData").child("States");

        addressSelect = findViewById(R.id.aadressSelect);
        recyclerView = findViewById(R.id.my_address_book);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        text_Address = findViewById(R.id.text_address);
        layout_code = getIntent().getIntExtra("layout_code",-1);

        if (layout_code == 3){// 3 means account fragment
            text_Address.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewAllAddress();
    }

    private void ViewAllAddress() {
        FirebaseRecyclerOptions<MyAddressModel> options =
                new FirebaseRecyclerOptions.Builder<MyAddressModel>()
                        .setQuery(addressRef.child("Address"),MyAddressModel.class).build();

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
                        viewHolder.SelectBTN.setVisibility(View.GONE);
                        viewHolder.deleteAddress.setVisibility(View.VISIBLE);
                        viewHolder.editAddress.setVisibility(View.VISIBLE);

                        productID = getIntent().getStringExtra("pid");
                        Qty = getIntent().getStringExtra("qty");
                        Price = getIntent().getStringExtra("price");
                        TotalPrice = getIntent().getStringExtra("Totalprice");

                        if (layout_code == 3){// 3 means account fragment
                            viewHolder.editAddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addressId = address.getAid();
                                    iName = address.getName();
                                    iAddress = address.getAddress();
                                    iCity = address.getCity();
                                    iPincode = address.getPincode();
                                    iState = address.getState();
                                    iLandmark = address.getLandmark();
                                    iContact = address.getMobile();
                                    EditAddress();
                                }
                            });

                            viewHolder.deleteAddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addressId = address.getAid();
                                    new AlertDialog.Builder(MyAddressBook.this)
                                            .setMessage("Are you sure you want to delete?")
                                            .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addressRef.child("Address").child(addressId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(MyAddressBook.this, "Removed ", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).setNegativeButton("No",null).show();
                                }
                            });


                        }
                    }

                    @NonNull
                    @Override
                    public MyAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_layout,parent,false);
                        MyAddressViewHolder holder = new MyAddressViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void EditAddress() {
        final Dialog addressDialog = new Dialog(MyAddressBook.this);
        addressDialog.setContentView(R.layout.add_address_layout);
        addressDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addressDialog.setCancelable(true);

        fullname = addressDialog.findViewById(R.id.add_address_name);
        mobile = addressDialog.findViewById(R.id.add_address_mobile);
        address = addressDialog.findViewById(R.id.add_address_address);
        city = addressDialog.findViewById(R.id.add_address_city);
        landmark = addressDialog.findViewById(R.id.add_address_landmark);
        pincode = addressDialog.findViewById(R.id.add_address_pincode);
        add_address = addressDialog.findViewById(R.id.add_address_button);
        state_layout = addressDialog.findViewById(R.id.state_layout);
        tv_state = addressDialog.findViewById(R.id.tv_state);

        tv_state.setText(iState);

        add_address.setText("Update Address");
        fullname.setText(iName);
        address.setText(iAddress);
        city.setText(iCity);
        landmark.setText(iLandmark);
        pincode.setText(iPincode);
        mobile.setText(iContact);

        state_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog stateDialog = new Dialog(MyAddressBook.this);
                stateDialog.setContentView(R.layout.layout_state_listview);
                stateDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                stateDialog.setCancelable(true);
                stateDialog.show();
                stateListView = stateDialog.findViewById(R.id.stateListView);
                ImageView ivClose = stateDialog.findViewById(R.id.layoutClose);

                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stateDialog.dismiss();
                    }
                });

                stateReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot item: dataSnapshot.getChildren()){
                            String StateTitle = (String) item.getValue();
                            stateDetails = new StateName(StateTitle);
                            stateArrayList.add(stateDetails);
                            stateAdapter = new StateAdapter(MyAddressBook.this, stateArrayList);
                            stateListView.setAdapter(stateAdapter);
                            stateAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                stateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tv_state.setText(stateArrayList.get(i).getStateName());
                        stateDialog.dismiss();
                        if(stateArrayList.get(i).getStateName().equals("Haryana")){
                            final Dialog offerDialog = new Dialog(MyAddressBook.this);
                            offerDialog.setContentView(R.layout.special_offer);
                            offerDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            offerDialog.setCancelable(false);
                            offerDialog.show();

                            Button addNew = offerDialog.findViewById(R.id.button3);
                            addNew.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    offerDialog.dismiss();
                                }
                            });
                        }
                    }
                });

            }
        });

        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Name = fullname.getText().toString().trim();
                Address = address.getText().toString().trim();
                City = city.getText().toString().trim();
                Pincode = pincode.getText().toString().trim();
                Landmark = landmark.getText().toString().trim();
                Contact = mobile.getText().toString().trim();
                State = tv_state.getText().toString().trim();

                if (tv_state.equals("Select State")){
                    Toast.makeText(MyAddressBook.this, "Select State", Toast.LENGTH_SHORT).show();
                }else if(Name.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter Name", Toast.LENGTH_SHORT).show();
                }else if(Address.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter Address", Toast.LENGTH_SHORT).show();
                }else if(City.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter City", Toast.LENGTH_SHORT).show();
                }else if(Pincode.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter Pincode", Toast.LENGTH_SHORT).show();
                }else if (Pincode.length() != 6){
                    Toast.makeText(MyAddressBook.this, "Pincode must be six digits", Toast.LENGTH_SHORT).show();
                }else if(Contact.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter Contact", Toast.LENGTH_SHORT).show();
                }else if (Contact.length() != 10){
                    Toast.makeText(MyAddressBook.this, "Invalid Contact", Toast.LENGTH_SHORT).show();
                } else {

                    HashMap<String, Object> addressMap = new HashMap<>();
                    addressMap.put("name", Name);
                    addressMap.put("aid", addressId);
                    addressMap.put("address", Address);
                    addressMap.put("city", City);
                    addressMap.put("pincode", Pincode);
                    addressMap.put("landmark", Landmark);
                    addressMap.put("mobile", Contact);
                    addressMap.put("state", State);

                    addressRef.child("Address").child(addressId).updateChildren(addressMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MyAddressBook.this, "Address Updated..", Toast.LENGTH_SHORT).show();
                                        addressDialog.dismiss();
                                    }
                                }
                            });
                }

            }
        });

        addressDialog.show();

    }


    public void AddNewAddress(View view) {
        final AlertDialog.Builder alert;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            alert = new AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert);
        }else{
            alert = new AlertDialog.Builder(this);
        }

        LayoutInflater inflater = getLayoutInflater();
        view = inflater.inflate(R.layout.add_address_layout, null);

        fullname = view.findViewById(R.id.add_address_name);
        mobile = view.findViewById(R.id.add_address_mobile);
        address = view.findViewById(R.id.add_address_address);
        city = view.findViewById(R.id.add_address_city);
        landmark = view.findViewById(R.id.add_address_landmark);
        pincode = view.findViewById(R.id.add_address_pincode);
        add_address = view.findViewById(R.id.add_address_button);
        state_layout = view.findViewById(R.id.state_layout);
        tv_state = view.findViewById(R.id.tv_state);
        tv_state.setText("Select State");

        state_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog stateDialog = new Dialog(MyAddressBook.this);
                stateDialog.setContentView(R.layout.layout_state_listview);
                stateDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                stateDialog.setCancelable(true);
                stateDialog.show();
                stateListView = stateDialog.findViewById(R.id.stateListView);
                ImageView ivClose = stateDialog.findViewById(R.id.layoutClose);

                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stateDialog.dismiss();
                    }
                });

                stateReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot item: dataSnapshot.getChildren()){
                            String StateTitle = (String) item.getValue();
                            stateDetails = new StateName(StateTitle);
                            stateArrayList.add(stateDetails);
                            stateAdapter = new StateAdapter(MyAddressBook.this, stateArrayList);
                            stateListView.setAdapter(stateAdapter);
                            stateAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                stateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tv_state.setText(stateArrayList.get(i).getStateName());
                        stateDialog.dismiss();
                        if(tv_state.getText().toString().equals("Haryana")){
                            final Dialog offerDialog = new Dialog(MyAddressBook.this);
                            offerDialog.setContentView(R.layout.special_offer);
                            offerDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            offerDialog.setCancelable(false);
                            offerDialog.show();

                            Button addNew = offerDialog.findViewById(R.id.button3);
                            addNew.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    offerDialog.dismiss();
                                }
                            });
                        }
                    }
                });

            }
        });

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        while (salt.length() < 18){
            int index = (int)(random.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        final String randomKey = salt.toString();

        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Name = fullname.getText().toString().trim();
                Address = address.getText().toString().trim();
                City = city.getText().toString().trim();
                Pincode = pincode.getText().toString().trim();
                Landmark = landmark.getText().toString().trim();
                Contact = mobile.getText().toString().trim();
                State = tv_state.getText().toString().trim();


                if (State.equals("Select State")){
                    Toast.makeText(MyAddressBook.this, "Select State", Toast.LENGTH_SHORT).show();
                }else if(Name.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter Name", Toast.LENGTH_SHORT).show();
                }else if(Address.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter Address", Toast.LENGTH_SHORT).show();
                }else if(City.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter City", Toast.LENGTH_SHORT).show();
                }else if(Pincode.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter Pincode", Toast.LENGTH_SHORT).show();
                }else if (Pincode.length() != 6){
                    Toast.makeText(MyAddressBook.this, "Pincode must be 6 digits", Toast.LENGTH_SHORT).show();
                }else if(Contact.equals("")){
                    Toast.makeText(MyAddressBook.this, "Enter Contact", Toast.LENGTH_SHORT).show();
                }else if (Contact.length() != 10){
                    Toast.makeText(MyAddressBook.this, "Invalid Contact", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> addressMap = new HashMap<>();
                    addressMap.put("name", Name);
                    addressMap.put("aid", randomKey);
                    addressMap.put("address", Address);
                    addressMap.put("city", City);
                    addressMap.put("pincode", Pincode);
                    addressMap.put("landmark", Landmark);
                    addressMap.put("mobile", Contact);
                    addressMap.put("state", State);

                    addressRef.child("Address").child(randomKey).updateChildren(addressMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MyAddressBook.this, "Address Added..", Toast.LENGTH_SHORT).show();
                                        dialog1.dismiss();
                                    }
                                }
                            });
                }
            }
        });

        alert.setView(view);
        alert.setCancelable(true);

        dialog1 = alert.create();
        dialog1.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog1.show();

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
