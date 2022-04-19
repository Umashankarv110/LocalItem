package com.umashankar.localitem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.umashankar.localitem.Model.Userdata;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Calendar calendar = Calendar.getInstance();
    private String dateText;
    private ImageView dateImageView,addProfilePic;
    private Button saveButton;

    private CircleImageView profileImage;
    private EditText dobTextView,fname,lname,email,contact,password;
    private String downloadImageUrl="";
    private String First,Last,DOB,Contact,Email,Password;
    private DatabaseReference profiletRef;
    private StorageReference profileImageRef;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentUser;

    public static final int GalleryPick = 1;
    private Uri ImageUri;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        Toolbar toolbar = findViewById(R.id.myProfile_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUser = firebaseUser.getUid();

        profiletRef = FirebaseDatabase.getInstance().getReference().child("UsersAuth").child(currentUser);
        profileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        profileImage = findViewById(R.id.ivProfile);
        dateImageView = findViewById(R.id.ivDate);
        fname = findViewById(R.id.etFirstName);
        lname = findViewById(R.id.etLastName);
        dobTextView = findViewById(R.id.etDOB);
        email = findViewById(R.id.tvEmailId);
        contact = findViewById(R.id.tvContact);
        password = findViewById(R.id.tvPassword);
        saveButton = findViewById(R.id.saveProfileBtn);
        addProfilePic = findViewById(R.id.addProfile);

        addProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });


        //Select Date
        dateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDate();
            }
        });


    }


    private void checkData() {
        First = fname.getText().toString().trim();
        Last = lname.getText().toString().trim();
        DOB = dobTextView.getText().toString().trim();
        Email = email.getText().toString().trim();
        Contact = contact.getText().toString().trim();
        Password = password.getText().toString().trim();

        if (TextUtils.isEmpty(First)){
            Toast.makeText(getApplicationContext(), "Enter first name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(Last)){
            Toast.makeText(getApplicationContext(), "Enter last name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(DOB)){
            Toast.makeText(getApplicationContext(), "Enter date of birth", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Email)){
            Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Contact)){
            Toast.makeText(getApplicationContext(), "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.PHONE.matcher(Contact).matches()){
            Toast.makeText(getApplicationContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Password)){
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else if (Password.length()<6){
            Toast.makeText(getApplicationContext(), "Password length at least 6 characters", Toast.LENGTH_SHORT).show();
        }
        else {
            saveProfileDetail();
        }
    }

    private void saveProfileDetail() {

        if (ImageUri != null){
            final StorageReference filePath = profileImageRef.child(ImageUri.getLastPathSegment() + currentUser + ".jpg");
            final UploadTask uploadTask = filePath.putFile(ImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(getApplicationContext(), "Error " + message, Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(getApplicationContext(), "Image Uploaded.....", Toast.LENGTH_SHORT).show();
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            downloadImageUrl = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            downloadImageUrl = task.getResult().toString();
                            saveProfile();
                        }
                    });
                }
            });
        }else{
            saveProfile();
        }
    }

    private void saveProfile() {
        HashMap<String,Object> userHashMap = new HashMap<>();
        userHashMap.put("address","");
        userHashMap.put("fname",First);
        userHashMap.put("lname",Last);
        userHashMap.put("dob",DOB);
        userHashMap.put("uid",currentUser);
        userHashMap.put("email",Email);
        userHashMap.put("phone",Contact);
        userHashMap.put("password",Password);

        if (!downloadImageUrl.equals("")){
            userHashMap.put("image",downloadImageUrl);

            profiletRef.updateChildren(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Update done..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),AccountSettingActivity.class));
                    finish();
                }
            });

        }else{
            userHashMap.put("image","");
            profiletRef.updateChildren(userHashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Update done..", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),AccountSettingActivity.class));
                            finish();
                        }
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        GetProfileData(currentUser);
    }

    private void GetProfileData(final String currentUser) {
        profiletRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Userdata userdata = dataSnapshot.getValue(Userdata.class);
                    fname.setText(userdata.getFname());
                    lname.setText(userdata.getLname());
                    dobTextView.setText(userdata.getDob());

                    if (!userdata.getPassword().equals("")){
                        password.setText(userdata.getPassword());
                        password.setEnabled(false);
                    }
                    if (!userdata.getEmail().equals("")){
                        email.setText(userdata.getEmail());
                        email.setEnabled(false);
                    }
                    if (!userdata.getPhone().equals("")){
                        contact.setText(userdata.getPhone());
                        contact.setEnabled(false);
                    }
                    if (!userdata.getImage().equals("")){
                        downloadImageUrl = userdata.getImage();
                        Picasso.get().load(userdata.getImage()).into(profileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SelectDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String dateString = dateFormat.format(calendar.getTime());
        dobTextView.setText(dateString);

    }

    private void OpenGallery() {
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,GalleryPick);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == Activity.RESULT_OK && data != null ){
            ImageUri = data.getData();
            profileImage.setImageURI(ImageUri);
        }else{
            Toast.makeText(getApplicationContext(), "Error Try Again...", Toast.LENGTH_SHORT).show(); }
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
