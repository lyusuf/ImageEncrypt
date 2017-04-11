package com.example.lanayusuf.imageencrypt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class PictureCoder {

    /*
     * Assumes ascii is in [0, 255].
     * Returns the binary representation of the integer as an array of ints.
     *
     * Note: The integer in the 0th position is the most significant bit (2^7)
     * while the integer in the 7th position is the least significant bit (2^0).
     *
     */
    private int[] getBits(int ascii) {
        int[] bits = new int[8];

        int bit = 0;
        int a = 0;
        for (int exp = 7; exp >= 0; exp--) {
            a = 1 << exp; // 2 to the power exp
            bit = ascii / a;
            ascii = ascii % a;
            bits[7 - exp] = bit;
        }

        return(bits);
    }

    /*
     * Returns the integer given the binary representation array of ints.
     */
    private int undoBits(int[] bits) {
        int ascii = 0;
        for (int exp = 0; exp < bits.length; exp++) {
            int a = 1 << exp; // 2 to the power exp
            int bit = bits[7 - exp];
            ascii += bit * a;
        }
        return(ascii);
    }

    /*
     * Encodes the character in the picture given the mutable Bitmap picture,
     * the character as a binary array, and the positions of the pixels to modify.
     */
    private void encodeChar(Bitmap outPicture, int[] charAsciiBin, int[] pixPos1, int[] pixPos2,
                            int[] pixPos3) {

        int pix1Int = outPicture.getPixel(pixPos1[0], pixPos1[1]);
        int pix2Int = outPicture.getPixel(pixPos2[0], pixPos2[1]);
        int pix3Int = outPicture.getPixel(pixPos3[0], pixPos3[1]);

        int alpha1 = Color.alpha(pix1Int);
        int red1 = Color.red(pix1Int);
        int green1 = Color.green(pix1Int);
        int blue1 = Color.blue(pix1Int);

        int alpha2 = Color.alpha(pix2Int);
        int red2 = Color.red(pix2Int);
        int green2 = Color.green(pix2Int);
        int blue2 = Color.blue(pix2Int);

        int alpha3 = Color.alpha(pix3Int);
        int red3 = Color.red(pix3Int);
        int green3 = Color.green(pix3Int);
        int blue3 = Color.blue(pix3Int);

        red1++;

        // set 2^7 bit
        if (charAsciiBin[0] == 0 && (green1 % 2 != 0)) {
            green1--;
        } else if (charAsciiBin[0] == 1 && (green1 % 2 == 0)) {
            green1++;
        }

        // set 2^6 bit
        if (charAsciiBin[1] == 0 && (blue1 % 2 != 0)) {
            blue1--;
        } else if (charAsciiBin[1] == 1 && (blue1 % 2 == 0)) {
            blue1++;
        }

        // set 2^5 bit
        if (charAsciiBin[2] == 0 && (red2 % 2 != 0)) {
            red2--;
        } else if (charAsciiBin[2] == 1 && (red2 % 2 == 0)) {
            red2++;
        }

        // set 2^4 bit
        if (charAsciiBin[3] == 0 && (green2 % 2 != 0)) {
            green2--;
        } else if (charAsciiBin[3] == 1 && (green2 % 2 == 0)) {
            green2++;
        }

        // set 2^3 bit
        if (charAsciiBin[4] == 0 && (blue2 % 2 != 0)) {
            blue2--;
        } else if (charAsciiBin[4] == 1 && (blue2 % 2 == 0)) {
            blue2++;
        }

        // set 2^2 bit
        if (charAsciiBin[5] == 0 && (red3 % 2 != 0)) {
            red3--;
        } else if (charAsciiBin[5] == 1 && (red3 % 2 == 0)) {
            red3++;
        }

        // set 2^1 bit
        if (charAsciiBin[6] == 0 && (green3 % 2 != 0)) {
            green3--;
        } else if (charAsciiBin[6] == 1 && (green3 % 2 == 0)) {
            green3++;
        }

        // set 2^0 bit
        if (charAsciiBin[7] == 0 && (blue3 % 2 != 0)) {
            blue3--;
        } else if (charAsciiBin[7] == 1 && (blue3 % 2 == 0)) {
            blue3++;
        }

        int outPicInt1 = Color.argb(alpha1, red1, green1, blue1);
        int outPicInt2 = Color.argb(alpha2, red2, green2, blue2);
        int outPicInt3 = Color.argb(alpha3, red3, green3, blue3);

        outPicture.setPixel(pixPos1[0], pixPos1[1], outPicInt1);
        outPicture.setPixel(pixPos2[0], pixPos2[1], outPicInt2);
        outPicture.setPixel(pixPos3[0], pixPos3[1], outPicInt3);

    }

    /*
     * Potential parameters: picture (as filepath String or Bitmap object, Bitmap preferred)
     *                       message (as String)
     *
     * Potential return: Bitmap or nothing (save Bitmap to PNG in function, return Bitmap preferred)
     *
     */
    void encode(Context ctx, Bitmap bmp, String message){

        Log.d("tag", "encode was called");

        // get picture

        Resources res = ctx.getResources();
        int resId = R.drawable.security;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inMutable = true;
        options.inPremultiplied = false;
        Bitmap picture = BitmapFactory.decodeResource(res, resId, options);

        int numRows = picture.getHeight();
        int numCols = picture.getWidth();
        int numPixels = numRows * numCols;

        int maxRange = numPixels - 3;

        int pixelsPerChar = 3;
        int availableChars = numPixels / pixelsPerChar;

        Log.d("number of rows", Integer.toString(numRows));
        Log.d("number of cols", Integer.toString(numCols));
        Log.d("number of pixels", Integer.toString(numPixels));
        Log.d("maximum characters", Integer.toString(availableChars));

        // get message

        //String message = "This isn't the message to decode";

        // "zero" out image

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {

                int picColorInt = picture.getPixel(i, j);
                int alpha = Color.alpha(picColorInt);
                int red = Color.red(picColorInt);
                int green = Color.green(picColorInt);
                int blue = Color.blue(picColorInt);

                // This floors the red value for every pixel to be even
                if (red%2 != 0) {
                    red -= 1;
                }



                picColorInt = Color.argb(alpha, red, green, blue);
                picture.setPixel(i, j, picColorInt);

            }
        }

        // encode message

        SortedSet<Integer> pixelsUsed = new TreeSet<>();

        // Need to make sure pixels don't overlap

        Set<Integer> exclusions = new HashSet<>();

        while (pixelsUsed.size() < message.length()) {

            int randPix = ThreadLocalRandom.current().nextInt(0, maxRange + 1);

            if (exclusions.add(randPix)) {

                exclusions.add(randPix-1);
                exclusions.add(randPix-2);
                exclusions.add(randPix+1);
                exclusions.add(randPix+2);

                pixelsUsed.add(randPix);

            }

        }

        for (int s : pixelsUsed) {

            Log.d("pixel positions encoded", Integer.toString(s));

        }

        int i = 0;

        while (pixelsUsed.size() > 0) {

            int pixelToSet = pixelsUsed.first();

            pixelsUsed.remove(pixelToSet);

            int charAscii = (int) message.charAt(i);
            int[] charAsciiBin = getBits(charAscii);

            int[] pixPos1 = {(pixelToSet/numCols), pixelToSet%numCols};

            int[] pixPos2 = {((pixelToSet+1)/numCols), (pixelToSet+1)%numCols};

            int[] pixPos3 = {((pixelToSet+2)/numCols), (pixelToSet+2)%numCols};

            encodeChar(picture, charAsciiBin, pixPos1, pixPos2, pixPos3);

            i++;

        }

        Log.d("tag", "encode had ended");

        // decode(ctx, picture);
    }

    /*
     * Decodes the character in the picture given the Bitmap picture
     * and the positions of the pixels to decode.
     */
    private char decodeChar(Bitmap picture, int[][] curPositions) {

        // assumes the character is stored in the first 3 pixels
        int pix1Int = picture.getPixel(curPositions[0][0], curPositions[0][1]);
        int pix2Int = picture.getPixel(curPositions[1][0], curPositions[1][1]);
        int pix3Int = picture.getPixel(curPositions[2][0], curPositions[2][1]);

        int green1 = Color.green(pix1Int);
        int blue1 = Color.blue(pix1Int);

        int red2 = Color.red(pix2Int);
        int green2 = Color.green(pix2Int);
        int blue2 = Color.blue(pix2Int);

        int red3 = Color.red(pix3Int);
        int green3 = Color.green(pix3Int);
        int blue3 = Color.blue(pix3Int);

        int[] charAsciiBin = {0, 0, 0, 0, 0, 0, 0, 0};

        charAsciiBin[0] = green1 % 2;
        charAsciiBin[1] = blue1 % 2;
        charAsciiBin[2] = red2 % 2;
        charAsciiBin[3] = green2 % 2;
        charAsciiBin[4] = blue2 % 2;
        charAsciiBin[5] = red3 % 2;
        charAsciiBin[6] = green3 % 2;
        charAsciiBin[7] = blue3 % 2;

        int charAscii = undoBits(charAsciiBin);
        return((char) charAscii);

    }

    /*
     * Checks to see if a character is stored in the current position.
     */
    private boolean charFlag(Bitmap picture, int[][] curPositions) {
        // assumes the flag is set in the first pixel
        int pix1Int = picture.getPixel(curPositions[0][0], curPositions[0][1]);
        int red1 = Color.red(pix1Int);
        return(red1 % 2 == 1);
    }

    /*
     * Potential parameters: picture (as filepath String or Bitmap object, Bitmap preferred)
     * Return: message (as String)
     *
     */
    String decode(Context ctx, Bitmap bmp){
        Log.d("tag", "decode was called");

        // get picture

        Resources res = ctx.getResources();
        int resId = R.drawable.security;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap picture = BitmapFactory.decodeResource(res, resId, options);
        //picture = bmp; /* TODO: remove this line once the right image is imported */

        int numRows = picture.getHeight();
        int numCols = picture.getWidth();
        int numPixels = numRows * numCols;

        Log.d("number of rows", Integer.toString(numRows));
        Log.d("number of cols", Integer.toString(numCols));
        Log.d("number of pixels", Integer.toString(numPixels));

        // sets up the pixPositions matrix

        int pixelsPerChar = 3;
        int[][] pixPositions = new int[pixelsPerChar][2];

        // loops through positions in the image

        String message = "";
        int pixelToCheck = 0;
        while (pixelToCheck <= numPixels - pixelsPerChar) {
            pixPositions[0][0] = pixelToCheck / numCols;
            pixPositions[0][1] = pixelToCheck % numCols;

            pixPositions[1][0] = (pixelToCheck + 1) / numCols;
            pixPositions[1][1] = (pixelToCheck + 1) % numCols;

            pixPositions[2][0] = (pixelToCheck + 2) / numCols;
            pixPositions[2][1] = (pixelToCheck + 2) % numCols;

            if (charFlag(picture, pixPositions)) {
                char curChar = decodeChar(picture, pixPositions);
                message += curChar;

                pixelToCheck += pixelsPerChar;
            } else {
                pixelToCheck += 1;
            }
        }
        Log.d("decoded message", message);

        Log.d("tag", "decode has ended");

        return message;
    }



    void save(){

        ImageView imageView;
        Drawable drawable;
        String imagePath;
        Uri uri;


//        drawable = context.getResources().getDrawable(R.drawable.security);
//        bitmap = ((BitmapDrawable)drawable).getBitmap();
        //imagePath = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"Security","Security");
        //uri = Uri.parse(imagePath);
        Log.d("tag", "save was called");
    }

}
