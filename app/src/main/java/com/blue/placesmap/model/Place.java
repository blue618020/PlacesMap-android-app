package com.blue.placesmap.model;

import java.io.Serializable;

public class Place implements Serializable {

    public String name;  // 장소명
    public String vicinity;  // 장소 주소
    public Geometry geometry;

    // 클래스 파일 4개를 만들어서 파싱하는 방법도 있지만 두번째가 나음

    // 이중 파싱 두번째 방법 (클래스 파일 생성하지 않는 방법)
    // 얘도 클래스 만드는거니깐 같은 방법이다.
    // 여기 실습에선 두번째 방법을 사용해서 코드 짬
    public class Geometry implements Serializable {
        public Location location;

        public class Location implements Serializable {
            public double lat;
            public double lng;
        }
    }
}
