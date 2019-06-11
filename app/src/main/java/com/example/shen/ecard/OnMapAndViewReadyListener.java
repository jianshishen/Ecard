package com.example.shen.ecard;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class OnMapAndViewReadyListener implements ViewTreeObserver.OnGlobalLayoutListener, OnMapReadyCallback {
    public interface OnGlobalLayoutAndMapReadyListener {
        void onMapReady(GoogleMap googleMap);
    }

    private final SupportMapFragment mapFragment;
    private final View mapView;
    private final OnGlobalLayoutAndMapReadyListener devCallback;

    private boolean isViewReady;
    private boolean isMapReady;
    private GoogleMap googleMap;

    public OnMapAndViewReadyListener(
            SupportMapFragment mapFragment, OnGlobalLayoutAndMapReadyListener devCallback) {
        this.mapFragment = mapFragment;
        mapView = mapFragment.getView();
        this.devCallback = devCallback;
        isViewReady = false;
        isMapReady = false;
        googleMap = null;

        registerListeners();
    }

    private void registerListeners() {
        if ((mapView.getWidth() != 0) && (mapView.getHeight() != 0)) {
            isViewReady = true;
        } else {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        isMapReady = true;
        fireCallbackIfReady();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onGlobalLayout() {
            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        isViewReady = true;
        fireCallbackIfReady();
    }

    private void fireCallbackIfReady() {
        if (isViewReady && isMapReady) {
            devCallback.onMapReady(googleMap);
        }
    }
}
