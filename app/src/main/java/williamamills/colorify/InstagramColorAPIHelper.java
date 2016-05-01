package williamamills.colorify;

/**
 * Created by Alexander on 4/26/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class InstagramColorAPIHelper extends AsyncTask<Void, Void, ArrayList<String>> {

    /* Popular Endpoint*/
    String API_URL = "https://api.instagram.com/v1/media/popular?client_id=e05c462ebd86446ea48a5af73769b602";
    Context ctx;
    MainActivity activity;
    String searchedColor;
    Integer iterations = 4;// number of times to search for images

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
    public InstagramColorAPIHelper(MainActivity act, Context context, String _color){
        activity = act;
        ctx = context;
        searchedColor = _color;
    }

    protected ArrayList<String> doInBackground(Void... urls) {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> stringArrayList = new ArrayList<>();
        for(int i = 0; i < iterations; i++) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    stringArrayList.add(stringBuilder.toString());
                   // return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                return null;
            }
        }
        return stringArrayList;
    }

    protected void onPostExecute(ArrayList<String> response) {
        /* what do with the network call response,
         * potentially to store into sql db */
        if (response == null) {
            Toast.makeText(ctx, "THERE WAS AN ERROR RETRIEVING JSON DATA", Toast.LENGTH_LONG).show();
            System.out.println("THERE WAS AN ERROR RETRIEVING JSON DATA");
        } else {
            ArrayList<String> tester = new ArrayList<>();
            ArrayList<Photo> photoList = new ArrayList<>();
            for (int k = 0; k < iterations; k++) {
                try {
                    JSONObject json = new JSONObject(response.get(k)); //contains meta and data
                    JSONArray data = json.getJSONArray("data"); //only get data, ignore meta

                    for (int i = 0; i < data.length(); i++) {
                        System.out.println(data.getJSONObject(i)); // print returned json objects
                    }

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject test = data.getJSONObject(i); //photo at index 1
                        JSONObject images = test.getJSONObject("images");
                        //JSONObject thumbnail = images.getJSONObject("thumbnail");
                        //String thumbnailUrl = thumbnail.getString("url");
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
                        Photo photo = new Photo(caption, tags, likes, ctx.getResources().getString(R.string.image_path) + photoList.size(), location);//location, tags, caption);
                        //tester.add(thumbnailUrl);
                        tester.add(highQuality);
                        photoList.add(photo);
                    }

                } catch (JSONException e) {
                    System.out.println("JSONERROR: " + e.getMessage());
                }
            }
                String[] array = tester.toArray(new String[0]);

                GetBitmap g = new GetBitmap(ctx, photoList, true);
                try {
                    g.execute(array);
                    g.get(1000, TimeUnit.MILLISECONDS);
                } catch (Exception e) {

                }

                //System.out.println(data.length()); //print number of objects returned (~24)



        }
    }
}

