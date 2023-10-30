# 위치 검색 앱

📝 <b> tistory : </b> x

### 🔍 학습 내용
-  구글맵 API를 사용해서 검색결과에 맞는 장소명과 주소 목록 리스트를 띄우기 
-  화면에 위치가 어디인지 지도를 띄워서 마커 표시하기

### 💻 실습
#### 1. 네트워크 통신하기 위해 연결시키는 코드 작성
-  NetworkClient.java 클래스 파일 참고하기
-  앞으로 통신할때마다 복붙해서 가져다 사용하면 됨

<br>

#### 2. 검색하기
-  먼저 핸드폰의 위치를 가져오기 위해, 시스템 서비스로부터 <b> locationManager </b>를 받아오기

   그리고 실시간으로 위치를 잡기 위해 <b> locationListener </b>를 만들기

       locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // 여기에 위치 로직 작성
                lat = location.getLatitude();
                lng = location.getLongitude();
                isLocationReady = true; 
            }
        };
<br>
-  앱 실행 시, <b> 위치 기반 서비스 권한 수락받기 </b>

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

<br>
-  검색 버튼을 클릭하면, <b> isLocationReady이 true 인지 확인</b>하고 아니라면 Snackbar 띄우기(예외처리)
-  true라면 keyword 변수에 입력한 검색어 저장하기

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

<br>
-  <b> getNetworkData </b>에서 <b> Retrofit api </b>를 실행하기

   <b> Call </b>해서 받아온 결과값들을 <b> placeArrayList에 넣고, adapter를 실행 </b>시킨다

        private void getNetworkData() {
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
                } else {}
            }
            @Override
            public void onFailure(retrofit2.Call<PlaceList> call, Throwable t) {

            }
        });
<br>

#### 3. PlaceAdapter
-  <b> placeArrayList </b>는 model 파일에 있는 <b> Place 클래스 파일</b>의 값을 갖고 있음
-  <b> onBindViewHolder </b> 부분에서 <b> holder </b>를 사용하여 값을 넣어 화면에 보여주기

        @Override
        public void onBindViewHolder(@NonNull PlaceAdapter.ViewHolder holder, int position) {
        Place place = placeArrayList.get(position);

        // 장소와 주소 이름이 없을 때를 대비하는 예외처리
        if (place.name == null){
            holder.txtName.setText("상점명 없음");
        }else {
            holder.txtName.setText(place.name); // 장소이름
        }

        if (place.vicinity == null){
            holder.txtVicinity.setText("주소 없음");
        }else {
            holder.txtVicinity.setText(place.vicinity); // 주소
        }
    }
   
-  화면에서 검색 결과가 뜬 카드뷰를 눌렀을 때, <b> 몇번째 카드뷰를 눌렀는지 확인하고 MapActivity에게 정보를 보내주기</b>

         cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 몇번째 카드뷰를 눌렀는지 확인
                    int index = getAdapterPosition();
                    Place place = placeArrayList.get(index);

                    // 맵 액티비티에게 보내주기
                    Intent intent = new Intent(context, MapActivity.class);
                    intent.putExtra("place", place);
                    context.startActivity(intent);
                }
            });

#### 4. MapActivity
-  PlaceAdapter가 보낸 place 정보를 받아오고, 위도와 경도값을 가지고 마커로 표시하기

         place = (Place) getIntent().getSerializableExtra("place");
         mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // 유저가 누른 플레이스의 위도, 경도 값을 가지고, 마커로 표시하기
                // 해당 플레이스의 정보는 어뎁터에게 받았음

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
