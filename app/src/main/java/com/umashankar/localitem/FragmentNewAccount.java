package com.umashankar.localitem;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;
import java.util.Calendar;
import java.text.SimpleDateFormat;


public class FragmentNewAccount extends Fragment {

    private Button Back,Signup;
    private TextView email,phone,pass;
    String UserId;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,userNotifyRef;
    private String Email,Phone,Pass,Name,Image,notificationID;
    private String msgTitle,msgDetails,megDateTime;
    private String date_time, saveCurrentDate, saveCurrentTime;

    public FragmentNewAccount() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_new_account, container, false);


        UserRef = FirebaseDatabase.getInstance().getReference().child("UsersAuth");
        userNotifyRef = FirebaseDatabase.getInstance().getReference().child("UserData");
        mAuth = FirebaseAuth.getInstance();

        Back = root.findViewById(R.id.buttonBack);
        email = root.findViewById(R.id.signin_email);
        phone = root.findViewById(R.id.signin_phone);
        pass = root.findViewById(R.id.signin_password);
        Signup = root.findViewById(R.id.buttonRegister);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd MMM,yyyy");
        saveCurrentDate = date.format(calendar.getTime());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = time.format(calendar.getTime());
        date_time = saveCurrentDate + " | " + saveCurrentTime;

        String SALTCHARS = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        while (salt.length() < 8) {
            int index = (int) (random.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        notificationID = "LCL"+salt.toString();

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterAcc();
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetFragment();
            }
        });
    }

    private void SetFragment() {
        FragmentLogin fragment = new FragmentLogin();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayout, fragment);
        transaction.commit();
    }

    private void RegisterAcc() {
        Email = email.getText().toString().trim();
        Phone = phone.getText().toString().trim();
        Pass = pass.getText().toString().trim();

        if (TextUtils.isEmpty(Email)){
            Toast.makeText(getActivity(), "Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Phone)){
            Toast.makeText(getActivity(), "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.PHONE.matcher(Phone).matches()){
            Toast.makeText(getActivity(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pass)){
            Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else if (Pass.length()<6){
            Toast.makeText(getActivity(), "Password length at least 6 characters", Toast.LENGTH_SHORT).show();
        }
        else {
            Signup.setEnabled(false);
            msgTitle = "Welcome To LocalItem";
            msgDetails = "We have amazing Offers for you.";
            megDateTime = date_time;
            mAuth.createUserWithEmailAndPassword(Email,Pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                final String uid = firebaseUser.getUid();
                                HashMap<String,Object> userHashMap = new HashMap<>();
                                userHashMap.put("image","");
                                userHashMap.put("address","");
                                userHashMap.put("fname","");
                                userHashMap.put("lname","");
                                userHashMap.put("dob","");
                                userHashMap.put("state","");
                                userHashMap.put("uid",uid);
                                userHashMap.put("email",Email);
                                userHashMap.put("phone",Phone);
                                userHashMap.put("password",Pass);

                                final HashMap<String, Object> notiMap = new HashMap<>();
                                notiMap.put("notificationID",notificationID);
                                notiMap.put("nofy_id","nofy001");
                                notiMap.put("title",msgTitle);
                                notiMap.put("details",msgDetails);
                                notiMap.put("date_time",megDateTime);


                                UserRef.child(uid).updateChildren(userHashMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                userNotifyRef.child(uid).child("Notification").child(notificationID).updateChildren(notiMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getActivity(), "Registration done..", Toast.LENGTH_SHORT).show();
                                                        FragmentLogin fragment = new FragmentLogin();
                                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                        transaction.replace(R.id.relativeLayout, fragment);
                                                        transaction.commit();
                                                    }
                                                });
                                            }
                                        });
                            }else{

                                Signup.setEnabled(true);
                                Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
