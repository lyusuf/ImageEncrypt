package com.example.lanayusuf.imageencrypt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by LanaYusuf on 2/7/2017.
 */

public class EncryptScreen extends AppCompatActivity {

    PictureCoder pc = new PictureCoder();

    private static int REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    private static int REQUEST_READ_EXTERNAL_STORAGE = 124;
    private static int SELECT_ENCODE_PHOTO = 1;
    private static int SELECT_DECODE_PHOTO = 2;
    private static String SELECTED_IMAGE_URI = "";
    private static Bitmap SELECTED_IMAGE_BITMAP = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encrypt_layout);

        View img = findViewById(R.id.picture);
        img.setBackgroundResource(R.drawable.security);


        final Button encode = (Button) findViewById(R.id.encode);
        encode.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // encode button has been pressed

                // Check Android Version
                // If Version is 23 or greater check that user has given application permission
                if(Build.VERSION.SDK_INT >= 23){
                    // Check that user has given application permission (Write External Storage)
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        // Application does not have permission (Write External Storage)
                        // Request permission to access external storage
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                }

                Intent selectEncodeImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(selectEncodeImage,SELECT_ENCODE_PHOTO);
                pc.encode(getApplicationContext(),SELECTED_IMAGE_BITMAP);
            }
        });


        final Button decode = (Button) findViewById(R.id.decode);
        decode.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // encode button has been pressed

                // Check Android Version
                // If Version is 23 or greater check that user has given application permissions
                if(Build.VERSION.SDK_INT >= 23){
                    // Check that user has given application permission (Read External Storage)
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        // Application does not have permission (Write External Storage)
                        // Request permission to access external storage
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }

                Resources res = getResources();
                int resId = R.drawable.security;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap picture = BitmapFactory.decodeResource(res, resId, options);
//                // Select image from gallery to decode
//                Intent selectDecodeImage = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(selectDecodeImage,SELECT_DECODE_PHOTO);
                pc.decode(getApplicationContext(), picture);
            }
        });


        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // save button has been pressed
                // Check Android Version
                // If Version is 23 or greater check that user has given application permission
                if(Build.VERSION.SDK_INT >= 23){
                    // Check that user has given application permission (Write External Storage)
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        // Application does not have permission (Write External Storage)
                        // Request permission to access external storage
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                }
                pc.save(getApplicationContext(),SELECTED_IMAGE_BITMAP);
            }
        });

        final Button text = (Button) findViewById(R.id.text);
        text.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // text image button has been pressed

                //send text
                Intent sendText = new Intent(Intent.ACTION_SEND);
                sendText.putExtra(Intent.EXTRA_STREAM, Uri.parse("SELECTED_IMAGE_URI"));
                sendText.setType("image/png");
                startActivity(sendText);

                /*
                //send email
                Intent sendEmail = new Intent(Intent.ACTION_SEND);
                sendEmail.setType("application/image");
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"lanayusuf24@gmail.com"});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT,"Image Encrypt");
                sendEmail.putExtra(Intent.EXTRA_TEXT, "Secret message in picture.");
                //picture file needs to be stored in public location/external storage, not in storage that's private to the app
                sendEmail.putExtra(Intent.EXTRA_STREAM, Uri.parse("android.resource://com.example.lanayusuf.imageencrypt/drawable/security"));
                startActivity(sendEmail);
                */

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == SELECT_ENCODE_PHOTO){
            if(resultCode == RESULT_OK){
                if(data != null) {
                    Uri imageUriSelected = data.getData();
                    // Set image uri that will be used in text message feature
                    SELECTED_IMAGE_URI = imageUriSelected.toString();
                    Log.d("URI IMAGE",SELECTED_IMAGE_URI);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(imageUriSelected, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bitmap = null;
                    try{
                        bitmap = getBitmapFromUri(imageUriSelected);
                        SELECTED_IMAGE_BITMAP = bitmap;
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcel = getContentResolver().openFileDescriptor(uri,"r");
        FileDescriptor file = parcel.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(file);
        parcel.close();
        Log.d("BITMAP", image.toString());
        return image;
    }
}
