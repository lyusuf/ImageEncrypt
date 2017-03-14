package com.example.lanayusuf.imageencrypt;

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
                pc.decode();
            }
        });


        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                // encode button has been pressed
                pc.save();
            }
        });

    }
}
