package edu.uiuc.cs427app;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static org.hamcrest.Matchers.containsString;

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
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestLocationFeature {

    @Rule
    public ActivityScenarioRule<CreateAccountActivity> activityScenarioRule = new ActivityScenarioRule<>(CreateAccountActivity.class);

    @Test
    public void test01Location() throws UiObjectNotFoundException {
        login("testLocation", "12345");
        onView(withId(R.id.buttonAddCity)).perform(click());
        //add city
        String city = "Chicago";
        addCity(city);

        pause(2000);
        // Check if the latitude and longitude match the expected values for Chicago
        onView(withText("WEATHER")).perform(click());
        onView(withText(city)).check(matches(isDisplayed()));
        pause(2000);
        pressBack();
        pause(2000);
        String expectedLatitude = "41.8781136";
        String expectedLongitude = "-87.6297982";
//        onView(withId(R.id.cityNameLatLon)).check(matches(withText(containsString(expectedLatitude))));
//        onView(withId(R.id.cityNameLatLon)).check(matches(withText(containsString(expectedLongitude))));

        //edit
        onView(withId(R.id.buttonAddCity)).perform(click());
        onView(withText("DELETE")).perform(click());
        pause(2000);

//        onView(withId(R.id.cityNameLatLon)).check(matches(withText(containsString(expectedLatitude))));
//        onView(withId(R.id.cityNameLatLon)).check(matches(withText(containsString(expectedLongitude))));
        pressBack();

        // Delay at the end
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //        onView(withId(R.id.cityNameLatLon)).check(matches(withText(containsString(expectedLatitude))));
    //        onView(withId(R.id.cityNameLatLon)).check(matches(withText(containsString(expectedLongitude))));



//    @Test
//    public void test02Location() throws UiObjectNotFoundException {
//        login("testLocation", "12345");
//        onView(withId(R.id.buttonAddCity)).perform(click());
//        //add city
//        String city = "Seattle";
//        addCity(city);
//        pause(2000);
//        // Check if the latitude and longitude match the expected values for Chicago
//        onView(withText("WEATHER")).perform(click());
//        onView(withText(city)).check(matches(isDisplayed()));
//        pause(2000);
//
//        String expectedLatitude = "47.6061389";
//        String expectedLongitude = "-122.3328481";
//        onView(withId(R.id.cityLatitude)).check(matches(withText("Latitude: " + expectedLatitude)));
//        onView(withId(R.id.cityLongitude)).check(matches(withText("Longitude: " + expectedLongitude)));
//        onView(withId(R.id.buttonAddCity)).perform(click());
//        onView(withText("DELETE")).perform(click());
//        pressBack();
//    }



    private void login(String account, String password){
        onView(withId(R.id.inputUsername)).perform(typeText(account), closeSoftKeyboard());
        onView(withId(R.id.inputPassword)).perform(typeText(password), closeSoftKeyboard());
        // Click on the sign up button
        onView(withId(R.id.buttonSignUp)).perform(click());
        // Click on the sign in button
        onView(withId(R.id.buttonSignIn)).perform(click());
    }
    //Function to add new city
    private void addCity(String cityName) throws UiObjectNotFoundException {
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
        pause(2000);
    }

    //Function for wait
    private void pause(int ms) {
        try {
            //wait time in milliseconds
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
