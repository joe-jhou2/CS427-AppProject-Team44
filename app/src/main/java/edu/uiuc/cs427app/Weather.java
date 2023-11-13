package edu.uiuc.cs427app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather {
    public Weather() {

    }
    private Context context;
    private static final String FORECAST_URL = "https://api.weatherapi.com/v1/forecast.json";

    private static final String API_KEY = "1c9ca48edcd6459aaa514033233110";


    public static void weather(Context context, String cityName,double latitude,double longitude) {
        String solo_name = cityName.split(",", 2)[0];
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Constructing the API URL with parameters
                String apiUrl = FORECAST_URL + "?key=" + API_KEY + "&q=" + latitude +","+longitude+ "&days=1" + "&aqi=yes";
                // Creating an instance of OkHttpClient for making HTTP requests
                OkHttpClient client = new OkHttpClient();
                // Building a request with the constructed URL
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .build();

                try {
                    // Executing the HTTP request and getting the response
                    Response response = client.newCall(request).execute();
                    // Checking if the response was successful
                    if (response.isSuccessful()) {
                        // Extracting the response body as a string
                        String responseBody = response.body().string();
                        // Parsing the response body as a JSON object
                        JSONObject json = new JSONObject(responseBody);

                        // Extract weather information from the JSON response
                        String city = json.getJSONObject("location").getString("name");
                        double temperature = json.getJSONObject("current").getDouble("temp_c");

                        // This temperature range can exclude the current 'temperature' due to polling/predictions. Consider manually setting a min/max using the range and current temps
                        double temperatureMax = json.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getDouble("maxtemp_c");
                        double temperatureMin = json.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getDouble("mintemp_c");

                        String weatherDescription = json.getJSONObject("current").getJSONObject("condition").getString("text");
                        System.out.println(weatherDescription);

                        double wind = json.getJSONObject("current").getDouble("wind_mph");
                        System.out.println(wind);

                        double humidity = json.getJSONObject("current").getDouble("humidity");
                        System.out.println(humidity);

                        double dewPoint = json.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour").getJSONObject(0).getDouble("dewpoint_c");
                        System.out.println(dewPoint);

                        double UV = json.getJSONObject("current").getDouble("uv");
                        System.out.println(UV);

                        double Air = json.getJSONObject("current").getJSONObject("air_quality").getDouble("pm2_5");
                        System.out.println(Air);

                        double Precipitation = json.getJSONObject("current").getDouble("precip_in");
                        System.out.println(Precipitation);

                        double PrecipitationChance = json.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour").getJSONObject(0).getDouble("chance_of_rain");
                        System.out.println(PrecipitationChance);

                        // store data in database
                        //setup up the ContentValues object with city data to send to the db
                        ContentValues values = new ContentValues();
                        values.put(DataStore.WeatherEntry.COL_CITY, cityName);
                        values.put(DataStore.WeatherEntry.COL_TEMPERATURE, temperature);
                        values.put(DataStore.WeatherEntry.COL_TEMPERATUREMAX, temperatureMax);
                        values.put(DataStore.WeatherEntry.COL_TEMPERATUREMIN, temperatureMin);
                        values.put(DataStore.WeatherEntry.COL_DESCRIPTION, weatherDescription);
                        values.put(DataStore.WeatherEntry.COL_WINDSPEED, wind);
                        values.put(DataStore.WeatherEntry.COL_HUMIDITY, humidity);
                        values.put(DataStore.WeatherEntry.COL_DEWPOINT, dewPoint);
                        values.put(DataStore.WeatherEntry.COL_UV, UV);
                        values.put(DataStore.WeatherEntry.COL_AIRINDEX, Air);
                        values.put(DataStore.WeatherEntry.COL_PRECIPITATION, Precipitation);
                        values.put(DataStore.WeatherEntry.COL_PRECIPITATIONCHANCE, PrecipitationChance);


                        //query the db to see if the city is already there for the given user
                        String selection = DataStore.WeatherEntry.COL_CITY+" = '"+cityName+"'";
                        Cursor cursor = context.getContentResolver().query(DataStore.WeatherEntry.CONTENT_URI, null, selection, null, null);
                        //only insert the city if not already in the users citylist, i.e. the query result is empty
                        if (cursor.getCount()==0){
                            // inserting into database through content URI
                            context.getContentResolver().insert(DataStore.WeatherEntry.CONTENT_URI, values);

                            // displaying a toast message
//                            Toast.makeText(context.getBaseContext(), cityName+" Saved", Toast.LENGTH_LONG).show();

                            //release the cursor and jump back to the Main Activity
                            cursor.close();

                        } else {
                            //pop a message about the duplicate city
//                            Toast.makeText(context.getBaseContext(), cityName+" is a duplicate. Choose a new city.", Toast.LENGTH_LONG).show();
                            context.getContentResolver().update(DataStore.WeatherEntry.CONTENT_URI, values, selection, null);
                            cursor.close();
                        }

                    } else {
                        // Handle API request error
                        // Optionally, use Handler to show an error message on the main thread
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("WeatherActivity", "Network error: " + e.getMessage());
                    // Handle network or other errors
                    // Optionally, use Handler to show an error message on the main thread
                }
            }
        }).start();
    }
}
