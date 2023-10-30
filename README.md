# 위치 검색 앱

📝 <b> tistory : </b> x

### 🔍 학습 내용
-  구글맵 API를 사용해서 검색결과에 맞는 장소명과 주소 목록 리스트를 띄우기 
-  화면에 위치가 어디인지 지도 띄우기

### 💻 실습
-  핸드폰의 위치를 가져오기 위해, 시스템 서비스로부터 locationManager를 받아오기

   그리고 실시간으로 위치를 잡기 위해 locationListener를 만들기

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

-  앱 실행 시, 위치 기반 서비스 권한 수락받기

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

-  검색 버튼을 클릭하면, isLocationReady이 true 인지 확인하고 아니라면 Snackbar 띄우기(예외처리)
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

-  getNetworkData에서 Retrofit api를 실행하기

  Call 해서 받아온 결과값들을 placeArrayList에 넣고, adapter를 실행시킨다

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
