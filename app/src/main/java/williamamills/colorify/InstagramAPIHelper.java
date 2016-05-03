package williamamills.colorify;

/**
 * Created by Nishant on 4/9/16.
 */

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InstagramAPIHelper extends AsyncTask<Void, Void, String> {

    /* Popular Endpoint*/
    String API_URL = "https://api.instagram.com/v1/media/popular?client_id=e05c462ebd86446ea48a5af73769b602";
    String start_location_url = "https://api.instagram.com/v1/locations/";
    String end_location_url = "/media/recent?client_id=e05c462ebd86446ea48a5af73769b602";
    String start_tags_url = "https://api.instagram.com/v1/tags/";
    String end_tags_url = "/media/recent?client_id=e05c462ebd86446ea48a5af73769b602";
    String latlng = "https://api.instagram.com/v1/locations/search?lat=" + 1 + "&lng="+ 2+ "&access_token=Your-Act";
    Context ctx;
    MainActivity activity;
    LatLng locationLat;

    String highQuality;
    String tags;
    String caption;
    String location;
    String likes;
    int numReturned;

    ArrayList<Photo> photoList;
    JSONArray data;


    protected void onPreExecute() {
        /* initialization before network call in background,
        * potentially do add different endpoints/search criteria */

    }
    private class LatLng{
        double latitude;
        double longitude;
        public LatLng(double lat, double lon){
            latitude = lat;
            longitude = lon;
        }
    }
    public InstagramAPIHelper(MainActivity act, Context context){
        activity = act;
        ctx = context;
    }

    protected String doInBackground(Void... urls) {

        try {
            URL url = new URL(API_URL);
            //locationLat.longitude*=-1;
            //URL url = new URL("https://api.instagram.com/v1/locations/search?lat=" + locationLat.latitude + "&lng="+ locationLat.longitude+ "&client_id=e05c462ebd86446ea48a5af73769b602");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    protected void onPostExecute(String response) {
        /* what do with the network call response,
         * potentially to store into sql db */
        if (response == null) {
            Toast.makeText(ctx, "THERE WAS AN ERROR RETRIEVING JSON DATA", Toast.LENGTH_LONG).show();
            System.out.println("THERE WAS AN ERROR RETRIEVING JSON DATA");
        } else {
            try {
                JSONObject json = new JSONObject(response); //contains meta and data
                data = json.getJSONArray("data"); //only get data, ignore meta
                numReturned = data.length();
                for (int i = 0; i < data.length(); i++) {
                    System.out.println(data.getJSONObject(i)); // print returned json objects
                }
                ArrayList<String> tester = new ArrayList<>();
                photoList = new ArrayList<>();
                for (int i = 0; i < numReturned; i++) {
                    JSONObject test = data.getJSONObject(i); //photo at index 1
                    JSONObject images = test.getJSONObject("images");
                    //JSONObject thumbnail = images.getJSONObject("thumbnail");
                    //String thumbnailUrl = thumbnail.getString("url");
                    highQuality = images.getJSONObject("standard_resolution").getString("url");

                    caption = "";
                    if (!test.isNull("caption")) {
                        caption = test.getJSONObject("caption").getString("text");
                    }
                    tags = "";
                    if (!test.isNull("tags")) {
                        tags = test.getString("tags");
                    }
                    location = "";
                    if (!test.isNull("location")) {
                        location = test.getString("location");
                    }
                    likes = "";
                    if (!test.isNull("likes")) {
                        likes = test.getJSONObject("likes").getString("count");
                    }
                    Photo photo = new Photo(caption, tags, likes, ctx.getResources().getString(R.string.image_path) + i,ctx.getResources().getString(R.string.thumbnail_image_path) + i, location);//location, tags, caption);
                    //tester.add(thumbnailUrl);
                    tester.add(highQuality);
                    photoList.add(photo);
                }
                String[] array = tester.toArray(new String[0]);
                GetBitmap g = new GetBitmap(ctx, photoList, false);
                try {
                    g.execute(array);
                    g.get(1000, TimeUnit.MILLISECONDS);
                } catch (Exception e) {

                }

                System.out.println(data.length()); //print number of objects returned (~24)
            } catch (JSONException e) {
                System.out.println("JSONERROR: " + e.getMessage());
            }
        }
    }

        public String getHighQuality(){
            return highQuality;
        }

        public String getTags(){
            return tags;
        }
        public String getCaption(){
            return caption;
        }
        public String getLocation(){
            return location;
        }
        public String getLikes(){
            return likes;
        }
        public int getNumReturned(){
            return numReturned;
        }

        public ArrayList<Photo> getPhotos(){
            return photoList;
        }

        public JSONArray getData(){
            return data;
        }

    }



