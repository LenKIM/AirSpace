package com.yyy.xxx.airspace;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yyy.xxx.airspace.Model.Board;
import com.yyy.xxx.airspace.Model.BoardLab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.graphics.Bitmap.CompressFormat;
import static android.graphics.Bitmap.createBitmap;

/**
 *
 * Created by len on 2017. 3. 23..
 *
 */

public class AddBoardActivity extends AppCompatActivity implements ACTIVITY_REQUEST,
                                                        DatePickerFragment.OnDateListener{

    private static final String TAG = AddBoardActivity.class.getName();

    private Board mBoard;

    @BindView(R.id.edit_name)
    EditText name_Edit;

    @BindView(R.id.edit_contents)
    EditText desc_Edit;

    @BindView(R.id.imageView_Captured)
    ImageView mImageView;

    @BindView(R.id.date_space)
    TextView mTextView;

    String mCurrentPhotoPath;

    Uri photoURI;

    @Override
    public void onReceivedDate(Date date) {
        mBoard.setDate(date);
        mTextView.setText(mBoard.getDate());
    }

    @OnClick(R.id.btn_add_cancel) void onCancelClick(){
        finish();
    }

    @OnClick(R.id.btn_select_date) void onClickSelectButton(){
        FragmentManager manager = getSupportFragmentManager();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(mBoard.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DatePickerFragment dialog = DatePickerFragment.newInstance(date);
        dialog.show(manager, DIALOG_DATE);
    }

    @OnClick(R.id.btn_add_confirm) void onConfirmClick(){
        //UUID는 자동으로 생성되므로 여기서 따로 추가해줄필요가 없다.
        Log.d(TAG, "새롭게 생긴 또는 이미 존재하는 UUID는 " + mBoard.getUUID() + "");
        mBoard.setTitle(name_Edit.getText().toString());
        mBoard.setDescription(desc_Edit.getText().toString());


        Intent getIntent = getIntent();

        if (getIntent.hasExtra("latitude")) {
            String latitude = getIntent.getStringExtra("latitude");
            String longitude = getIntent.getStringExtra("longitude");
            mBoard.setMapPoint(latitude + "/" + longitude);
            BoardLab.getBoardLab(getApplicationContext()).insertBoard(mBoard);
        } else {
            BoardLab.getBoardLab(getApplicationContext()).updateBoard(mBoard);
        }

        if (BuildConfig.DEBUG){
            Log.d(TAG, mBoard.getTitle() + "   " + mBoard.getDescription() + "   " + mBoard.getDate());
            Log.d(TAG, mBoard.getUUID() + "   " + mBoard.getMapPoint() + "   ");
        }

        //BoardLab의 인스턴트를 만들어 저장하는 부분!

        setResult(RESULT_OK_INPUT_BOARD);

        //화면 전환시키는것.
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        이렇게하면, 화면이 중첩되어 항상 더 하나가 생긴다.
        finish();
    }

    @OnClick(R.id.imageView_Captured) void onImageOnClick(){

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
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
                        doTakeAlbumAction();

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

    private void doTakeAlbumAction() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
// /-1/1/content://media/external/images/media/235/ORIGINAL/NONE/912977700
//        albumIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(albumIntent, REQUEST_IMAGE_ALBUM);
    }

    private void doTakePictureAction() {
        //사진촬영후 가져오기
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        photoURI = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }


    /**
     *  add the photo to Gallery when you take a photo
     */
    private void doGalleryAddPic() {
        Intent mediaScanIntent =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }

    public Bitmap getBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inInputShareable = true;
        options.inDither=false;
        options.inTempStorage=new byte[32 * 1024];
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;

        File f = new File(mCurrentPhotoPath);

        FileInputStream fs=null;
        try {
            fs = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }

        Bitmap bm = null;

        try {
            if(fs!=null) bm= BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public static Bitmap rotate(Bitmap image, int degrees)
    {
        if(degrees != 0 && image != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float)image.getWidth(), (float)image.getHeight());

            try
            {
                Bitmap b = createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);

                if(image != b)
                {
                    image.recycle();
                    image = b;
                }

                image = b;
            }
            catch(OutOfMemoryError ex)
            {
                ex.printStackTrace();
            }
        }
        return image;
    }

    public void saveBitmap(Bitmap bitmap) {
        File file = new File(mCurrentPhotoPath);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.compress(CompressFormat.JPEG, 100, out) ;
        try {
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mTextView.setText(mBoard.getDate());
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_IMAGE_ALBUM: {

                    photoURI = data.getData();
                    Log.d(TAG, photoURI.getPath());

                    mBoard.setPhotoUri(photoURI.toString());
                    Glide.with(AddBoardActivity.this)
                            .load(photoURI)
                            .centerCrop()
                            .crossFade()
                            .into(mImageView);
                    break;
                }
//            Coulnt ensure I get extras from EXTRA_OUTPUT
//            So, should find the last pixs
                case REQUEST_IMAGE_CAPTURE: {
                    Uri imgUri = photoURI;
                    mBoard.setPhotoUri(imgUri + "");

                    Glide.with(getApplicationContext())
                            .load(imgUri)
                            .asBitmap()
                            .centerCrop()
                            .into(mImageView);
                    break;
                }
            }
        }
    }

    public static Intent newIntent(Context context, UUID boardid){
        Intent intent = new Intent(context, AddBoardActivity.class);
        intent.putExtra(EXTRA_BOARD_ID, boardid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addboard);
        ButterKnife.bind(this);

        UUID boardId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_BOARD_ID);

        if (boardId == null){
            mBoard = new Board();
            Log.d(TAG, mBoard.getUUID() + "Created");
        }


        try {
            //UUID의 비교는 != 으로
            if (boardId != null) {
                mBoard = BoardLab.getBoardLab(getApplicationContext()).getBoard(boardId);
                mBoard.setMapPoint(mBoard.getMapPoint());
                name_Edit.setText(mBoard.getTitle());
                desc_Edit.setText(mBoard.getDescription());
                Glide.with(getApplicationContext())
                        .load(mBoard.getPhotoUri())
                        .asBitmap()
                        .centerCrop()
                        .into(mImageView);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
