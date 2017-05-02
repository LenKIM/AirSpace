package com.yyy.xxxx.airspace2.DataBase;

/**
 * Created by len on 2017. 3. 29..
 */

public class BoardSchema {

    public static final class BoardTable {

        public static final String NAME = "board";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String CONTENT = "content";
            public static final String MAPPOINT = "mapPoint";
            public static final String PHOTOURL = "photoUrl";
            //Image는 서버로 보내기
        }
    }
}
