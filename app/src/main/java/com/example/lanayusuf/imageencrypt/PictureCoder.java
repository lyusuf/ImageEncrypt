package com.example.lanayusuf.imageencrypt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Created by Master McCord on 2/19/2017.
 * Changes made by the Data Analytics Major on 3/11/2017.
 */

public class PictureCoder {

    // Bitmap picture = BitmapFactory.decodeFile("app/src/main/res/drawable/security.png");

    /*
     * Potential parameters: picture (as filepath String or Bitmap object, Bitmap preferred)
     *                       message (as String)
     *
     * Potential return: Bitmap or nothing (save Bitmap to PNG in function, return Bitmap preferred)
     *
     * TODO: Randomize message placement
     *
     */

    /*
     * Assumes ascii is in [0, 255].
     *
     * Returns the binary representation
     * of the integer as an array of ints.
     *
     */
    public int[] getBits(int ascii) {
        int[] bits;
        bits = new int[8];

        int bit = 0;
        int a = 0;
        for (int exp = 7; exp >= 0; exp--) {
            a = 1 << exp; // 2 to the power exp
            bit = ascii / a;
            ascii = ascii % a;
            bits[exp] = bit;
        }

        return(bits);
    }

    void encode(Context ctx){

        /* get picture */

        Resources res = ctx.getResources();
        int resId = R.drawable.security;
        Bitmap picture = BitmapFactory.decodeResource(res, resId);

        int numRows = picture.getHeight();
        int numCols = picture.getWidth();

        Log.d("tag", Integer.toString(numRows)); // 672
        Log.d("tag", Integer.toString(numCols)); // 672

        /* set up mutable Bitmap */

        Bitmap outPicture = Bitmap.createBitmap(numCols, numRows, Bitmap.Config.ARGB_8888);

        /* get message */

        String message = "This is the message to encode";

        /* "zero" out image */

        int count = 0;

        for (int i = 0; i < numRows; i++) {

            for (int j = 0; j < numCols; j++) {

                count++;

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

                outPicture.setPixel(i, j, picColorInt);

            }

        }

        String strCount = Integer.toString(count);
        Log.d("tag", strCount); // 451584 not 256*256 = 65536

        /* encode message */

        int messagePos = 0;

        int row = 0;

        while (messagePos < message.length() && row < numRows) {

            int col = 0;

            while (messagePos < message.length() && col < numCols) {

                int overflow = 0;

                if (col + 1 == numCols) {

                    overflow = 2;

                } else if (col + 2 == numCols) {

                    overflow = 1;

                }

                int pix1Int = outPicture.getPixel(row, col);

                col++;
            }

            row++;
        }

        Log.d("tag", "encode was called");

        // test getBits
        int ascii = 127 - 32 - 2;
        int[] bits = getBits(ascii);
        String s = " ";
        for (int i = 7; i >= 0; i--) {
            s = Integer.toString(bits[i]);
            Log.d("tag", s);
        }

/*        // test context
        String packageName = ctx.getPackageName();
        Log.d("tag", packageName);

        // get picture
        Resources res = ctx.getResources();
        int resId = R.drawable.security;
        Bitmap picture = BitmapFactory.decodeResource(res, resId);
        int currentPixel = picture.getPixel(0, 0);

        Color thisPixelColor = new Color();
        int red = thisPixelColor.red(currentPixel);
        int green = thisPixelColor.green(currentPixel);
        int blue = thisPixelColor.blue(currentPixel);

        String redStr = Integer.toString(red);
        String greenStr = Integer.toString(green);
        String blueStr = Integer.toString(blue);

        Log.d("tag", redStr);
        Log.d("tag", greenStr);
        Log.d("tag", blueStr);*/
    }

    void decode(){
        Log.d("tag", "decode was called");
    }

    void save(){

        Log.d("tag", "save was called");
    }



    //picture = new picture
}
