package com.yyy.xxx.airspace.DataBase;

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
            //Image는 서버로 보내기
        }
    }
}
