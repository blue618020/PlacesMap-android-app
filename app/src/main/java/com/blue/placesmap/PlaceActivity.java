package com.blue.placesmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.blue.placesmap.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PlaceActivity extends AppCompatActivity {

    ArrayList<Place> placeArrayList;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        // 메인 액티비티가 보내준거 받아오기
        placeArrayList = (ArrayList<Place>) getIntent().getSerializableExtra("placeArrayList");
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // 우리의 위치를 맵의 중심으로 놓고, 플레이스 여러개를 마커로 만들어서 표시하기
                LatLng center = new LatLng(lat, lng);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 17));

                for (Place place : placeArrayList){
                    // 리스트에 있는걸 다 꺼낼때까지 반복하는 반복문
                    LatLng latLng = new LatLng(place.geometry.location.lat, place.geometry.location.lng);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(place.name));
                }
            }
        });
    }
}