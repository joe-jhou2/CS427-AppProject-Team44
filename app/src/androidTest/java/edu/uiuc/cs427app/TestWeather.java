package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
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
public class TestWeather {
    @Rule
    public ActivityScenarioRule<CreateAccountActivity> activityScenarioRule = new ActivityScenarioRule<>(CreateAccountActivity.class);

    @Test
    public void testWeather1() throws UiObjectNotFoundException {
        login("testWeather", "12345");
        onView(withId(R.id.buttonAddCity)).perform(click());

        //Add city
        String city = "Chicago";
        addCity(city);
        pause(2000);

        //check if weather information matches
        onView(withText("WEATHER")).perform(click());
        onView(withText(city)).check(matches(isDisplayed()));
        checkWeatherInfo();
        pause(2000);

        //go back and delete the city
        pressBack();
        onView(withId(R.id.buttonAddCity)).perform(click());
        onView(withText("DELETE")).perform(click());
        pause(2000);
        pressBack();

        // Delay at the end
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testWeather2() throws UiObjectNotFoundException {
        login("testWeather", "12345");
        onView(withId(R.id.buttonAddCity)).perform(click());

        //Add city
        String city = "Madison";
        addCity(city);
        pause(2000);

        //check if weather information matches
        onView(withText("WEATHER")).perform(click());
        onView(withText(city)).check(matches(isDisplayed()));
        checkWeatherInfo();
        pause(2000);

        //go back and delete the city
        pressBack();
        onView(withId(R.id.buttonAddCity)).perform(click());
        onView(withText("DELETE")).perform(click());
        pause(2000);
        pressBack();

        // Delay at the end
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // perform login
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
    }


    // Function for Assertions for weather information
    private void checkWeatherInfo() {
        onView(withId(R.id.CityTemperature)).check(matches(isDisplayed()));
        onView(withId(R.id.CityWeather)).check(matches(isDisplayed()));
        onView(withId(R.id.humidityData)).check(matches(isDisplayed()));
        onView(withId(R.id.UVData)).check(matches(isDisplayed()));
        onView(withId(R.id.AirData)).check(matches(isDisplayed()));
        onView(withId(R.id.PrecipitationData)).check(matches(isDisplayed()));
        onView(withId(R.id.PrecipitationChanceData)).check(matches(isDisplayed()));
        onView(withId(R.id.windData)).check(matches(isDisplayed()));
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
