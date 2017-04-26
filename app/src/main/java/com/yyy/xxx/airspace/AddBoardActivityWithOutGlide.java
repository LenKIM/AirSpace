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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static android.provider.MediaStore.EXTRA_OUTPUT;

/**
 *
 * Created by len on 2017. 3. 23..
 *
 */

public class AddBoardActivityWithOutGlide extends AppCompatActivity implements ACTIVITY_REQUEST,
                                                        DatePickerFragment.OnDateListener{

    private static final String TAG = AddBoardActivityWithOutGlide.class.getName();
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

    public AddBoardActivityWithOutGlide() {
            mBoard = new Board();
            Log.d(TAG, mBoard.getUUID() + " Created");
    }

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
        Log.d(TAG, "새롭게 생긴 UUID는 " + mBoard.getUUID() + "");
        mBoard.setTitle(name_Edit.getText().toString());
        mBoard.setDescription(desc_Edit.getText().toString());

        Intent getIntent = getIntent();
        String latitude = getIntent.getStringExtra("latitude");
        String longitude = getIntent.getStringExtra("longitude");

        mBoard.setMapPoint(latitude+ "/" + longitude);

        if (BuildConfig.DEBUG){
            Log.d(TAG, mBoard.getTitle() + "   " + mBoard.getDescription() + "   " + mBoard.getDate());
            Log.d(TAG, mBoard.getUUID() + "   " + mBoard.getMapPoint() + "   ");
        }

        //BoardLab의 인스턴트를 만들어 저장하는 부분!
        BoardLab.getBoardLab(getApplicationContext()).insertBoard(mBoard);
        setResult(RESULT_OK_INPUT_BOARD);

        //화면 전환시키는것.
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

        AlertDialog.Builder builder = new AlertDialog.Builder(AddBoardActivityWithOutGlide.this);
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
        Intent albumIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
// /-1/1/content://media/external/images/media/235/ORIGINAL/NONE/912977700
//        albumIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(albumIntent, REQUEST_IMAGE_ALBUM);
    }

    private void doTakePictureAction() {
        //사진촬영후 가져오기
        Intent imageCaptureIntent = new Intent(ACTION_IMAGE_CAPTURE);
        //Ensure that thers's a camera activity to handle the intent
        if (imageCaptureIntent.resolveActivity(getPackageManager()) != null) {
            //create photo file where it should go
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Continue only if the File was successfully created
            if (photoFile != null){
                photoURI = FileProvider.getUriForFile(this,
                        "com.yyy.xxx.airspace",
                        photoFile);

                Log.d("photoURL", photoURI + "");
                imageCaptureIntent.putExtra(EXTRA_OUTPUT, photoURI);
                startActivityForResult(imageCaptureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
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

    private void cropImage(Uri contentUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(contentUri, "image/*");
        //set crop properties
//        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    public void rotatePhoto(Uri contentUri) {
        ExifInterface exif;
        try {
            if(mCurrentPhotoPath == null) {
                mCurrentPhotoPath = contentUri.getPath();
            }
            exif = new ExifInterface(mCurrentPhotoPath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            if(exifDegree != 0) {
                Bitmap bitmap = getBitmap();
                Bitmap rotatePhoto = rotate(bitmap, exifDegree);
                saveBitmap(rotatePhoto);
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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

        if (resultCode == RESULT_OK){
            mTextView.setText(mBoard.getDate());
        }

        if (resultCode == RESULT_OK){
        switch (requestCode) {

            case REQUEST_IMAGE_ALBUM: {
                photoURI = data.getData();
                Log.d(TAG, photoURI.getPath());
//                rotatePhoto(photoURI);
                cropImage(photoURI);
                break;
            }
            case REQUEST_IMAGE_CAPTURE: {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    mImageView.setImageBitmap(bitmap);

                    if (mCurrentPhotoPath != null) {
                        File f = new File(mCurrentPhotoPath);
                        if (f.exists()) {
                            f.delete();
                        }
                        mCurrentPhotoPath = null;
                    }
                }
                break;
            }
            case REQUEST_IMAGE_CROP: {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    mImageView.setImageBitmap(bitmap);

                    if (mCurrentPhotoPath != null) {
                        File f = new File(mCurrentPhotoPath);
                        if (f.exists()) {
                            f.delete();
                            }
                        mCurrentPhotoPath = null;
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     *  이미지 파일 만드는 메소드
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//      Files you save in the directories provided by getExternalFilesDir() or getFilesDir() are deleted when the user uninstalls your app.

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static Intent newIntent(Context context, UUID boardid){
        Intent intent = new Intent(context, AddBoardActivityWithOutGlide.class);
        intent.putExtra(EXTRA_BOARD_ID, boardid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addboard);
        ButterKnife.bind(this);

        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_BOARD_ID);


        try {
            //UUID의 비교는 != 으로
            if (crimeId != null) {
                mBoard = BoardLab.getBoardLab(getApplicationContext()).getBoard(crimeId);
                mBoard.setTitle(mBoard.getTitle());
                mBoard.setDescription(mBoard.getDescription());
                mBoard.setMapPoint(mBoard.getMapPoint());

                name_Edit.setText(mBoard.getTitle());
                desc_Edit.setText(mBoard.getDescription());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
