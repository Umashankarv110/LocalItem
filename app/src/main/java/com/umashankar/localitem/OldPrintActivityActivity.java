package com.umashankar.localitem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import static com.umashankar.localitem.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.umashankar.localitem.permission.PermissionsChecker.REQUIRED_PERMISSION;

import com.umashankar.localitem.permission.PermissionsActivity;
import com.umashankar.localitem.permission.PermissionsChecker;


public class OldPrintActivityActivity extends AppCompatActivity {
    Context mContext;
    PermissionsChecker checker;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_print_activity);

        mContext = getApplicationContext();
        checker = new PermissionsChecker(this);
    }

    public void createPDF(View view) {
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            PermissionsActivity.startActivityForResult(OldPrintActivityActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
        } else {
            createPdf();
        }
    }

    private void createPdf() {
    }


}