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
public class TestUserLogin {

    @Rule
    public ActivityScenarioRule<CreateAccountActivity> activityRule = new ActivityScenarioRule<CreateAccountActivity>(CreateAccountActivity.class);

    // Test methods will be added here
    @Test
    public void testSignInFunctionality() {
        // Enter Valid Inputs
        String testUsername = "riley";
        String testPassword = "team44";

        // Enter text in username and password fields
        onView(withId(R.id.inputUsername)).perform(typeText(testUsername), closeSoftKeyboard());
        onView(withId(R.id.inputPassword)).perform(typeText(testPassword), closeSoftKeyboard());

        // Sleep to slow time
        try {Thread.sleep(2000);} catch(InterruptedException e) {System.out.println("Interrupted!");}

        // Click on the sign in button
        onView(withId(R.id.buttonSignIn)).perform(click());

        // Assert that we have made it to the MainActivity, thus, the login was successful
        String mainPageString = "  CS427 Project App";
        onView(withId(R.id.textView3)).check(matches(withText(mainPageString)));

        // Sleep to slow time
        try {Thread.sleep(2000);} catch(InterruptedException e) {System.out.println("Interrupted!");}
    }

}
