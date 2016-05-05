package williamamills.colorify;

import android.support.annotation.ColorInt;
import android.graphics.Color;
import com.thebluealliance.spectrum.SpectrumDialog;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import com.thebluealliance.spectrum.SpectrumDialog;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    final int RED = Color.rgb(204,0,0);
    final int BLUE = Color.rgb(51,51,255);
    final int GREEN = Color.rgb(0,153,0);
    final int PURPLE = Color.rgb(102,0,204);
    final int ORANGE = Color.rgb(255,128,0);
    final int YELLOW = Color.rgb(255,255,0);
    int[] colors = {RED,BLUE,GREEN,PURPLE,ORANGE, YELLOW};
    public void setColor(int color){
        sent_color = color;
        if(color == RED){
            color_to_send = "Red";
        }else if(color == BLUE){
            color_to_send = "Blue";
        }else if(color == GREEN){
            color_to_send = "Green";
        }else if(color == PURPLE){
            color_to_send = "Purple";
        }else if (color == ORANGE){
            color_to_send = "Orange";
        }else if (color == YELLOW){
            color_to_send = "Yellow";
        }
    }
    Button mainButton;
    JSONArray j;
    MainActivity activity = this;
    Spinner choiceSpinner;
    EditText editText;
    //Spinner colorSpinner;
    SpectrumDialog diag;
    String color_to_send;
    int sent_color = 0;
    static{
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Colorify Image Search");
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        // display the first navigation drawer view on app launch
        displayView(0);
        mainButton = (Button) findViewById(R.id.main_enter);
        choiceSpinner = (Spinner) findViewById(R.id.main_activity_choice_spinner);
        //colorSpinner = (Spinner) findViewById(R.id.main_activity_color_spinner);
        editText = (EditText) findViewById(R.id.main_activity_edit_text);

        choiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://popular
                        editText.setVisibility(View.INVISIBLE);
                       // colorSpinner.setVisibility(View.INVISIBLE);
                        break;
                    case 1://location
                        editText.setVisibility(View.VISIBLE);
                      //  colorSpinner.setVisibility(View.INVISIBLE);
                        break;
                    case 2://color
                        diag = new SpectrumDialog.Builder(getApplicationContext())
                                .setTitle("Choose your color")
                                .setFixedColumnCount(2)
                                .setColors(colors)
                                .setDismissOnColorSelected(false)
                                .setSelectedColor((sent_color!= 0)?sent_color:RED)
                                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener(){
                                    public void onColorSelected(boolean positiveResult, @ColorInt int color){
                                        setColor(color);
                                    }
                                })
                                .build();
                        FragmentManager fm = getSupportFragmentManager();
                        diag.show(fm, "new");
                        editText.setVisibility(View.INVISIBLE);
                        //colorSpinner.setVisibility(View.VISIBLE);
                        break;
                    case 3://tag
                        diag = new SpectrumDialog.Builder(getApplicationContext())
                                .setTitle("Choose your color")
                                .setFixedColumnCount(2)
                                .setColors(colors)
                                .setDismissOnColorSelected(false)
                                .setSelectedColor((sent_color!= 0)?sent_color:RED)
                                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener(){
                                    public void onColorSelected(boolean positiveResult, @ColorInt int color){
                                        setColor(color);
                                    }
                                })
                                .build();
                        fm = getSupportFragmentManager();
                        diag.show(fm, "new");
                        editText.setVisibility(View.VISIBLE);
                        //colorSpinner.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView adapterView) {

            }
        });
        mainButton.setText("Search");
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = choiceSpinner.getSelectedItemPosition();
                switch (pos) {
                    case 0://popular
                        InstagramAPIHelper popularHelper = new InstagramAPIHelper(activity, getApplicationContext());
                        popularHelper.execute();
                        break;
                    case 1:
                        if(editText.getText().toString().matches("")){
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_location_string), Toast.LENGTH_SHORT).show();
                            break;}
                        InstagramLocationAPIHelper locationHelper = new InstagramLocationAPIHelper(activity, getApplicationContext(), editText.getText().toString().trim());
                        locationHelper.execute();
                        break;
                    case 2:
                        InstagramColorAPIHelper colorHelper = new InstagramColorAPIHelper(activity, getApplicationContext(), color_to_send/*(String)colorSpinner.getSelectedItem()*/);
                        colorHelper.execute();
                        Toast.makeText(getApplicationContext(), "Please Wait", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        if(editText.getText().toString().matches("")){break;}
                        InstagramTagAPIHelper tagHelper = new InstagramTagAPIHelper(activity, getApplicationContext(), editText.getText().toString().trim(),color_to_send /*(String)colorSpinner.getSelectedItem()*/);
                        tagHelper.execute();
                        Toast.makeText(getApplicationContext(), "Please Wait", Toast.LENGTH_LONG).show();
                        break;
                }
                editText.setText("");
            }
        });


    }
    public void setJSON(String[] obj, ArrayList<Photo> photoList){

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

    @Override
    public void onBackPressed(){

    }
    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new AboutUsFragment();
                title = getString(R.string.title_friends);
                break;
            case 2:
                Intent i = new Intent(getApplicationContext(), MainViewActivity.class);
                startActivity(i);
                fragment = new HomeFragment();
                title = getString(R.string.title_messages);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }


}
