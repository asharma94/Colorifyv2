package williamamills.colorify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Nishant on 4/9/16.
 */
public class ColorHelper extends AsyncTask<String, Void, Boolean> {

    protected void onPreExecute(){

    }
    ArrayList<Photo> photoList;
    Context mCtx;
    ColorHelper(ArrayList<Photo> _photoList, Context ctx){
        photoList = _photoList;
        mCtx = ctx;
    }

    protected Boolean doInBackground(String... url){
        for(int i = 0; i < photoList.size(); i++){
            String color = findAverageColor("myImage"+i);//// TODO: 4/26/2016 use correct name from openCVhelper
            photoList.get(i).setColor(color);
        }

        //String color = findAverageColor(url[0]);

        return true;
    }

    protected void onPostExecute(Boolean result){
        System.out.println(result);
    }

    public String findAverageColor(String image_url){
        Bitmap bitmap;// = getBitmapFromURL(image_url);
        try{
            bitmap = BitmapFactory.decodeStream(mCtx.openFileInput(image_url));
        }catch (FileNotFoundException e){
            Toast.makeText(mCtx, "Image Not Found", Toast.LENGTH_SHORT).show();
            return "N/A";
        }
        Palette palette = Palette.generate(bitmap);
        int vibrant = palette.getVibrantColor(0);
        return bucket(vibrant);
       /* long redBucket = 0;
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
*/
    }
    private String bucket(int hex){
        String[] colors = mCtx.getResources().getStringArray(R.array.color_choices);//red, blue,green,purple,orange,yellow
        int[] colorsHex = {0xFF0000, 0x0000FF, 0x00FF00, 0x800080, 0xFFA500,0xFFFF00};
        Integer closest = Integer.MAX_VALUE;
        Integer index = -1;
        for(int i = 0; i < colors.length; i++){
            int current = Math.abs(hex-colorsHex[i]);
            if(current < closest){
                closest = current;
                index = i;
            }
        }
        return colors[index];
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
