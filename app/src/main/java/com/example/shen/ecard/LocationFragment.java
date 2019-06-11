package com.example.shen.ecard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationFragment extends Fragment implements GoogleMap.OnMarkerClickListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener{

    private static final LatLng ColesBroadway = new LatLng(-33.883548, 151.194602);

    private static final LatLng ColesWorldSquare = new LatLng(-33.877577, 151.206719);

    private static final LatLng ColesLeichhardt = new LatLng(-33.886543, 151.158641);

    private static final LatLng WoolworthsCentral = new LatLng(-33.884374, 151.200618);

    private static final LatLng WoolworthsRedfern = new LatLng(-33.894562, 151.204646);

    private static final LatLng WoolworthsNewtown = new LatLng(-33.898546, 151.181625);

    private GoogleMap map;

    private Marker mColesBroadway;

    private Marker mColesWorldSquare;

    private Marker mColesLeichhardt;

    private Marker mWoolworthsCentral;

    private Marker mWoolworthsRedfern;

    private Marker mWoolworthsNewtown;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Location");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        map.getUiSettings().setZoomControlsEnabled(false);
        add();
        map.setOnMarkerClickListener(this);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(ColesBroadway)
                .include(ColesWorldSquare)
                .include(ColesLeichhardt)
                .include(WoolworthsCentral)
                .include(WoolworthsRedfern)
                .include(WoolworthsNewtown)
                .build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    private void add() {
        mWoolworthsCentral = map.addMarker(new MarkerOptions()
                .position(WoolworthsCentral)
                .title("WoolworthsCentral"));
        mColesWorldSquare = map.addMarker(new MarkerOptions()
                .position(ColesWorldSquare)
                .title("ColesWorldSquare"));
        mWoolworthsRedfern = map.addMarker(new MarkerOptions()
                .position(WoolworthsRedfern)
                .title("WoolworthsRedfern"));
        mColesBroadway = map.addMarker(new MarkerOptions()
                .position(ColesBroadway)
                .title("ColesBroadway"));
        mColesLeichhardt = map.addMarker(new MarkerOptions()
                .position(ColesLeichhardt)
                .title("ColesLeichhardt"));
        mWoolworthsNewtown = map.addMarker(new MarkerOptions()
                .position(WoolworthsNewtown)
                .title("WoolworthsNewtown"));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        final Interpolator interpolator = new BounceInterpolator();

        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
        return false;
    }
}
