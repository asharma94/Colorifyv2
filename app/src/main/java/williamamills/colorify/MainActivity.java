package williamamills.colorify;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity {
    Button mainButton;
    JSONArray j;

    static {
        System.loadLibrary("opencv_java3");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainButton = (Button) findViewById(R.id.main_enter);
        final JSONArray jArrayFacebookData = new JSONArray();
        final JSONObject jObjectType = new JSONObject();
        try {
            // put elements into the object as a key-value pair
            jObjectType.put("type", "instagram_json");

            jArrayFacebookData.put(jObjectType);

            // 2nd array for user information
            JSONObject jObjectData = new JSONObject();

            // Create Json Object using Facebook Data
            jObjectData.put("facebook_user_id", "id");
            jObjectData.put("first_name", "Alexander");
            jObjectData.put("last_name", "Mills");
            jObjectData.put("email", "wamills1@gmail.com");
            jObjectData.put("username", "wamills");
            jObjectData.put("birthday", "january");
            jObjectData.put("gender", "Male");
            jObjectData.put("location", "Austin");
            jObjectData.put("display_photo", "a");
            jArrayFacebookData.put(jObjectData);
        }catch(Exception e){

        }
        mainButton.setText("ImageListView Test");
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               OpenCVHelper opencv = new OpenCVHelper(getApplicationContext());
                opencv.execute();
            }
        });
        Button button3 = (Button) findViewById(R.id.button3);
        final ImageView imageView = (ImageView) findViewById(R.id.image_view);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                imageView.setImageBitmap(BitmapFactory.decodeStream(openFileInput("test2.png")));
                }catch(FileNotFoundException e){
                    Toast.makeText(getApplicationContext(), "Fuck", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button b2 = (Button) findViewById(R.id.b2);
        b2.setText("Instagram Test");
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstagramAPIHelper iHelper = new InstagramAPIHelper(MainActivity.this, getApplicationContext());
                iHelper.execute();
            }
        });

    }
    public void setJSON(String[] obj, ArrayList<Photo> photoList){
        GetBitmap g = new GetBitmap(getApplicationContext(), photoList);
        try {
            g.execute(obj);
            g.get(1000, TimeUnit.MILLISECONDS);
        }catch(Exception e){

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
