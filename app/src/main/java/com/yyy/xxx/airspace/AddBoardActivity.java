package com.yyy.xxx.airspace;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.yyy.xxx.airspace.Model.Board;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * Created by len on 2017. 3. 23..
 *
 */

public class AddBoardActivity extends AppCompatActivity {

    private static final String TAG = AddBoardActivity.class.getName();

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_iMAGE = 3;
    private static final int READ_REQEST_CODE = 4;

    @BindView(R.id.edit_name)
    EditText name_Edit;

    @BindView(R.id.edit_contents)
    EditText desc_Edit;

    @BindView(R.id.imageView)
    ImageView mImageView;

    Uri mUri;

    interface onRefreshListener{
        void onRefreshClick(Boolean isRefresh);
    }

    private String absoultePath;
    onRefreshListener mOnRefrshListener;

    @OnClick(R.id.btn_add_cancel) void onCancelClick(){
        finish();
    }

    @OnClick(R.id.btn_add_confirm) void onConfirmClick(){
        Board.getInstance().setName(name_Edit.getText().toString());
        Board.getInstance().setDescription(desc_Edit.getText().toString());
        if (!(mImageView.getDrawable() == null)) {
            Board.getInstance().setImage(mImageView.getDrawable());
        }

         getFragmentManager().findFragmentById(R.id.container);

        finish();

    }

    @OnClick(R.id.imageView) void onImageOnClick(){

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED){
            //권한 없음
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, READ_REQEST_CODE);
        } else {
            //권한 있음
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(AddBoardActivity.this);
        Log.d(TAG, "이미지 촬영하기 동작");

         builder.setTitle("사진업로드하기")
                .setPositiveButton("사진촬영", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePictureAction();
                    }

                })
                .setNeutralButton("앨범선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doPickUpAction();
                    }


                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void doTakePictureAction() {
        //사진촬영후 가져오기
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //임시로 사용할 파일의 경로
        String url = "temp_" + String.valueOf(System.currentTimeMillis())+ ".jpg";
        mUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent1.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(intent1, PICK_FROM_CAMERA);
    }

    private void doPickUpAction() {
        //앨범호출
        Intent intent2 = new Intent(Intent.ACTION_PICK);
        intent2.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent2, PICK_FROM_ALBUM);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;


        switch (requestCode){
            case PICK_FROM_ALBUM: {
                //이후의 처리가 카메라와 동일하기 때문에 break를 설정하지 않겠습니다.
                mUri = data.getData();
                Log.d(TAG, "선택한 Uri은" + mUri.getPath().toString());
            }
            case PICK_FROM_CAMERA :
            {
                //이미지를 가져온 이후의 리사이즈할 이미지크기를 결정
                //이후에 이미지 crop application을 호출합니다.
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mUri, "image/*");

                //CROP할 이미지를 200*200크기로 저장
                intent.putExtra("outputX",200); //CROp한 이미지의 x축 크기
                intent.putExtra("outputY",200); //CROP한 이미지의 y축 크기
                intent.putExtra("aspectX",1); //CROP박스의 X축 비율
                intent.putExtra("aspectY",1); //CROP박스의 Y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_iMAGE);
                break;
            }

            case CROP_FROM_iMAGE :
            {
                //크룹이 된 이후의 이미지를 넘겨 받습니다
                //이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                //임시 파일을 삭제합니다
                if (resultCode != RESULT_OK){
                    return;
                }

                final Bundle extras = data.getExtras();

                //CROP된 이미지를 저장하기 위한 FILE경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/AirSpace/" + System.currentTimeMillis() + ".jpg";

                if (extras != null){
                    Bitmap photo = extras.getParcelable("data"); //CROP된 BITMAP
                    Log.d(TAG, photo.toString());
                    mImageView.setImageBitmap(photo);

                    storeCropImage(photo, filePath);//CROP된 이미지를 외부저장소, 앨범에 저장한다
                    absoultePath = filePath;
                    break;
                }

                //임시 파일 삭제
                File f = new File(mUri.getPath());
                if (f.exists())
                {
                    f.delete();
                }
            }
        }
    }

    /**
     * /AirSpace 이라는 디렉토리가 있는지 if문을 체크를 한다
     * 디렉터리 폴더가 없다면 .mkdir()함수호출을 통해 폴더를 만든다
     * 이후에 createNewFile()을 통해 파일을 생성하고 BufferedOutputStream과
     * FileOutputStream복사를 진행한다
     *
     * sendBroadcaset()함수는 폰의 앨범에 크롭된 사진을 갱신하는 함수이다.
     * 이 함수를 쓰지 않는다면 크롭된 사진을 저장해도 앨범에 안보이며 직접 파일 관리자
     * 앱을 통해 폴더를 들어가야만 볼 수 있다.
     *
     *  Bitmap을 저장하는 부분
     * @param photo
     * @param filePath
     */
    private void storeCropImage(Bitmap photo, String filePath) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/AirSpace";
        File directory_AirSpace = new File(dirPath);
        if (!directory_AirSpace.exists()){
            directory_AirSpace.mkdir();
        }
        File copyFile = new File(filePath);
        BufferedOutputStream out = null;
        try {
        copyFile.createNewFile();
        out = new BufferedOutputStream(new FileOutputStream(copyFile));
        photo.compress(Bitmap.CompressFormat.JPEG, 100, out);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

        out.flush();
        out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);
        ButterKnife.bind(this);

    }

}
