package edu.uiuc.cs427app;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.app.Activity;
import android.content.SharedPreferences;

public class ThemeUtils {
    public static void applyTheme(Account currentAccount, SharedPreferences sharedPreferences, Context context) {
        if (currentAccount != null) {
            String themeKey = AccountManager.get(context).getUserData(currentAccount, "theme");
            if (themeKey == null) themeKey = "Theme.Day"; // default
            ThemePreference themePreference = ThemePreference.fromKey(themeKey);
            context.setTheme(themePreference.styleRes);
        }
    }

    // Method to show a dialog allowing the user to choose a theme.
    public static void showThemeDialog(final Activity activity) {
        String[] themes = {"Day", "Night", "Dawn", "Dusk"};
        SharedPreferences sharedPreferences = activity.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String currentThemeKey = sharedPreferences.getString("theme", "");
        final ThemePreference currentTheme = ThemePreference.fromKey(currentThemeKey);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose a theme:")
                .setSingleChoiceItems(themes, currentTheme.index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ThemePreference selectedTheme = ThemePreference.fromIndex(which);
                        saveThemePreference(activity, selectedTheme.key);
                        activity.recreate();
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void saveThemePreference(Activity activity, String themeKey) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", themeKey);
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

