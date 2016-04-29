package williamamills.colorify;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import android.support.test.espresso.intent.Intents;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import android.support.test.espresso.DataInteraction.*;
import android.view.View;
import android.support.test.espresso.ViewAction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onData;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    private String userStringToBetyped;
    private String passwordStringToBetyped;
    private String newUser = "user2@user1.com";
    private String newPassword= "hey";

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Before
    public void initValidString() {
        // Specify a valid string.
        userStringToBetyped = "wamills1@gmail.com";
        passwordStringToBetyped = "hello";
        Intents.init();
    }
    @After
    public void end(){
        Intents.release();
    }

    @Test
    public void loginWithUser() {
        onView(withId(R.id.login_user_name))
                .perform(typeText(userStringToBetyped), closeSoftKeyboard());
        onView(withId(R.id.login_password))
                .perform(typeText(passwordStringToBetyped), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }
    @Test
    public void loginWithoutUser(){
        onView(withId(R.id.login_user_name))
                .perform(typeText("Not a user"), closeSoftKeyboard());
        onView(withId(R.id.login_password))
                .perform(typeText("Not a password"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withText("FirebaseError:")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(not(isDisplayed())));
    }
    @Test
    public void makeUserWithUser(){
        onView(withId(R.id.create_user_button))
                .perform(click());
        IdlingPolicies.setMasterPolicyTimeout(3000 * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(3000 * 2, TimeUnit.MILLISECONDS);

        IdlingResource idlingResource = new ElapsedTimeIdlingResource(3000);
        Espresso.registerIdlingResources(idlingResource);
        onView(withHint("Email"))
                //onView(withId(R.id.my_edit_text_1))
                .perform(typeText(userStringToBetyped), closeSoftKeyboard());
        // onView(withId(R.id.my_edit_text_2))
        onView(withHint("New Password"))
                .perform(typeText(passwordStringToBetyped), closeSoftKeyboard());
        onView(withText("Ok")).perform(click());
        onView(withText("Error creating user"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void makeUserWithoutUser(){
        IdlingPolicies.setMasterPolicyTimeout(3000 * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(3000 * 2, TimeUnit.MILLISECONDS);


        IdlingResource idlingResource = new ElapsedTimeIdlingResource(3000);
        Espresso.registerIdlingResources(idlingResource);

        onView(withId(R.id.create_user_button))
                .perform(click());
        //onView(withId(R.id.my_edit_text_1))
        onView(withHint("Email"))
                .perform(typeText(newUser), closeSoftKeyboard());
        //onView(withId(R.id.my_edit_text_2))
        onView(withHint("New Password"))
                .perform(typeText(newPassword), closeSoftKeyboard());
        onView(withText("Ok")).perform(click());
        Espresso.unregisterIdlingResources(idlingResource);
        intended(hasComponent(MainActivity.class.getName()));
    }

    private class ElapsedTimeIdlingResource implements IdlingResource{
        long startTime;
        long waitingTime;
        private ResourceCallback resourceCallback;
        @Override
        public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
            this.resourceCallback = resourceCallback;
        }
        public ElapsedTimeIdlingResource(long waitingTime) {
            this.startTime = System.currentTimeMillis();
            this.waitingTime = waitingTime;
        }
        @Override
        public String getName(){
            return "ElapsedTime";
        }

        @Override
        public boolean isIdleNow() {
            long elapsed = System.currentTimeMillis() - startTime;
            boolean idle = (elapsed >= waitingTime);
            if (idle) {
                resourceCallback.onTransitionToIdle();
            }
            return idle;
        }
    }
    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
}