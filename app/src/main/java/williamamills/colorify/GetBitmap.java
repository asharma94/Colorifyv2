package williamamills.colorify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alexander on 4/9/2016.
 */
public class GetBitmap extends AsyncTask<String, Void, ArrayList<Bitmap>> {
    protected void onPreExecute() {
        /* initialization before network call in background,
        * potentially do add different endpoints/search criteria */

    }
    Context ctx;
    ArrayList<Photo> photoList;

    public GetBitmap(Context context, ArrayList<Photo> p){
        ctx = context;
        photoList = p;
    }

    protected ArrayList<Bitmap> doInBackground(String... urls) {
        ArrayList<Bitmap> arrayList = new ArrayList<>();
        try {
            //URL url = new URL(urls[0]);
            boolean success = false;
            for(String u : urls) {
                java.net.URL url = new java.net.URL(u);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input); //// FIXME: 4/13/2016
                arrayList.add(myBitmap);
                myBitmap.recycle();
                success = true;
            }
            return arrayList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }




    }

    protected void onPostExecute(ArrayList<Bitmap> response) {
        /* what do with the network call response,
         * potentially to store into sql db */
        if(response.isEmpty()) {
            Toast.makeText(ctx, "THERE WAS AN ERROR RETRIEVING JSON DATA", Toast.LENGTH_LONG).show();
            System.out.println("THERE WAS AN ERROR RETRIEVING JSON DATA");
        }
        else {
            Intent i = new Intent(ctx, ItemsList.class);
            for(int k = 0; k < response.size(); k++) {
               createImageFromBitmap(response.get(k), k);
            }
            Bundle extras = new Bundle();
            //extras.putInt("uris", response.size());
            extras.putParcelableArrayList("photos", photoList);
            i.putExtras(extras);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        }

    }
    public String createImageFromBitmap(Bitmap bitmap, int i) {
        String fileName = "myImage" + i;//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }
}
