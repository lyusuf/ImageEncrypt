package com.example.lanayusuf.imageencrypt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by Master McCord on 2/19/2017.
 */

public class PictureCoder {

    //variable which is the picture

    Bitmap picture = BitmapFactory.decodeFile("app/res/drawable/security.png");

    /*
     * Potential parameters: picture (as filepath String or Bitmap object, Bitmap preferred)
     *                       message (as String)
     *
     * Potential return: Bitmap or nothing (save Bitmap to PNG in function, return Bitmap preferred)
     *
     * TODO: Randomize message placement
     *
     */

    void encode(){

        String message = "This is the message to encode";

        for (int i = 0; i < picture.getHeight(); i++) {

            for (int j = 0; j < picture.getWidth(); j++) {

                int picColorInt = picture.getPixel(i, j);

                Color thisPixelColor = new Color();

                int alpha = thisPixelColor.alpha(picColorInt);

                int red = thisPixelColor.red(picColorInt);

                int green = thisPixelColor.green(picColorInt);

                int blue = thisPixelColor.blue(picColorInt);

                if (red%2 != 0) {

                    red -= 1;

                }

                picColorInt = thisPixelColor.argb(alpha, red, green, blue);

                picture.setPixel(i, j, picColorInt);

            }

        }

        int messagePos = 0;

        int row = 0;

        while (messagePos < message.length() && row < picture.getHeight()) {

            int col = 0;

            while (messagePos < message.length() && col < picture.getWidth()) {

//                int overflow = 0;
//
//                if (col + 1 == picture.getWidth()) {
//
//                    overflow = 1;
//
//                } else if (col + 2 == )
//
//                int pix1Int = picture.getPixel(row, col);                

                col++;
            }

            row++;
        }

        Log.d("tag", "encode was called");
    }


    void decode(){
        Log.d("tag", "decode was called");
    }

    void save(){

        Log.d("tag", "save was called");
    }



    //picture = new picture
}
