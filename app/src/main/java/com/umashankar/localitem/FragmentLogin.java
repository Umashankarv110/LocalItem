package com.umashankar.localitem;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentLogin extends Fragment {

    private FirebaseAuth mAuth;
    private Button signin,login;
    private TextView email,pas,forgotpawd;

    private ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(getActivity());
        signin = root.findViewById(R.id.buttonSignin);
        email = root.findViewById(R.id.loginEmail);
        pas = root.findViewById(R.id.loginPass);
        login = root.findViewById(R.id.btnLogin);
        forgotpawd = root.findViewById(R.id.forgotPassword);

        forgotpawd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPassword();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentNewAccount fragment = new FragmentNewAccount();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.relativeLayout, fragment);
                transaction.commit();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Checking Credentials \nPlease Wait.... ");
                pd.show();

                String Email = email.getText().toString().trim();
                String Pass = pas.getText().toString().trim();

                if(Email.isEmpty()){
                    Toast.makeText(getActivity(), "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Pass.isEmpty()){
                    Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(Email, Pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Successfully Login...", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(),HomeUserActivity.class));
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Error!! \nInvalid Email or Password", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        return root;
    }

    //Forgot Password------------
    private void showRecoverPassword() {
        final Dialog forgotDialog = new Dialog(getActivity());
        forgotDialog.setContentView(R.layout.forgot_pwd_dialog);
        forgotDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        forgotDialog.setCancelable(true);

        Button recover = forgotDialog.findViewById(R.id.recover);
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEt = forgotDialog.findViewById(R.id.recoverEmail);
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });

        Button cancel = forgotDialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotDialog.dismiss();
            }
        });

        forgotDialog.show();
    }
    private void beginRecovery(String email) {

        pd.setMessage("Sending email...");
        pd.show();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Recovery Email sent", Toast.LENGTH_SHORT).show();
                }else{
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //--------------------

}
