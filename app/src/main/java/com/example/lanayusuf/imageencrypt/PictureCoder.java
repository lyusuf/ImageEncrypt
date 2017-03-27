package com.example.lanayusuf.imageencrypt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

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
     * Writes an image held as a Bitmap object
     * to the specified file location filename.
     *
     */
    public void writeImage(Bitmap image, String filename) {
        FileOutputStream out = null;
        try {
            File outFilename = new File(filename); // this part needs fixed
            out = new FileOutputStream(filename);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Encodes the character in the picture given the mutable Bitmap picture,
     * the character as a binary array, and the positions of the pixels to modify.
     */
    private void encodeChar(Bitmap outPicture, int[] charAsciiBin, int[] pixPos1, int[] pixPos2,
                           int[] pixPos3) {

        int pix1Int = outPicture.getPixel(pixPos1[0], pixPos1[1]); // TODO: picture?
        int pix2Int = outPicture.getPixel(pixPos2[0], pixPos2[1]); // TODO: picture?
        int pix3Int = outPicture.getPixel(pixPos3[0], pixPos3[1]); // TODO: picture?

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

    void encode(Context ctx){

        Log.d("tag", "encode was called");

        /* get picture */

        Resources res = ctx.getResources();
        int resId = R.drawable.security;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap picture = BitmapFactory.decodeResource(res, resId, options);

        int numRows = picture.getHeight();
        int numCols = picture.getWidth();
        int numPixels = numRows * numCols;

        int pixelsPerChar = 3;
        int availableChars = numPixels / pixelsPerChar;

        Log.d("tag", Integer.toString(numRows));
        Log.d("tag", Integer.toString(numCols));
        Log.d("tag", Integer.toString(numPixels));
        Log.d("tag", Integer.toString(availableChars));

        /* set up mutable Bitmap */

        Bitmap outPicture = Bitmap.createBitmap(numCols, numRows, Bitmap.Config.ARGB_8888);

        /* get message */

        String message = "This is the message to encode";

        /* "zero" out image */

       for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {

                int picColorInt = picture.getPixel(i, j);
                int alpha = Color.alpha(picColorInt);
                int red = Color.red(picColorInt);
                int green = Color.green(picColorInt);
                int blue = Color.blue(picColorInt);

                /*
                 * This floors the red value for every pixel to be even.
                 * Do we want to only floor the red values for the beginning
                 * of character pixels? (i.e. every 3 pixels)
                 */
                if (red%2 != 0) {
                    red -= 1;
                }

                picColorInt = Color.argb(alpha, red, green, blue);
                outPicture.setPixel(i, j, picColorInt);

            }
        }

//        /* write "zero" image */
//
//        String outFileLocation = "/users/iowner/StudioProjects/securityZero.png";
//        writeImage(outPicture, outFileLocation);

        /* encode message */

        int messagePos = 0;
        int row = 0;

        while (row < numRows) {

            int col = 0;

            while (col < numCols) {

                if (messagePos >= message.length()) {

                    // TODO: fill in rest of pixels with original image pixels?
                    int pixInt = picture.getPixel(row, col);
                    outPicture.setPixel(row, col, pixInt);
                    col++;

                } else {

                    // Keep track of the position of the pixels encoding the character
                    int[] pixPos1 = {row, col};
                    int[] pixPos2 = {-1, -1};
                    int[] pixPos3 = {-1, -1};

                    // TODO: It's possible row will exceed numRow.
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

                    int charAscii = (int) message.charAt(messagePos);
                    int[] charAsciiBin = getBits(charAscii);
                    messagePos++;

                    encodeChar(outPicture, charAsciiBin, pixPos1, pixPos2, pixPos3);

                    col++;

                }
            }

            row++;
        }

        Log.d("tag", "encode had ended");
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
     * Gets the locations of the pixels in the next character
     * given the locations of the pixels in the current character
     * and the picture itself.
     */
    private int[][] getNextPositions(Bitmap picture, int[][] curPositions) {
        int numRows = picture.getHeight();
        int numCols = picture.getWidth();
        int pixelsPerChar = curPositions.length;

        // get location of the last pixel in the current character
        int row = curPositions[pixelsPerChar - 1][0];
        int col = curPositions[pixelsPerChar - 1][1];

        // set the position each pixel in the next character
        int[][] nextPositions = new int[pixelsPerChar][2];
        for (int pixel = 0; pixel < pixelsPerChar; pixel++) {
            // the location of the next pixel is directly after the current pixel
            col++;
            if (col == numCols) {
                row++; // TODO: It's possible row will exceed numRow.
                col = 0;
            }

            nextPositions[pixel][0] = row;
            nextPositions[pixel][1] = col;
        }

        return(nextPositions);
    }

    /*
     * Potential parameters: picture (as filepath String or Bitmap object, Bitmap preferred)
     * Potential return: message (as String)
     *
     * TODO: Randomize message placement
     *
     */
    void decode(Context ctx){
        Log.d("tag", "decode was called");

        /* get picture */

        Resources res = ctx.getResources();
        int resId = R.drawable.security;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap picture = BitmapFactory.decodeResource(res, resId, options);

        int numRows = picture.getHeight();
        int numCols = picture.getWidth();
        int numPixels = numRows * numCols;

        int pixelsPerChar = 3;
        int availableChars = numPixels / pixelsPerChar;

        Log.d("tag", Integer.toString(numRows));
        Log.d("tag", Integer.toString(numCols));
        Log.d("tag", Integer.toString(numPixels));
        Log.d("tag", Integer.toString(availableChars));

        /* TODO: read header? */

        int numChars = 29;

        /* get initial character */

        int[][] pixPositions = new int[][]{
                {0, 0},
                {0, 1},
                {0, 2}
        };
        String message = "" + decodeChar(picture, pixPositions);

        /* get remaining characters */

        for (int messagePos = 1; messagePos < numChars; messagePos++) {
            // get the locations of the pixels to decode
            pixPositions = getNextPositions(picture, pixPositions);

            // decode pixels
            char curChar = decodeChar(picture, pixPositions);
            message += curChar;
        }
        Log.d("tag", message);

        Log.d("tag", "decode has ended");
    }

    void save(){

        Log.d("tag", "save was called");
    }

}
