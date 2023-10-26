package edu.uiuc.cs427app;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public abstract class ThemeActivity extends AppCompatActivity {
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        applyTheme();
    }

    protected void applyTheme() {
        String theme = sharedPreferences.getString("theme", "");

        switch(theme) {
            case "Theme.Day": default:
                setTheme(R.style.Theme_Day);
                break;
            case "Theme.Night":
                setTheme(R.style.Theme_Night);
                break;
            case "Theme.Dawn":
                setTheme(R.style.Theme_Dawn);
                break;
            case "Theme.Dusk":
                setTheme(R.style.Theme_Dusk);
                break;
        }
    }

    protected void showThemeDialog() {
        String[] themes = {"Day", "Night", "Dawn", "Dusk"};
        String theme = sharedPreferences.getString("theme", "");

        int currentThemeIndex;
        switch(theme) {
            case "Theme.Day": default: currentThemeIndex = 0; break;
            case "Theme.Night": currentThemeIndex = 1; break;
            case "Theme.Dawn": currentThemeIndex = 2; break;
            case "Theme.Dusk": currentThemeIndex = 3; break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Choose a theme:")
                .setSingleChoiceItems(themes, currentThemeIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        switch (which) {
                            case 0:
                                editor.putString("theme", "Theme.Day");
                                break;
                            case 1:
                                editor.putString("theme", "Theme.Night");
                                break;
                            case 2:
                                editor.putString("theme", "Theme.Dawn");
                                break;
                            case 3:
                                editor.putString("theme", "Theme.Dusk");
                                break;
                        }
                        editor.apply();
                        recreate(); // To apply the theme change.
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
