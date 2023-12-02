package edu.uiuc.cs427app;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class

TestUserSignUp {


    @Rule
    public ActivityScenarioRule<CreateAccountActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(CreateAccountActivity.class);

    // Test that a user sign up fails upon submitting an invalid account (e.g. existing)
    @Test
    public void testUserSignUpFails() {
        // Enter test inputs
        String testUsername = "existingUser";
        String testPassword = "password";
        String testToastMessage = "Account already exists! Please sign in with your username and password.";
        String expectedActivitySignUp = "Authentication Page";
        long waitTimer = 2000; // in millis (ms)

        // Enter text in username field
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.inputUsername),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(testUsername), closeSoftKeyboard());

        // Enter text in password field
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.inputPassword),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login),
                                        1),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(testPassword), closeSoftKeyboard());

        // Sleep to slow time
        try {
            Thread.sleep(waitTimer);
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }

        // Click on the sign up button
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.buttonSignUp), withText("sign up"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login),
                                        1),
                                3),
                        isDisplayed()));
        materialButton.perform(click());

        // Assert that the sign up failed
        // Assert that we remain on the Authentication Page
        ViewInteraction textView = onView(
                allOf(withId(R.id.textViewHeader), withText("Authentication Page"),
                        withParent(allOf(withId(R.id.login),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView.check(matches(withText(expectedActivitySignUp)));

        // Assert that the toast message indicates a failed account creation
        ViewInteraction toastMessage = onView(withText(testToastMessage)).inRoot(new ToastMatcher());
        toastMessage.check(matches(isDisplayed()));

        // Sleep to slow time
        try {
            Thread.sleep(waitTimer);
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }

        // Delay at the end
        try {
            Thread.sleep(waitTimer / 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Test that a user sign up succeeds upon submitting a valid account (e.g. NOT existing)
    @Test
    public void testUserSignUpSuccess() {
        // Enter test inputs
        String testUsername = "newUser";
        String testPassword = "password";
        String testToastMessage = "Account created! Please sign in with your username and password.";
        String expectedActivitySignUp = "Authentication Page";
        String expectedActivitySignIn = "  CS427 Project App";
        long waitTimer = 2000; // in millis (ms)


        // Enter text in username field
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.inputUsername),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(testUsername), closeSoftKeyboard());

        // Enter text in password field
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.inputPassword),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login),
                                        1),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(testPassword), closeSoftKeyboard());

        // Sleep to slow time
        try {
            Thread.sleep(waitTimer);
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }

        // Click on the sign up button
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.buttonSignUp), withText("sign up"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login),
                                        1),
                                3),
                        isDisplayed()));
        materialButton.perform(click());

        // Assert that the sign up succeeded
        // Assert that we remain on the Authentication Page
        ViewInteraction textView = onView(
                allOf(withId(R.id.textViewHeader), withText("Authentication Page"),
                        withParent(allOf(withId(R.id.login),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView.check(matches(withText(expectedActivitySignUp)));

        // Assert that the toast message indicates a successful account creation
        ViewInteraction toastMessage = onView(withText(testToastMessage)).inRoot(new ToastMatcher());
        toastMessage.check(matches(isDisplayed()));

        // Sleep to slow time
        try {
            Thread.sleep(waitTimer);
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }

        // Click on the sign in button
        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.buttonSignIn), withText("sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login),
                                        1),
                                4),
                        isDisplayed()));
        materialButton2.perform(click());

        // Assert that the sign in succeeded by landing on main page
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textView3), withText("  CS427 Project App"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        textView2.check(matches(withText(expectedActivitySignIn)));

        // Sleep to slow time
        try {
            Thread.sleep(waitTimer);
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }

        // Delay at the end
        try {
            Thread.sleep(waitTimer / 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void resetEnvironment() {
        Log.v("RESET", "start cleanup");
        Context context = ApplicationProvider.getApplicationContext();
        AccountManager accountManager = AccountManager.get(context);
        Account accountToRemove = new Account("newUser", context.getString(R.string.account_type));
        accountManager.removeAccount(accountToRemove, null, null, null);
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }

    }
}
