package edu.uiuc.cs427app;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateAccountActivityTest {

    @Rule
    public ActivityScenarioRule<CreateAccountActivity> activityRule = new ActivityScenarioRule<>(CreateAccountActivity.class);

    // Test methods will be added here
    @Test
    public void testSignupFunctionality() {
        // Replace "testUsername" and "testPassword" with valid test inputs
        String testUsername = "joe";
        String testPassword = "test";

        // Enter text in username and password fields
        onView(withId(R.id.inputUsername)).perform(typeText(testUsername), closeSoftKeyboard());
        onView(withId(R.id.inputPassword)).perform(typeText(testPassword), closeSoftKeyboard());

        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            System.out.println("got interrupted!");
        }

        // Click on the signup button
        onView(withId(R.id.buttonSignUp)).perform(click());
    }
}
