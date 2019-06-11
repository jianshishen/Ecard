package com.example.shen.ecard;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class DialogTool {
    public static void showDialog(final Context ctx, String msg)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(ctx).setMessage(msg).setCancelable(false);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }
    public static void showDialog(Context ctx , View view)
    {
        new AlertDialog.Builder(ctx).setView(view).setCancelable(false).setPositiveButton("OK", null).create().show();
    }
}
