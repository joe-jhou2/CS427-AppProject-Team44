package edu.uiuc.cs427app;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.accounts.Account;
import android.content.Context;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

public class ThemeUtils {
    private static final String THEME_PREFERENCES = "theme_preferences";
    private static final String DEFAULT_THEME = "Theme.Day";

    // apply theme includes background color, text font size, et.al.
    public static void applyTheme(Account currentAccount, Context context) {
        String themeKey;
        if (currentAccount != null) {
            themeKey = getThemePreferenceForAccount(context, currentAccount.name);
            ThemePreference themePreference = ThemePreference.fromKey(themeKey);
            context.setTheme(themePreference.styleRes);
        }
    }

    // Method to show a dialog allowing the user to choose a theme.
    public static void showThemeDialog(final Activity activity, final Account currentAccount) {
        // 4 theme pre-designed
        String[] themes = {"Day", "Night", "Dawn", "Dusk"};
        String currentThemeKey = getThemePreferenceForAccount(activity, currentAccount.name);
        final ThemePreference currentTheme = ThemePreference.fromKey(currentThemeKey);

        // show up dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose a theme:")
                .setSingleChoiceItems(themes, currentTheme.index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ThemePreference selectedTheme = ThemePreference.fromIndex(which);
                        saveThemePreferenceForAccount(activity, currentAccount.name, selectedTheme.key);
                        dialog.dismiss();
                        activity.recreate();
                        Log.d("Theme Dialogz", "theme dialog dismissed.");
                    }
                });
        builder.create().show();
    }

    // get user preference from user profile
    private static String getThemePreferenceForAccount(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(username, DEFAULT_THEME);
    }

    // apply any preference change to user profile
    public static void saveThemePreferenceForAccount(Context context, String username, String themeKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username, themeKey);
        editor.apply();
    }

    public enum ThemePreference {
        DAY("Theme.Day", R.style.Theme_Day, 0),
        NIGHT("Theme.Night", R.style.Theme_Night, 1),
        DAWN("Theme.Dawn", R.style.Theme_Dawn, 2),
        DUSK("Theme.Dusk", R.style.Theme_Dusk, 3);

        // Variables to store the theme's key, style resource ID, and index.
        final String key;
        final int styleRes;
        final int index;

        // Constructor to initialize the theme's attributes
        ThemePreference(String key, int styleRes, int index) {
            this.key = key;
            this.styleRes = styleRes;
            this.index = index;
        }

        // Method to fetch a theme preference based on its key.
        static ThemePreference fromKey(String key) {
            for (ThemePreference pref : values()) {
                if (pref.key.equals(key)) {
                    return pref;
                }
            }
            return DAY; // Return the DAY theme as a default.
        }

        static ThemePreference fromIndex(int index) {
            for (ThemePreference pref : values()) {
                if (pref.index == index) {
                    return pref;
                }
            }
            return DAY; // Return the DAY theme as a default.
        }
    }
}

