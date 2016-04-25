package williamamills.colorify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import android.support.test.espresso.intent.Intents;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {




        private String mStringToBetyped;

        @Rule
        public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
                MainActivity.class);

        @Before
        public void initValidString() {
            // Specify a valid string.
            mStringToBetyped = "Espresso";
        }

        @Test
        public void searchByPopular() {
            Intents.init();
            // Type text and then press the button.
            //onView(withId(R.id.main_activity_edit_text))
              //      .perform(typeText(mStringToBetyped), closeSoftKeyboard());
            onView(withId(R.id.main_enter)).perform(click());
            // Check that the text was changed.
            intended(hasComponent(ItemsList.class.getName()));
            //onView(withId(R.id.main_activity_edit_text))
             //       .check(matches(withText(mStringToBetyped)));
        }

}