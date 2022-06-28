package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    //initializing the widgets used fro UI
    EditText searchCity;
    TextView cityTv;
    TextView countryTv;
    TextView latitudeTv;
    TextView longitudeTv;
    TextView windSpeedTv;
    TextView pressureTv;
    TextView humidityTv;
    TextView sunRiseTv;
    TextView sunsSetTv;
    TextView temperatureTv;
    ImageView conditionIv;
    Button citySearch;
    LocationListener locationListener;
    LocationManager locationManager;
    int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //assigning the id's to the widgets
        searchCity = findViewById(R.id.searchCity);
        cityTv = findViewById(R.id.cityTv);
        countryTv = findViewById(R.id.countryTv);
        latitudeTv = findViewById(R.id.latitude);
        longitudeTv = findViewById(R.id.longitude);
        windSpeedTv = findViewById(R.id.windSpeed);
        pressureTv = findViewById(R.id.pressure);
        humidityTv = findViewById(R.id.humidity);
        sunRiseTv = findViewById(R.id.sunRise);
        sunsSetTv = findViewById(R.id.sunSet);
        temperatureTv = findViewById(R.id.temperature);
        conditionIv = findViewById(R.id.weatherIv);
        citySearch = findViewById(R.id.searchCityButton);

        citySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeatherInfo(); //function which has all the weather info code
            }
        });


    }

    private void getCurrentLocationInfo() { // function used to get the current location of the user

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
               String latitude = String.valueOf(location.getLatitude());
               String longitude = String.valueOf(location.getLongitude());
                String url = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon=" + longitude+ "&appid=55419c2eda7b4623561d4635ea1cb725";/*open weather api*/
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() { //volley library used to process the API
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // getting the JSON data 
                            String country = jsonObject.getJSONObject("sys").getString("country"); 
                            countryTv.setText(country);
                            
                            String city = jsonObject.getString("name");
                            cityTv.setText(city);
                            
                            String latitude = jsonObject.getJSONObject("coord").getString("lat");
                            String longitude = jsonObject.getJSONObject("coord").getString("lon");
                            latitudeTv.setText(latitude + "°N");
                            longitudeTv.setText(longitude+" °E");

                            double temperature = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
                            int roundedValue = (int) Math.rint(temperature);
                            String temp = Integer.toString(roundedValue);
                            temperatureTv.setText(temp + " °C");

                            JSONArray jsonArray = jsonObject.getJSONArray("weather");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            String icon = jsonObject1.getString("icon");
                            Picasso.get().load("https://openweathermap.org/img/wn/"+ icon+ "@2x.png").into(conditionIv); //picasso library used so as to get the icon as weather condition

                            String pressure = jsonObject.getJSONObject("main").getString("pressure");
                            pressureTv.setText(pressure +" hpa");

                            String humidity = jsonObject.getJSONObject("main").getString("humidity");
                            humidityTv.setText(humidity + "%");

                            String windSpeed = jsonObject.getJSONObject("wind").getString("speed");
                            windSpeedTv.setText(windSpeed + " km/hr");

                            String sunRise = jsonObject.getJSONObject("sys").getString("sunrise");
                            sunRiseTv.setText(sunRise);

                            String sunSet = jsonObject.getJSONObject("sys").getString("sunset");
                            sunsSetTv.setText(sunSet);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);
                //permissions for user to make the use of GPS to fetch the current lo
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
            }
            };
        };




    private void getWeatherInfo() {
        String city = searchCity.getText().toString();
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=55419c2eda7b4623561d4635ea1cb725";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String country = jsonObject.getJSONObject("sys").getString("country");
                    countryTv.setText(country);
                    String city = jsonObject.getString("name");
                    cityTv.setText(city);
                    String latitude = jsonObject.getJSONObject("coord").getString("lat");
                    String longitude = jsonObject.getJSONObject("coord").getString("lon");
                    latitudeTv.setText(latitude + "°N");
                    longitudeTv.setText(longitude+" °E");

                   double temperature = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
                   int roundedValue = (int) Math.rint(temperature);
                   String temp = Integer.toString(roundedValue);
                   temperatureTv.setText(temp + " °C");

                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    String icon = jsonObject1.getString("icon");
                    Picasso.get().load("https://openweathermap.org/img/wn/"+ icon+ "@2x.png").into(conditionIv);

                    String pressure = jsonObject.getJSONObject("main").getString("pressure");
                    pressureTv.setText(pressure +" hpa");

                    String humidity = jsonObject.getJSONObject("main").getString("humidity");
                    humidityTv.setText(humidity + "%");

                    String windSpeed = jsonObject.getJSONObject("wind").getString("speed");
                    windSpeedTv.setText(windSpeed + " km/hr");

                    String sunRise = jsonObject.getJSONObject("sys").getString("sunrise");
                    sunRiseTv.setText(sunRise);

                    String sunSet = jsonObject.getJSONObject("sys").getString("sunset");
                    sunsSetTv.setText(sunSet);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
    // code will be executed if user gives the permission fro current location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions granted ", Toast.LENGTH_SHORT).show();
                getCurrentLocationInfo();;

            }
            else{

            }
        }

    }
}
