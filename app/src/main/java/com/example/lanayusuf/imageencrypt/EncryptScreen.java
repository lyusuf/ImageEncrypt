package com.example.lanayusuf.imageencrypt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by LanaYusuf on 2/7/2017.
 */

public class EncryptScreen extends AppCompatActivity {

    PictureCoder pc = new PictureCoder();

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
                pc.encode(getApplicationContext());
            }
        });


        final Button decode = (Button) findViewById(R.id.decode);
        decode.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // encode button has been pressed
                pc.decode(getApplicationContext());
            }
        });


        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // encode button has been pressed
                pc.save(getApplicationContext());
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
