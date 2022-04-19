package com.umashankar.localitem.ui.account;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.umashankar.localitem.AccountSettingActivity;
import com.umashankar.localitem.AddToCartActivity;
import com.umashankar.localitem.HomeUserActivity;
import com.umashankar.localitem.LoginAuthActivity;
import com.umashankar.localitem.Model.Userdata;
import com.umashankar.localitem.MyAddressBook;
import com.umashankar.localitem.MyOrdersActivity;
import com.umashankar.localitem.R;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;


import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {


    CircleImageView imageView;
    TextView accName, email;
    Button logout,login;
    TextView accountSettings,cartView,address,myOrder,myReward,aboutUs,feed;

    private String date_time, saveCurrentDate, saveCurrentTime;
    private String feedRandomKey;

    private EditText txtName,txtEmail,txtDetail;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentUser,userEmail;
    private DatabaseReference adminListRef;
    private String downloadImageUrl="";
    private DatabaseReference profiletRef;

    private Dialog feedDialog;

    public AccountFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        cartView = root.findViewById(R.id.Menu_Cart);
        address = root.findViewById(R.id.add_book);
        myOrder = root.findViewById(R.id.order);
        myReward = root.findViewById(R.id.tvRewards);
        aboutUs = root.findViewById(R.id.tvAboutUs);
        accountSettings = root.findViewById(R.id.acc_settings);
        feed = root.findViewById(R.id.feedBack);


        myOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser != null){
                    startActivity(new Intent(getActivity(), MyOrdersActivity.class));
                }else{
                    SignInDialog();
                }
            }
        });

        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser != null){
                    Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
                    intent.putExtra("User",currentUser);
                    startActivity(intent);
                }else{
                    SignInDialog();
                }
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackDialog();
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null){
                    Intent intent = new Intent(getActivity(), MyAddressBook.class);
                    intent.putExtra("layout_code", 3);
                    startActivity(intent);
                }else {
                    SignInDialog();
                }
            }
        });

        myReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    ComingsoonDialog();
                }else {
                    SignInDialog();
                }
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsDialog();
            }
        });

        cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    startActivity(new Intent(getActivity(), AddToCartActivity.class));
                }else {
                    SignInDialog();
                }
            }
        });


        imageView = root.findViewById(R.id.imageView);
        accName = root.findViewById(R.id.txtProfileName);
        email = root.findViewById(R.id.textEmailId);
        logout = root.findViewById(R.id.Logout_button);
        login = root.findViewById(R.id.Login_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),LoginAuthActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("Are you sure you want to Logout?")
                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(getActivity(), HomeUserActivity.class));
                    }
                }).setNegativeButton("No",null).show();
            }
        });

        return root;
    }

    private void FeedbackDialog() {
        feedDialog = new Dialog(getActivity());
        feedDialog.setContentView(R.layout.feedback_form_layout);
        feedDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        feedDialog.setCancelable(true);
        Button send = feedDialog.findViewById(R.id.feedSend);
        Button cancel = feedDialog.findViewById(R.id.feedCancel);

        txtName = feedDialog.findViewById(R.id.feedName);
        txtEmail = feedDialog.findViewById(R.id.feedEmail);
        txtDetail = feedDialog.findViewById(R.id.feedDetails);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFeedback();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedDialog.dismiss();
            }
        });
        feedDialog.show();
    }

    private void SendFeedback() {
        adminListRef = FirebaseDatabase.getInstance().getReference();
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
        feedRandomKey = "FEED-"+salt.toString();

        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("feedid", feedRandomKey);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("order_date_time", date_time);
        hashMap.put("name", txtName.getText().toString());
        hashMap.put("email", txtEmail.getText().toString());
        hashMap.put("details", txtDetail.getText().toString());

        adminListRef.child("UserFeedback").child(feedRandomKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                sendEmailFeedback();
                feedDialog.dismiss();
            }
        });
    }


//    private void sendEmailFeedback() {
//        Properties properties = new Properties();
//        properties.put("mail.smtp.host", "smtp.dreamhost.com");
//        properties.put("mail.smtp.ssl.trust", "smtp.dreamhost.com");
//        properties.put("mail.smtp.socketFactory.port", "465");
//        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.port", "465");
//
//        session = Session.getDefaultInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("info@localitem.in","Localitem@001");
//            }
//        });
//
//        session.setDebug(true);
//        AccountFragment.RetreiveFeedTask retreiveFeedTask = new AccountFragment.RetreiveFeedTask();
//        retreiveFeedTask.execute();
//    }
//    class RetreiveFeedTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                String messageMail =
//                        "<img src=\"http://localitem.in/src/logo/logo.png\"><br/>" +
//                                "Name" +txtName.getText().toString().trim() + "," +
//                                "<br>" +
//                                "Email : " + txtEmail.getText().toString().trim() +
//                                "<br>" +
//                                "Feedback : " + txtDetail.getText().toString().trim() +
//                                "<br>";
//
//                String TO = "umashankarv110@gmail.com";
//
//                Message message = new MimeMessage(session);
//                message.setFrom(new InternetAddress("info@localitem.in","LocalItem",""));
//                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
//                message.setSubject("Feedback");
//                message.setContent(messageMail, "text/html; charset=utf-8");
//                Transport trans = session.getTransport("smtp");
//                trans.connect("smtp.dreamhost.com", 465, "info@localitem.in", "Localitem@001");
//                trans.sendMessage(message, message.getAllRecipients());
//
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }

    private void AboutUsDialog() {
        final Dialog aboutDialog = new Dialog(getActivity());
        aboutDialog.setContentView(R.layout.about_us);
        aboutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        aboutDialog.setCancelable(true);
        Button Ok = aboutDialog.findViewById(R.id.aboutOk);
        TextView link = aboutDialog.findViewById(R.id.tv_link);


        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://kiratcommunications.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.dismiss();
            }
        });
        aboutDialog.show();

    }

    private void ComingsoonDialog() {
        final Dialog soonDialog = new Dialog(getActivity());
        soonDialog.setContentView(R.layout.comingsoon_layout);
        soonDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        soonDialog.setCancelable(true);
        Button Ok = soonDialog.findViewById(R.id.buttonOk);
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soonDialog.dismiss();
            }
        });
        soonDialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();
        CheckUser();
    }

    private void CheckUser() {
        if (firebaseUser != null){
            GetProfileData();
            email.setText(firebaseUser.getEmail());
            email.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
        }else{
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            accName.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }
    }

    private void GetProfileData() {
        currentUser = firebaseUser.getUid();
        userEmail = firebaseUser.getEmail();
        profiletRef = FirebaseDatabase.getInstance().getReference().child("UsersAuth").child(currentUser);

        profiletRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Userdata userdata = dataSnapshot.getValue(Userdata.class);

                    if (!userdata.getFname().equals("")){
                        accName.setText(userdata.getFname()+" "+userdata.getLname());
                        accName.setVisibility(View.VISIBLE);
                    }
                    if (!userdata.getEmail().equals("")){
                        email.setText(userdata.getEmail());
                    }
                    if (!userdata.getImage().equals("")){
                        downloadImageUrl = userdata.getImage();
                        Glide.with(getActivity()).load(downloadImageUrl).apply(new RequestOptions().placeholder(R.drawable.profile)).into(imageView);
                        //Picasso.get().load(userdata.getImage()).into(imageView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SignInDialog() {
        final Dialog signInDialog = new Dialog(getActivity());
        signInDialog.setContentView(R.layout.signin_dialog);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        signInDialog.setCancelable(true);
        Button signin = signInDialog.findViewById(R.id.cancel_btn);
        Button signup = signInDialog.findViewById(R.id.ok_btn);

        final Intent intent = new Intent(getActivity(), LoginAuthActivity.class);
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
