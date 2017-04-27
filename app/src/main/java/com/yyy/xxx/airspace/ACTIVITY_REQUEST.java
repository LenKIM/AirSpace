package com.yyy.xxx.airspace;

/**
 * Created by len on 2017. 3. 29..
 */

public interface ACTIVITY_REQUEST {

    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_IMAGE_ALBUM = 2;
    int REQUEST_IMAGE_CROP = 3;


    int READ_REQEST_CODE = 4;
    int REQUEST_DATE = 5;
    int RESULT_OK_INPUT_BOARD = 6;
    int CONFIRM_REQUEST = 100;

    String DIALOG_DATE = "DialogDate";

    String EXTRA_BOARD_ID = "com.yyy.xxx.airspace.board_id";
}
