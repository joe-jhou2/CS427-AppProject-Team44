package edu.uiuc.cs427app;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.Espresso.pressBack;

import android.widget.TextView;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeleteCityTest {

    @Rule
    public ActivityScenarioRule<CreateAccountActivity> activityScenarioRule = new ActivityScenarioRule<>(CreateAccountActivity.class);

    //test that removing an existing city will remove the city from the Add City and the Main Page
    @Test
    public void deleteCityFunctionality() throws UiObjectNotFoundException{
        // Enter Valid Account Info
        String testUsername = "testDelete";
        String testPassword = "abc";

        // Enter text in username and password fields
        onView(withId(R.id.inputUsername)).perform(typeText(testUsername), closeSoftKeyboard());
        onView(withId(R.id.inputPassword)).perform(typeText(testPassword), closeSoftKeyboard());

        // Sleep to slow time
        try {Thread.sleep(2000);} catch(InterruptedException e) {System.out.println("Interrupted!");}

        // Click on the sign up then sign in button
        onView(withId(R.id.buttonSignUp)).perform(click());
        onView(withId(R.id.buttonSignIn)).perform(click());

        // Click on add city button
        onView(withId(R.id.buttonAddCity)).perform(click());

        //Add City
        String cityName = "Los Angeles";
        onView(withId(R.id.autocomplete_fragment)).perform(click());

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject searchTextField = device.findObject(new UiSelector().textContains("Search for City"));
        if (searchTextField.exists() && searchTextField.isEnabled()) {
            searchTextField.click();
            searchTextField.setText(cityName);
        }

        UiScrollable recyclerView = new UiScrollable(new UiSelector().resourceId("edu.uiuc.cs427app:id/places_autocomplete_list"));
        UiObject firstSuggestion = recyclerView.getChildByText(new UiSelector().className(TextView.class.getName()), cityName, true);

        if (firstSuggestion.exists()) {
            firstSuggestion.click();
        } else {
            throw new UiObjectNotFoundException("Could not find city in the suggestions list.");
        }

        device.wait(Until.findObject(By.text("Save City")), 2000); // Waits for 2 seconds
        onView(withId(R.id.buttonSaveCity)).perform(click());

        try {Thread.sleep(2000);} catch(InterruptedException e) {System.out.println("Interrupted!");}

        //check city is added to main page
        onView(withId(R.id.cityListLayout)).check(matches(hasDescendant(withText("Los Angeles, CA, USA"))));

        //check that city is added in the add/delete city page
        onView(withId(R.id.buttonAddCity)).perform(click());
        onView(withId(R.id.deleteCityListLayout)).check(matches(hasDescendant(withText("Los Angeles, CA, USA"))));

        // Delete City
        onView(withText("DELETE")).perform(click());

        //Check that city is removed from the add/delete city page
        onView(withId(R.id.deleteCityListLayout)).check(matches(not(hasDescendant(withText("Los Angeles, CA, USA")))));

        //check city is removed from main page
        pressBack();
        onView(withId(R.id.cityListLayout)).check(matches(not(hasDescendant(withText("Los Angeles, CA, USA")))));

    }
}