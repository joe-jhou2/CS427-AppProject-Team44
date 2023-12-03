package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestUserLogoff {

    @Rule
    public ActivityScenarioRule<CreateAccountActivity> activityScenarioRule = new ActivityScenarioRule<>(CreateAccountActivity.class);

    // Test that after logging in, logging off takes the user back to the login screen
    @Test
    public void testLogoff() {
        // First, log in with valid credentials
        login("joe", "test"); // Assuming this method does the login process

        // Navigate to the Settings activity
        onView(withId(R.id.settingsPage)).perform(click());

        // Sleep to slow down for the UI to update
        try { Thread.sleep(2000); } catch (InterruptedException e) { System.out.println("Interrupted!"); }

        // Perform logoff action in Settings activity
        onView(withId(R.id.signOutButton)).perform(click());

        // Sleep to slow down for the UI to update
        try { Thread.sleep(2000); } catch (InterruptedException e) { System.out.println("Interrupted!"); }

        // Check that we are back on the login screen
        String loginPageString = "Authentication Page";
        onView(withId(R.id.textViewHeader)).check(matches(withText(loginPageString)));

        // Sleep to slow down for the UI to update
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void login(String account, String password){
        onView(withId(R.id.inputUsername)).perform(typeText(account), closeSoftKeyboard());
        onView(withId(R.id.inputPassword)).perform(typeText(password), closeSoftKeyboard());
        // Click on the sign up button
        onView(withId(R.id.buttonSignUp)).perform(click());
        // Click on the sign in button
        onView(withId(R.id.buttonSignIn)).perform(click());
    }
}

