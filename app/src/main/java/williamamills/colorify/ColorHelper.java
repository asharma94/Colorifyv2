package williamamills.colorify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.IOException;


/**
 * Created by Nishant on 4/9/16.
 */
public class ColorHelper extends AsyncTask<String, Void, String> {

    protected void onPreExecute(){

    }

    protected String doInBackground(String... url){

        String color = findAverageColor(url[0]);

        return color;
    }

    protected void onPostExecute(String result){
        System.out.println(result);
    }

    public String findAverageColor(String image_url){
        Bitmap bitmap = getBitmapFromURL(image_url);

        long redBucket = 0;
        long greenBucket = 0;
        long blueBucket = 0;
        long pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++)
        {
            for (int x = 0; x < bitmap.getWidth(); x++)
            {
                int c = bitmap.getPixel(x, y);

                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
                // does alpha matter?
            }
        }

        int averageColor = Color.rgb((int)(redBucket / pixelCount),(int)(greenBucket / pixelCount), (int)(blueBucket / pixelCount));

        String hexColor = String.format("#%06X", (0xFFFFFF & averageColor));

        return hexColor;

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            //connection.disconnect();
            return myBitmap;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
