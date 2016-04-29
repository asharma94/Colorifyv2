package williamamills.colorify;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Nishant on 4/28/16.
 */
@RunWith(AndroidJUnit4.class)
public class InstagramTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    final CountDownLatch signal = new CountDownLatch(1);

    InstagramAPIHelper apiHelper;

    @Before
    public void init(){
        apiHelper = new InstagramAPIHelper(mActivityRule.getActivity(), mActivityRule.getActivity().getApplicationContext());
        apiHelper.execute();
        try {
            signal.await(25, TimeUnit.SECONDS);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testCaption(){
        assertNotNull(apiHelper.getCaption());
    }
    @Test
    public void testHighQuality(){
        assertNotNull(apiHelper.getHighQuality());
    }
    @Test
    public void testLocation(){
        assertNotNull(apiHelper.getLocation());
    }
    @Test
    public void testLikes(){
        assertNotNull(apiHelper.getLikes());
    }
    @Test
    public void testTags(){
        assertNotNull(apiHelper.getTags());
    }

    @Test
    public void testCount(){
        assertEquals(24, apiHelper.getNumReturned());
    }

    @Test
    public void testDB() throws JSONException {
       DBHelper db =  DBHelper.getInstance(mActivityRule.getActivity().getApplicationContext());
       JSONArray data = apiHelper.getData();
       db.insertPhoto(data.getJSONObject(0),"test");
        assertNotNull(db.getPhoto(1));
    }


}
