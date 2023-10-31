package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import retrofit2.Response;


public class WeatherActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://api.weatherapi.com/v1/";
    private static final String API_KEY = "1c9ca48edcd6459aaa514033233110";
    private static String cityname;


}
