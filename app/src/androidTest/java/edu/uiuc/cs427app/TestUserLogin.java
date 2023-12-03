package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestUserLogin {

    @Rule
    public ActivityScenarioRule<CreateAccountActivity> activityRule = new ActivityScenarioRule<CreateAccountActivity>(CreateAccountActivity.class);

    // Test that a valid sign in takes a user to the MainActivity
    @Test
    public void testSignInSuccess() {
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

        // Delay at the end
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Test that a invalid sign in attempt. This should keep the user on the login page
    @Test
    public void testSignInFail() {
        // Enter Valid Inputs
        String testUsername = "riley";
        String testPassword = "team43";

        // Enter text in username and password fields
        onView(withId(R.id.inputUsername)).perform(typeText(testUsername), closeSoftKeyboard());
        onView(withId(R.id.inputPassword)).perform(typeText(testPassword), closeSoftKeyboard());

        // Sleep to slow time
        try {Thread.sleep(2000);} catch(InterruptedException e) {System.out.println("Interrupted!");}

        // Click on the sign in button
        onView(withId(R.id.buttonSignIn)).perform(click());

        // Assert that we have did NOT make it to MainActivity, thus, the login was failed
        String loginPageString = "Authentication Page";
        onView(withId(R.id.textViewHeader)).check(matches(withText(loginPageString)));

        // Sleep to slow time
        try {Thread.sleep(2000);} catch(InterruptedException e) {System.out.println("Interrupted!");}

        // Delay at the end
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Set up test environment with required accounts
    @Before
    public void initializeEnvironment() {
        Log.v("INITIALIZE", "start initialization");
        String testUsername = "riley";
        String testPassword = "team44";
        Context context = ApplicationProvider.getApplicationContext();
        AccountManager accountManager = AccountManager.get(context);
        Account accountToAdd = new Account(testUsername, context.getString(R.string.account_type));
        // Checks if the account was successfully added to the account manager, if so,
        if (accountManager.addAccountExplicitly(accountToAdd, testPassword, null)) {
            Log.v("TestAccountCreate", "account created, username="+ testUsername); // Account creation succeeded
        } else {
            Log.v("TestAccountCreate", "account NOT created, username="+ testUsername);// Account creation failed
        }
    }

    // Clean up environment of newly created accounts
    @After
    public void resetEnvironment() {
        Log.v("RESET", "start cleanup");
        String removeUser1 = "riley";
        Context context = ApplicationProvider.getApplicationContext();
        AccountManager accountManager = AccountManager.get(context);
        Account accountToRemove1 = new Account(removeUser1, context.getString(R.string.account_type));
        accountManager.removeAccount(accountToRemove1, null, null, null);
    }
}
