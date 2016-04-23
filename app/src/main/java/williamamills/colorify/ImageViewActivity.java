package williamamills.colorify;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class ImageViewActivity extends Activity {
    ImageView imageView;
    Integer u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = (ImageView) findViewById(R.id.image);
        Bundle extras = getIntent().getExtras();
        u = extras.getInt("uri");
        try{
            imageView.setImageBitmap(BitmapFactory.decodeStream(openFileInput("myImage" + u)));
        }catch (FileNotFoundException e){
            Toast.makeText(getApplicationContext(), "Image Not Found", Toast.LENGTH_SHORT).show();
        }
        final Button previous = (Button) findViewById(R.id.previous_button);
        final Button next = (Button) findViewById(R.id.next_button);
        if(u.equals(0)){
            previous.setVisibility(View.INVISIBLE);
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
                        imageView.setImageBitmap(BitmapFactory.decodeStream(openFileInput("myImage" + u)));
                    }catch (FileNotFoundException e){

                    }
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!u.equals(23)){
                    u++;
                    if(u.equals(23)){
                        next.setVisibility(View.INVISIBLE);
                    }
                    previous.setVisibility(View.VISIBLE);
                    try{
                        imageView.setImageBitmap(BitmapFactory.decodeStream(openFileInput("myImage"+u)));
                    }catch (FileNotFoundException e){

                    }
                }
            }
        });

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
