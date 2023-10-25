package com.blue.placesmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blue.placesmap.adapter.PlaceAdapter;
import com.blue.placesmap.api.PlaceApi;
import com.blue.placesmap.api.NetworkClient;
import com.blue.placesmap.config.Config;
import com.blue.placesmap.model.Place;
import com.blue.placesmap.model.PlaceList;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    EditText editKeyword;
    Button btnSearch;
    RecyclerView recyclerView;
    ArrayList<Place> placeArrayList = new ArrayList<Place>();
    PlaceAdapter adapter;

    // 내 위치를 가져오기 위한 멤버변수
    LocationManager locationManager;
    LocationListener locationListener;

    double lat;
    double lng;

    int radius = 2000; // 미터 단위
    String keyword;
    boolean isLocationReady;
    String pagetoken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editKeyword = findViewById(R.id.editKeyword);
        btnSearch = findViewById(R.id.btnSearch);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // 페이징 저리
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCont = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCont){

                    if(pagetoken != null){
                        addNetworkData();
                    }
                }
            }
        });


        // 핸드폰의 위치를 가져오기 위해서, 시스템 서비스로부터 로케이션 매니저를 받아오기
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 로케이션 리스너 만들기
        // 위치를 잡으면 동작함. 위치가 변해도 실시간으로 동작해서 잡아옴
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // 여기에 위치 로직 작성

                // 위도 가져오기
//                location.getLatitude();
                // 경도 가져오기
//                location.getLongitude();

                lat = location.getLatitude();
                lng = location.getLongitude();
                isLocationReady = true;

            }
        };

        // 위치 기반 서비스 권한 필요
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return;
        }

        // 체크해서 위치기반 허용했다면, 로케이션 매니저에 리스너를 연결하기
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,
                -1,
                locationListener); // 3초마다 위치 찍기



        // 검색 버튼을 눌럿을 때
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLocationReady == false){
                    Snackbar.make(btnSearch,
                            "아직 위치를 잡지 못했습니다. 잠시후에 다시 시도해주세요",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                keyword = editKeyword.getText().toString().trim();

                Log.i("keyword", keyword);

                if (keyword.isEmpty()){
                    Log.i("isEmpty", "isEmpty");
                    return;
                }

                // 체크했다면 api 호출
                getNetworkData();
            }
        });
    }

    private void addNetworkData() {
        // 나중에 프로그래스바 추가하기

        // 리스트 초기화
//        placeArrayList.clear();

        // 레트로핏 api 실행
        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        PlaceApi api = retrofit.create(PlaceApi.class);

        Call<PlaceList> call = api.getPlaceList("ko",
                lat+","+lng,
                radius,
                Config.GOOGLE_API_KEY,
                keyword);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                if (response.isSuccessful()){

                    PlaceList placeList = response.body();
                    pagetoken = placeList.next_page_token;
                    placeArrayList.addAll(placeList.results);
                    adapter.notifyDataSetChanged();

                } else{

                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {

            }
        });
    }


    private void getNetworkData() {
        Log.i("getNetworkData", "getNetworkData");

        // 나중에 프로그래스바 추가하기

        // 리스트 초기화
        placeArrayList.clear();

        // 레트로핏 api 실행
        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        PlaceApi api = retrofit.create(PlaceApi.class);

        Call<PlaceList> call = api.getPlaceList("ko",
                lat+"+"+lng,
                radius,
                Config.GOOGLE_API_KEY,
                keyword);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(retrofit2.Call<PlaceList> call, Response<PlaceList> response) {
                if (response.isSuccessful()){

                    if (response.isSuccessful()){
                        PlaceList placeList = response.body();
                        pagetoken = placeList.next_page_token;

                        placeArrayList.addAll(placeList.results);

                        Log.i("placeArrayList", "placeArrayList");

                        adapter = new PlaceAdapter(MainActivity.this, placeArrayList);
                        recyclerView.setAdapter(adapter);
                    }

                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<PlaceList> call, Throwable t) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            // 위치기반 허용했다면, 한번 더 체크

            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
                return;
            }

            // 체크해서 위치기반 허용했다면, 로케이션 매니저에 리스너를 연결하기
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    -1,
                    locationListener); // 3초마다 위치 찍기
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 액션바의 메뉴가 나오도록 설정하기
        getMenuInflater().inflate(R.menu.main, menu); // 화면과 menu 를 연결
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // 유저가 + 아이콘을 눌렀을 경우, add 액티비티를 실행시키기
        int itemId = item.getItemId();

        if (itemId == R.id.menuMap) {
            Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
            intent.putExtra("placeArrayList", placeArrayList);

            // 내 위치 정보도 넘겨주기
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}