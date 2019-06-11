package com.example.shen.ecard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;

/**
 * Created by shen on 30/09/2017.
 */

public class NetworkTool {
    static String accountkey="4VtJDaerZNDx4EoC5S2WGT77z2OcskOuWxUklTr2AUQPYIoigHZtNQKhmwlFJPGu8cMgB/i5vUq8W+hIMtB4IA==";
    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=http;" +
                    "AccountName=ecard;" +
                    "AccountKey="+accountkey;

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        DialogTool.showDialog(context, "No Network!");
        return false;
    }
    public static String getroot(Context context) {
        File dir = Environment.getExternalStorageDirectory();
        String rootDir = null;
        if (dir == null) {
            dir = context.getDir(".", 0);
            rootDir = dir.getAbsolutePath();
        } else {
            rootDir = dir.getAbsolutePath() +"/"+ context.getResources().getString(R.string.app_name);
        }
        dir = new File(rootDir);
        if (dir.mkdir()) {
            new File(rootDir+"/discounts").mkdir();
        }
        return rootDir;
    }
}
