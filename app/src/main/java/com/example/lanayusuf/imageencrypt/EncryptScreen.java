package com.example.lanayusuf.imageencrypt;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by LanaYusuf on 2/7/2017.
 */

public class EncryptScreen extends AppCompatActivity {


    final private int REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    PictureCoder pc = new PictureCoder();

    ImageView imageView;
    Drawable drawable;
    Bitmap bitmap;
    String imagePath;
    Uri uri;

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
                pc.encode();
            }
        });


        final Button decode = (Button) findViewById(R.id.decode);
        decode.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // decode button has been pressed
                pc.decode();
            }
        });


        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // save button has been pressed

                // Check Version
                // If Version is 23 or greater check that user has given application permission
                if(Build.VERSION.SDK_INT >= 23){
                    // Check that user has given application permission
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        // Application does not have permission
                        // Request permission to access external storage
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                }

                drawable = getResources().getDrawable(R.drawable.security);
                bitmap = ((BitmapDrawable)drawable).getBitmap();
                // Insert image into Android Image Gallery
                imagePath = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Security","Security");
                uri = Uri.parse(imagePath);
                pc.save();
            }
        });

        final Button text = (Button) findViewById(R.id.text);
        text.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // text image button has been pressed


                //temp solution using image saved in resources
                //to do: save encoded image, get path on sd card, parse path to get uri

                Intent sendText = new Intent(Intent.ACTION_SEND);
                sendText.putExtra(Intent.EXTRA_STREAM, Uri.parse("android.resource://com.example.lanayusuf.imageencrypt/drawable/security"));
                sendText.setType("image/png");
                startActivity(sendText);
            }
        });

    }
}
