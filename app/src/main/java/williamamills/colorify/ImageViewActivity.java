package williamamills.colorify;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class ImageViewActivity extends Activity {
    ImageView imageView;
    Integer u;
    Integer size;
    ArrayList<Photo> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_view);

        imageView = (ImageView) findViewById(R.id.image);
        //getActionBar().setTitle("");
        Bundle extras = getIntent().getExtras();
        u = extras.getInt("uri");
        photoList = extras.getParcelableArrayList("photoList");
        size = photoList.size()-1;
        try{
            imageView.setImageBitmap(BitmapFactory.decodeStream(openFileInput(photoList.get(u).getBitmapAddress())));
        }catch (FileNotFoundException e){
            Toast.makeText(getApplicationContext(), "Image Not Found", Toast.LENGTH_SHORT).show();
        }
        final Button previous = (Button) findViewById(R.id.previous_button);
        final Button next = (Button) findViewById(R.id.next_button);
        if(u.equals(0)){
            previous.setVisibility(View.INVISIBLE);
        }
        if(u.equals(size)){
            next.setVisibility(View.INVISIBLE);
        }
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!u.equals(0)){
                    u--;
                    if(u.equals(0)){
                        previous.setVisibility(View.INVISIBLE);
                    }
                    next.setVisibility(View.VISIBLE);
                    try {
                        imageView.setImageBitmap(BitmapFactory.decodeStream(openFileInput(photoList.get(u).getBitmapAddress())));
                    }catch (FileNotFoundException e){

                    }
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!u.equals(size)){
                    u++;
                    if(u.equals(size)){
                        next.setVisibility(View.INVISIBLE);
                    }
                    previous.setVisibility(View.VISIBLE);
                    try{
                        imageView.setImageBitmap(BitmapFactory.decodeStream(openFileInput(photoList.get(u).getBitmapAddress())));
                    }catch (FileNotFoundException e){

                    }
                }
            }
        });
        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    saveFile(BitmapFactory.decodeStream(openFileInput(photoList.get(u).getBitmapAddress())));
                    MediaStore.Images.Media.insertImage(getContentResolver(), BitmapFactory.decodeStream(openFileInput(photoList.get(u).getBitmapAddress())), "Photo" , "What a great photo");
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.image_not_saved), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void saveFile(Bitmap imageToSave) {
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fileName = "colorifyimage-" + n + ".jpg";
        File file = new File(new File("/sdcard/ColorifySavedPictures/"), fileName);
        if(file.exists()){
            n = 10000;
            n = generator.nextInt();
            fileName = "colorifyimage-" + n + ".jpg";
            file = new File(new File("/sdcard/ColorifySavedPictures/"), fileName);
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
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
