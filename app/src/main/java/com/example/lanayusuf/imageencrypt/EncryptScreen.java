package com.example.lanayusuf.imageencrypt;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
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
import android.widget.EditText;

import java.io.FileDescriptor;
import java.io.IOException;


public class EncryptScreen extends AppCompatActivity {

    PictureCoder pc = new PictureCoder();

    private static int REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    private static int REQUEST_READ_EXTERNAL_STORAGE = 124;
    private static int SELECT_ENCODE_PHOTO = 1;
    private static int SELECT_DECODE_PHOTO = 2;
    private static String SELECTED_ENCODE_IMAGE_URI = "";
    private static String SELECTED_DECODE_IMAGE_URI = "";
    private static Bitmap SELECTED_ENCODE_IMAGE_BITMAP = null;
    private static Bitmap SELECTED_DECODE_IMAGE_BITMAP = null;

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
            }
        });


        final Button decode = (Button) findViewById(R.id.decode);
        decode.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // encode button has been pressed

                // Check Android Version
                // If Version is 23 or greater check that user has given application permission
                if(Build.VERSION.SDK_INT >= 23){
                    // Check that user has given application permission (Read External Storage)
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        // Application does not have permission (Write External Storage)
                        // Request permission to access external storage
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }

                Intent selectDecodeImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(selectDecodeImage,SELECT_DECODE_PHOTO);
            }
        });

        final Button text = (Button) findViewById(R.id.text);
        text.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // text image button has been pressed

                //send text
                Intent sendText = new Intent(Intent.ACTION_SEND);
                sendText.putExtra(Intent.EXTRA_STREAM, Uri.parse(SELECTED_ENCODE_IMAGE_URI));
                sendText.setType("image/png");
                startActivity(sendText);
            }
        });

        final Button email = (Button) findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // email image button has been pressed

                //send email
                Intent sendEmail = new Intent(Intent.ACTION_SEND);
                sendEmail.setType("application/image");
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT,"Image Encrypt");
                sendEmail.putExtra(Intent.EXTRA_TEXT, "Secret message in picture.");
                sendEmail.putExtra(Intent.EXTRA_STREAM, Uri.parse(SELECTED_ENCODE_IMAGE_URI));
                startActivity(sendEmail);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Bitmap bitmap = null;

        // Call for selected image bitmap for ENCODE
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                if(data != null){
                    Uri imageUriSelected = data.getData();
                    // Set image uri that will be used in text message feature
                    SELECTED_ENCODE_IMAGE_URI = imageUriSelected.toString();
                    Log.d("URI IMAGE",SELECTED_ENCODE_IMAGE_URI);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(imageUriSelected, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    try{
                        bitmap = getBitmapFromUri(imageUriSelected);
                        SELECTED_ENCODE_IMAGE_BITMAP = bitmap;
                        Log.d("tag", "ENCODE BITMAP: " + SELECTED_ENCODE_IMAGE_BITMAP.toString());

                        final EditText encodedMessage = (EditText) findViewById(R.id.encodeEditText);
                        String message = encodedMessage.getText().toString();
                        pc.encode(getApplicationContext(), bitmap, message);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

        }

        // Call for selected image bitmap for DECODE
        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUriSelected = data.getData();
                SELECTED_DECODE_IMAGE_URI = imageUriSelected.toString();
                Log.d("URI DIMAGE", SELECTED_DECODE_IMAGE_URI);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageUriSelected,filePathColumn,null,null,null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                try{
                    bitmap = getBitmapFromUri(imageUriSelected);
                    SELECTED_DECODE_IMAGE_BITMAP = bitmap;
                    Log.d("tag", "DECODE BITMAP: " + SELECTED_DECODE_IMAGE_BITMAP.toString());

                    final EditText decodedMessage = (EditText) findViewById(R.id.decodeMessageEditText);
                    String message = pc.decode(getApplicationContext(), SELECTED_DECODE_IMAGE_BITMAP);
                    decodedMessage.setText(message);
                }catch(IOException e){
                    e.printStackTrace();
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
