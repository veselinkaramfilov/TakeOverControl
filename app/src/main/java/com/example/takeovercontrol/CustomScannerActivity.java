package com.example.takeovercontrol;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

public class CustomScannerActivity extends CaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        DecoratedBarcodeView barcodeScannerView = findViewById(com.google.zxing.client.android.R.id.zxing_barcode_scanner);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) barcodeScannerView.getLayoutParams();
        int scanAreaSize = (int) (Math.min(screenWidth, screenHeight) * 0.6);
        params.width = scanAreaSize;
        params.height = scanAreaSize;
        barcodeScannerView.setLayoutParams(params);
    }
}
