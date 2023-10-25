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

public class MapActivity extends AppCompatActivity {

    Place place; // 다른데에서도 쓸려고 멤버변수로 만듬

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // 어뎁터가 보낸 플레이스 정보 받기
        place = (Place) getIntent().getSerializableExtra("place");

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // 유저가 누른 플레이스의 위도, 경도 값을 가지고, 마커로 표시하기
                // 해당 플레이스의 정보는 어뎁터에게 받았음(위에서)

                double lat = place.geometry.location.lat;
                double lng = place.geometry.location.lng;

                // 맵이 화면에 나타나는 부분
                LatLng latLng = new LatLng(lat, lng);  // 위도, 경도값 가져온거 여기에 넣기

                // 지도의 중심을 내가 정한 위치로 세팅
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

                // 마커를 만들어서 지도에 표시
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng).title(place.name);  // 장소 이름 가져온거 여기에 넣기
                googleMap.addMarker(markerOptions).setTag(0);  // 화면에 표시, 태그 달기
            }
        });
    }
}