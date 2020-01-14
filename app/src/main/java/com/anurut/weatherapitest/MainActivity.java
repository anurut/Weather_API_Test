package com.anurut.weatherapitest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView temperature,humidity,wind, cityText, cityWeather;
    CardView cardView;
    ImageView weatherImage,flagImage;
    EditText cityName,country;
    final String apiKey = "20b924e178d8213c17ae446cdbfa7bfb";
    String flagurl = "http://openweathermap.org/images/flags/";
    String city;
    String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = findViewById(R.id.temperature_text);
        humidity = findViewById(R.id.humidity_text);
        wind = findViewById(R.id.wind_speed);
        cityText = findViewById(R.id.city_name);
        weatherImage = findViewById(R.id.weather_image);
        cityWeather = findViewById(R.id.city_weather);
        cardView = findViewById(R.id.cardView);
        flagImage = findViewById(R.id.flag);

        cityName = findViewById(R.id.enter_city);
        country = findViewById(R.id.enter_country);


    }

    public void fetchWeather(View view) {

        if(!cityName.getText().toString().isEmpty() && !country.getText().toString().isEmpty()){
            city = cityName.getText().toString();
            countryCode = country.getText().toString();

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + countryCode + "&APPID=" + apiKey;
            //String url = "https://jsonplaceholder.typicode.com/todos/1";
            Log.d("myVolley", url);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        setTemperatureAndHumidity(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("myVolley", error.toString());
                    Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                }
            });

            queue.add(jsonObjectRequest);
        } else {
            Toast.makeText(MainActivity.this, "Empty fields not allowed",Toast.LENGTH_SHORT).show();
        }



    }

    void setTemperatureAndHumidity(JSONObject obj) throws JSONException {

        JSONObject main = obj.getJSONObject("main");
        JSONObject winds = obj.getJSONObject("wind");
        JSONObject weather = obj.getJSONArray("weather").getJSONObject(0);
        JSONObject sys = obj.getJSONObject("sys");
        String imageID = weather.getString("icon");
        String flag = flagurl + sys.getString("country").toLowerCase()+".png";

        String temp = getString(R.string.temp,String.format(Locale.UK,"%.2g%n",getCelsiusFromKelvin(main.getInt("temp"))),"");
        String windSpeed = String.format(Locale.ENGLISH,"%.2g%n",meterPerSecondToKilometerPerHour(winds.getInt("speed")));

        temperature.setText(temp);
        humidity.setText("Humidity: "+main.getInt("humidity")+"%");
        wind.setText("Wind speed: " + windSpeed+ " km/h");
        cityText.setText(obj.getString("name"));
        cityWeather.setText(weather.getString("description"));


        Picasso.with(this).load(getImageUrl(imageID)).resize(60,60).centerInside().into(weatherImage);
        Picasso.with(this).load(flag).resize(32,22).into(flagImage);

        cardView.setVisibility(View.VISIBLE);


    }

    double getCelsiusFromKelvin(int kelvin){
        return kelvin - 273.15;
    }

    String getImageUrl(String icon) {

        return "http://openweathermap.org/img/wn/"+ icon +"@2x.png";
    }

    double meterPerSecondToKilometerPerHour(int meterPerSecond) {
        return meterPerSecond*3.6;
    }
}
