package com.umashankar.localitem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class LoginAuthActivity extends AppCompatActivity {

    public static boolean setSignUpFragment = false;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (setSignUpFragment){
            setSignUpFragment = false;
            FragmentNewAccount f1  = new FragmentNewAccount();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(R.id.relativeLayout,f1).commit();
        }else {
            FragmentLogin f1  = new FragmentLogin();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(R.id.relativeLayout,f1).commit();
        }
    }

}
