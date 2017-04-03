package com.example.lanayusuf.imageencrypt;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.util.Log;
import android.widget.ImageView;

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

    /*aramaram
     * Assumes ascii is in [0, 255].
     *
     * Returns the binary representationaram
     * of the integer as an array of ints.
     *
     */
    public int[] getBits(int ascii) {
        int[] bits = new int[8];

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

        // Need to fill in rest of pixels with original image pixels

        while (row < numRows) {

            int col = 0;

            while (col < numCols) {

                if (messagePos >= message.length()) {

                    int pixInt = picture.getPixel(row, col);

                    outPicture.setPixel(row, col, pixInt);

                    col++;

                } else {

                    // Keep track of the position of the pixels encoding the character

                    int[] pixPos1 = {row, col};

                    int[] pixPos2 = {-1, -1};

                    int[] pixPos3 = {-1, -1};

                    if (col + 1 == numCols) {

                        // Both pixels overflow

                        row++;

                        col = 0;

                        pixPos2[0] = row;

                        pixPos2[1] = col;

                        if (col + 1 == numCols) {

                            row++;

                            col = 0;

                            pixPos3[0] = row;

                            pixPos3[1] = col;

                        } else {

                            col++;

                            pixPos3[0] = row;

                            pixPos3[1] = col;

                        }


                    } else if (col + 2 == numCols) {

                        // Only 1 pixel overflows

                        col++;

                        pixPos2[0] = row;

                        pixPos2[1] = col;

                        row++;

                        col = 0;

                        pixPos3[0] = row;

                        pixPos3[1] = col;

                    } else {

                        // No overflow

                        col++;

                        pixPos2[0] = row;

                        pixPos2[1] = col;

                        col++;

                        pixPos3[0] = row;

                        pixPos3[1] = col;

                    }

                    int[] charAsciiBin = getBits((int) message.charAt(messagePos));

                    messagePos++;

                    int pix1Int = picture.getPixel(pixPos1[0], pixPos1[1]);

                    int pix2Int = picture.getPixel(pixPos2[0], pixPos2[1]);

                    int pix3Int = picture.getPixel(pixPos3[0], pixPos3[1]);

                    Color pix1Color = new Color();

                    int alpha1 = pix1Color.alpha(pix1Int);

                    int red1 = pix1Color.red(pix1Int);

                    int green1 = pix1Color.green(pix1Int);

                    int blue1 = pix1Color.blue(pix1Int);

                    Color pix2Color = new Color();

                    int alpha2 = pix2Color.alpha(pix2Int);

                    int red2 = pix2Color.red(pix2Int);

                    int green2 = pix2Color.green(pix2Int);

                    int blue2 = pix2Color.blue(pix2Int);

                    Color pix3Color = new Color();

                    int alpha3 = pix3Color.alpha(pix3Int);

                    int red3 = pix3Color.red(pix3Int);

                    int green3 = pix3Color.green(pix3Int);

                    int blue3 = pix3Color.blue(pix3Int);

                    red1++;

                    if (charAsciiBin[0] == 0 && (green1 % 2 != 0)) {

                        green1--;

                    } else if (charAsciiBin[0] == 1 && (green1 % 2 == 0)) {

                        green1++;

                    }

                    if (charAsciiBin[1] == 0 && (blue1 % 2 != 0)) {

                        blue1--;

                    } else if (charAsciiBin[1] == 1 && (blue1 % 2 == 0)) {

                        blue1++;

                    }

                    if (charAsciiBin[2] == 0 && (red2 % 2 != 0)) {

                        red2--;

                    } else if (charAsciiBin[2] == 1 && (red2 % 2 == 0)) {

                        red2++;

                    }

                    if (charAsciiBin[3] == 0 && (green2 % 2 != 0)) {

                        green2--;

                    } else if (charAsciiBin[3] == 1 && (green2 % 2 == 0)) {

                        green2++;

                    }

                    if (charAsciiBin[4] == 0 && (blue2 % 2 != 0)) {

                        blue2--;

                    } else if (charAsciiBin[4] == 1 && (blue2 % 2 == 0)) {

                        blue2++;

                    }

                    if (charAsciiBin[5] == 0 && (red3 % 2 != 0)) {

                        red3--;

                    } else if (charAsciiBin[5] == 1 && (red3 % 2 == 0)) {

                        red3++;

                    }

                    if (charAsciiBin[6] == 0 && (green3 % 2 != 0)) {

                        green3--;

                    } else if (charAsciiBin[6] == 1 && (green3 % 2 == 0)) {

                        green3++;

                    }

                    if (charAsciiBin[6] == 0 && (blue3 % 2 != 0)) {

                        blue3--;

                    } else if (charAsciiBin[6] == 1 && (blue3 % 2 == 0)) {

                        blue3++;

                    }

                    int outPicInt1 = pix1Color.argb(alpha1, red1, green1, blue1);

                    int outPicInt2 = pix2Color.argb(alpha2, red2, green2, blue2);

                    int outPicInt3 = pix2Color.argb(alpha3, red3, green3, blue3);

                    outPicture.setPixel(pixPos1[0], pixPos1[1], outPicInt1);

                    outPicture.setPixel(pixPos2[0], pixPos2[1], outPicInt2);

                    outPicture.setPixel(pixPos3[0], pixPos3[1], outPicInt3);

                    col++;

                }
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


    void save(Context context){

        ImageView imageView;
        Drawable drawable;
        Bitmap bitmap;
        String imagePath;
        Uri uri;

        int REQUEST_WRITE_EXTERNAL_STORAGE = 123;

        if(Build.VERSION.SDK_INT >= 23){
            if(ContextCompat.checkSelfPermission(context.getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context.getApplicationContext(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }

        drawable = context.getResources().getDrawable(R.drawable.security);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        imagePath = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"Security","Security");
        uri = Uri.parse(imagePath);


        Log.d("tag", "save was called");
    }

}
