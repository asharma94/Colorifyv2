package williamamills.colorify;

/**
 * Created by Ankit on 4/29/16.
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

public class InstagramTagAPIHelper extends AsyncTask<Void, Void, ArrayList<String>> {

    /* Popular Endpoint*/
    String API_URL = "https://api.instagram.com/v1/media/popular?client_id=e05c462ebd86446ea48a5af73769b602";
    String start_tags_url = "https://api.instagram.com/v1/tags/";
    String end_tags_url = "/media/recent?client_id=e05c462ebd86446ea48a5af73769b602";
    Context ctx;
    MainActivity activity;
    String tag;
    Integer iterations = 4;
    String colorToSearch;

    protected void onPreExecute() {
        /* initialization before network call in background,
        * potentially do add different endpoints/search criteria */

    }

    public InstagramTagAPIHelper(MainActivity act, Context context, String input_tag, String _color){
        activity = act;
        ctx = context;
        tag = input_tag;
        colorToSearch = _color;
    }

    protected ArrayList<String> doInBackground(Void... urls) {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> stringArrayList = new ArrayList<>();
        try {
            URL url = new URL(start_tags_url + tag + end_tags_url);
            for (int i = 0; i < iterations; i++) {

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    //StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    stringArrayList.add(stringBuilder.toString());
                    JSONObject json = new JSONObject(stringArrayList.get(i)); //contains meta and data
                    JSONObject data = json.getJSONObject("pagination");
                    String next = "";
                    if (!data.isNull("next_url")) {
                        next = data.getString("next_url");
                    }
                    if(next.equals("")){
                        iterations = i+1;
                        return stringArrayList;
                    }
                    url = new URL(next);
                    stringBuilder = new StringBuilder();

                } finally {
                    urlConnection.disconnect();
                }
            }
        }
        catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
        return stringArrayList;
    }

    protected void onPostExecute(ArrayList<String> response) {
        /* what do with the network call response,
         * potentially to store into sql db */
        if(response == null) {
            Toast.makeText(ctx, "THERE WAS AN ERROR RETRIEVING JSON DATA", Toast.LENGTH_LONG).show();
            System.out.println("THERE WAS AN ERROR RETRIEVING JSON DATA");
        }
        else {
            try{
                ArrayList<String> tester = new ArrayList<>();
                ArrayList<Photo> photoList = new ArrayList<>();
                for (int k = 0; k < iterations; k++) {
                    JSONObject json = new JSONObject(response.get(k)); //contains meta and data
                    JSONArray data = json.getJSONArray("data"); //only get data, ignore meta

                    for (int i = 0; i < data.length(); i++) {
                        System.out.println(data.getJSONObject(i)); // print returned json objects
                    }
                    // ArrayList<String> tester = new ArrayList<>();
                    //ArrayList<Photo> photoList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject test = data.getJSONObject(i); //photo at index 1
                        JSONObject images = test.getJSONObject("images");
                        //JSONObject thumbnail = images.getJSONObject("thumbnail");
                        //String thumbnailUrl = thumbnail.getString("url");
                        String thumbnail = images.getJSONObject("thumbnail").getString("url");
                        String highQuality = images.getJSONObject("standard_resolution").getString("url");

                        String caption = "";
                        if (!test.isNull("caption")) {
                            caption = test.getJSONObject("caption").getString("text");
                        }
                        String tags = "";
                        if (!test.isNull("tags")) {
                            tags = test.getString("tags");
                        }
                        String location = "";
                        if (!test.isNull("location")) {
                            location = test.getString("location");
                        }
                        String likes = "";
                        if (!test.isNull("likes")) {
                            likes = test.getJSONObject("likes").getString("count");
                        }
                        Photo photo = new Photo(caption, tags, likes, ctx.getResources().getString(R.string.image_path) + photoList.size(), thumbnail, location);//location, tags, caption);
                        //tester.add(thumbnailUrl);
                        tester.add(highQuality);
                        photoList.add(photo);
                    }
                }
                String[] array = tester.toArray(new String[0]);
                GetBitmap g = new GetBitmap(ctx, photoList, true, colorToSearch.toUpperCase());
                try {
                    g.execute(array);
                    g.get(1000, TimeUnit.MILLISECONDS);
                }catch(Exception e){

                }

                //System.out.println(data.length()); //print number of objects returned (~24)
            } catch(JSONException e){
                System.out.println("JSONERROR: " + e.getMessage());
            }

        }

    }

}

