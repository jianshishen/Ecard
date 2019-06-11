package com.example.shen.ecard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class DisplayActivity extends AppCompatActivity {

    public final static int WIDTH = 882;
    public final static int HEIGHT = 270;
    String number;
    String company;
    ImageView logo;
    ImageView barcode;
    TextView name;
    int curBrightnessValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        logo=(ImageView)findViewById(R.id.displaylogo);
        barcode=(ImageView)findViewById(R.id.displaybarcode);
        name=(TextView)findViewById(R.id.displayname);

        curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS,-1);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = curBrightnessValue/100.0f;
        getWindow().setAttributes(layoutParams);

        Intent intentlast=getIntent();
        number=intentlast.getStringExtra("number");
        company=intentlast.getStringExtra("company");
        name.setText(company);
        switch (company){
            case "Woolworths":
                logo.setImageResource(R.drawable.woolworths);
                break;
            case "Coles":
                logo.setImageResource(R.drawable.flybuys);
                break;
        }
        try {
            Bitmap bitmap = encodeAsBitmap(number);
            barcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.EAN_13, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    protected void onPause() {
        curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS,-1);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = curBrightnessValue/-1f;
        getWindow().setAttributes(layoutParams);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS,-1);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = curBrightnessValue/-1f;
        getWindow().setAttributes(layoutParams);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS,-1);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = curBrightnessValue/-1f;
        getWindow().setAttributes(layoutParams);
        super.onStop();
    }

    @Override
    protected void onResume() {
        curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS,-1);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = curBrightnessValue/100.0f;
        getWindow().setAttributes(layoutParams);
        super.onResume();
    }
}
