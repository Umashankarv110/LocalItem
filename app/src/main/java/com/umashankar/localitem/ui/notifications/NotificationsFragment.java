package com.umashankar.localitem.ui.notifications;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.LoginAuthActivity;
import com.umashankar.localitem.Model.Notification;
import com.umashankar.localitem.R;
import com.umashankar.localitem.ViewHolder.NotificationAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationsFragment extends Fragment {
    private LinearLayout empty;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference notificationReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentUser;
    private int size = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        empty = root.findViewById(R.id.emptyNotificationLayout);

        recyclerView = root.findViewById(R.id.recyclerNotificationLayout);
        recyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseUser != null) {
            currentUser = firebaseUser.getUid();
            notificationReference.child(currentUser).child("Notification").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        size = (int) dataSnapshot.getChildrenCount();
                        empty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        empty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            getNotificationList();

        } else {
            SigninDialog();
        }
    }

    private void SigninDialog() {
        if (firebaseUser == null) {
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

    private void getNotificationList() {
        FirebaseRecyclerOptions<Notification> options =
                new FirebaseRecyclerOptions.Builder<Notification>()
                        .setQuery(notificationReference.child("UserData").child(currentUser).child("Notification").orderByChild("notificationID"),
                                Notification.class)
                        .build();

        FirebaseRecyclerAdapter<Notification, NotificationAdapter> adapter =
                new FirebaseRecyclerAdapter<Notification, NotificationAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull NotificationAdapter notificationAdapter, int i, @NonNull Notification notification) {
                        notificationAdapter.mesgTitle.setText(notification.getTitle());
                        notificationAdapter.mesgDetails.setText(notification.getDetails());
                        notificationAdapter.mesgTime.setText(notification.getDate_time());
                    }

                    @NonNull
                    @Override
                    public NotificationAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
                        NotificationAdapter holder = new NotificationAdapter(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}