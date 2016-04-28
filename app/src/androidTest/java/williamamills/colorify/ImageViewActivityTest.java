package williamamills.colorify;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.NoMatchingRootException;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.String;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;


/**
 * Created by Alexander on 4/25/2016.
 */
@RunWith(AndroidJUnit4.class)
public class ImageViewActivityTest {


    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<ImageViewActivity> mActivityRule = new ActivityTestRule<>(
            ImageViewActivity.class, false, false);

    @Before
    public void initValidString() {
        // Specify a valid string.
        mStringToBetyped = "Espresso";

    }

    @Test
    public void checkPreviousButton() {
        // myObj = MyObject.mockObject();
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putInt("uri", 10);
        i.putExtras(extras);
        mActivityRule.launchActivity(i);

        onView((withId(R.id.previous_button))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        for(int j = 0; j <= 9; j++) {
            onView(ViewMatchers.withId(R.id.previous_button)).perform(click());
        }
        onView((withId(R.id.previous_button))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

    }
    @Test
    public void checkNextButton() {
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putInt("uri", 22);
        i.putExtras(extras);
        mActivityRule.launchActivity(i);

        onView((withId(R.id.next_button))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(ViewMatchers.withId(R.id.next_button)).perform(click());
        onView((withId(R.id.next_button))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

    }
    @Test
    public void checkSavesImage(){
        boolean exceptionCaptured = false;
        try {
            onView(withText(R.string.image_not_saved))
                    .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        }catch (Exception e){
            exceptionCaptured = true;
        }finally {
            assert(exceptionCaptured);
        }

    }
}
