package com.example.shen.ecard;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {

    String LOG="fingerprint";
    SharedPreferences preferences;

    private SpassFingerprint mSpassFingerprint;
    private Spass mSpass;

    private boolean isFeatureEnabled_fingerprint = false;
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        flag = preferences.getString("Flag", "");

        Timer time = new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                if ( ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else{
                    if(flag.equals("true")){

                        mSpass = new Spass();

                        try {
                            mSpass.initialize(WelcomeActivity.this);
                        } catch (SsdkUnsupportedException e) {
                            e.printStackTrace();
                        } catch (UnsupportedOperationException e) {
                            Log.d(LOG,"UnsupportedOperationException");
                        }

                        isFeatureEnabled_fingerprint = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

                        if (isFeatureEnabled_fingerprint) {
                            mSpassFingerprint = new SpassFingerprint(WelcomeActivity.this);
                        }

                        startIdentifyDialog(false);
                    }
                    else{
                        Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        time.schedule(task, 3000);
    }

    private SpassFingerprint.IdentifyListener mIdentifyListenerDialog = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                Intent intent = new Intent(WelcomeActivity.this, HomepageActivity.class);
                intent.putExtra("username",preferences.getString("username",""));
                startActivity(intent);
                finish();
            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED
                    || eventStatus == SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Flag","");
                editor.putString("username","");
                editor.apply();
                Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onReady() {

        }

        @Override
        public void onStarted() {
        }

        @Override
        public void onCompleted() {

        }
    };

    private void startIdentifyDialog(boolean backup) {
        try {
            if (mSpassFingerprint != null) {
                mSpassFingerprint.startIdentifyWithDialog(WelcomeActivity.this, mIdentifyListenerDialog, backup);
            }
        } catch (IllegalStateException e) {
            Log.d(LOG,"Exception: " + e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(flag.equals("true")){

                    mSpass = new Spass();

                    try {
                        mSpass.initialize(WelcomeActivity.this);
                    } catch (SsdkUnsupportedException e) {
                        e.printStackTrace();
                    } catch (UnsupportedOperationException e) {
                        Log.d(LOG,"UnsupportedOperationException");
                    }

                    isFeatureEnabled_fingerprint = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

                    if (isFeatureEnabled_fingerprint) {
                        mSpassFingerprint = new SpassFingerprint(WelcomeActivity.this);
                    }

                    startIdentifyDialog(false);
                }
                else{
                    Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
