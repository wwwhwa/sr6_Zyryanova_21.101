package com.example.sr6;


import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "d52b8bede3444a9bbef102215231311";
    private static final String API_URL = "https://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=Novosibirsk";

    private TextView weatherTextView, weatherDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherTextView = findViewById(R.id.weatherTextView);

        new FetchWeatherTask().execute();
    }

    private class FetchWeatherTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String weatherJsonString = null;

            try {
                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                weatherJsonString = buffer.toString();

            } catch (IOException e) {
                Log.e("MainActivity", "Error fetching weather data: " + e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("MainActivity", "Error closing reader: " + e);
                    }
                }
            }
            return weatherJsonString;
        }

        @Override
        protected void onPostExecute(String weatherJsonString) {
            if (weatherJsonString != null) {
                try {

                    JSONObject weatherJson = new JSONObject(weatherJsonString);
                    JSONObject currentJson = weatherJson.getJSONObject("current");
                    JSONObject locationJson = weatherJson.getJSONObject("location");


                    String city = locationJson.getString("name");
                    String region = locationJson.getString("region");
                    String country = locationJson.getString("country");
                    String localtime = locationJson.getString("localtime");

                    String temperature = currentJson.getString("temp_c");
                    String humidity = currentJson.getString("humidity");
                    String wind = currentJson.getString("wind_mph");


                    String weatherText = "\nCity: " + city;
                    weatherText += "\nRegion: " + region;
                    weatherText += "\nCountry: " + country;
                    weatherText += "\nLocal time: " + localtime;


                    weatherText += "\nTemperature: " + temperature + "°C";
                    weatherText += "\nHumidity: " + humidity + "%";
                    weatherText += "\nWind: " + wind + "mph";

                    weatherTextView.setText(weatherText);

                } catch (JSONException e) {
                    Log.e("MainActivity", "Error parsing JSON: " + e);
                }
            }
        }
    }
}