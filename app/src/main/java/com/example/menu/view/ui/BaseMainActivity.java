package com.example.menu.view.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.menu.AddDataAcctivity;
import com.example.menu.App;
import com.example.menu.BuildConfig;
import com.example.menu.MapActivity;
import com.example.menu.R;
import com.example.menu.database.DatabaseHelper;
import com.example.menu.model.DataModel;
import com.example.menu.services.model.ResponseWeather;
import com.example.menu.services.model.TimeDate;
import com.example.menu.services.repository.OpenWeather;
import com.example.menu.viewModel.WeatherCoordViewModel;
import com.example.menu.viewModel.WeatherViewModel;
import com.example.menu.weather.CitySelection;
import com.example.menu.weather.DialogCity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import static com.example.menu.weather.CitySelection.TOWN;


public class BaseMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private OpenWeather openWeather;
    private TextView textTemp; // Температура (в градусах)
    private TextView description;
    private TextInputEditText editCity;
    private TextView inputCity;
    private TextView date;
    private TextView speed; // скорость ветра
    private TextView wind; // ветер
    private TextView ms; // м/с
    private TextView clouds; // облачность
    private TextView humidity; // влажность
    private TextView pressure; // давление
    private TextView sunriseName;
    private TextView sunsetName;
    private ImageView imageView;
    private String pngUrl; //url картинки openweathermap.org/img/wn/(11d).png
    private String url;
    final String metric = "metric";
    final String lang = "ru";
    private LatLng location;
    FrameLayout frameLayout;
    ProgressBar progressBar;
    private String NAME_CITY = "nameCity"; // для SharedPreferences
    private String LATIT = "latit"; // для SharedPreferences
    private String LONTIT = "lontit"; // для SharedPreferences

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
  //      initRetorfit();
        initGui();
        initNameCity();
        initCoord();
        picasso(pngUrl);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initGui() {
        frameLayout = findViewById(R.id.linear);
        description = findViewById(R.id.description);
        inputCity = findViewById(R.id.inputCity);
        textTemp = findViewById(R.id.temp);
        date = findViewById(R.id.date);
        speed = findViewById(R.id.speed);
        clouds = findViewById(R.id.clouds);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.press);
        imageView = findViewById(R.id.coord);
        sunriseName = findViewById(R.id.sunrise);
        sunsetName = findViewById(R.id.sunset);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initNameCity() {
        Intent intent = getIntent();
        String town = intent.getStringExtra(TOWN);
        if (town == null) {
            loadSharedPrefs();
            town = inputCity.getText().toString();
            requestRetrofitnameCity(town, metric, BuildConfig.WEATHER_API_KEY, lang);
        } else {
            requestRetrofitnameCity(town, metric, BuildConfig.WEATHER_API_KEY, lang);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(3000);
                        saveSharedPrefs();
                        result();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //прием координат lon и lat от MapActivity
    private void initCoord() {
        Intent intent = getIntent();
        String latit = intent.getStringExtra(MapActivity.LAT);
        String lontit = intent.getStringExtra(MapActivity.LON);
        requestRetrofit(latit, lontit, metric, BuildConfig.WEATHER_API_KEY, lang);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(3000);
                    saveSharedPrefs();
                    result();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void picasso(String png) {
        png = pngUrl;
        url = "https://openweathermap.org/img/wn/";
        Picasso.get()
                .load(url + png + ".png")
                .placeholder(R.drawable.w_03d)
                .into(imageView);
    }

    // погода по координатам lat и lon
    private void requestRetrofit(String lat, String lon, String metric, String keyApi, String lang) {
        WeatherCoordViewModel weatherCoordViewModel = ViewModelProviders.of(this).get(WeatherCoordViewModel.class);
        weatherCoordViewModel.getWeatherCoord(lat, lon, metric, keyApi, lang).observe(this, new Observer<ResponseWeather>() {

            @Override
            public void onChanged(ResponseWeather responseWeather) {
                Boolean result = weatherCoordViewModel.getSuccess().getValue();
                if (result && responseWeather != null) {
                    long temp = responseWeather.getMain().getTemp();
                    textTemp.setText(Long.toString(temp) + " ℃");
                    String dis = responseWeather.getWeather().get(0).getDescription();
                    description.setText(dis);

                    pngUrl = (responseWeather.getWeather().get(0).getIcon());
                    picasso(pngUrl); // передаем "weather: icon"

                    String nameCity = responseWeather.getName();
                    inputCity.setText(nameCity);

                    date.setText((TimeDate.dateNow()));

                    double speedWind = responseWeather.getWind().getSpeed();
                    speed.setText(Double.toString(speedWind));

                    int nameClouds = responseWeather.getClouds().getAll();
                    clouds.setText(Integer.toString(nameClouds));

                    int nameHumidity = responseWeather.getMain().getHumidity();
                    humidity.setText(Integer.toString(nameHumidity));

                    int namePress = responseWeather.getMain().getPressure();
                    pressure.setText(Integer.toString(namePress));

                    String sunr = TimeDate.timeStamp(responseWeather.getSys().getSunrise());
                    sunriseName.setText(sunr);
                    String suns = TimeDate.timeStamp(responseWeather.getSys().getSunset());
                    sunsetName.setText(suns);
                }
            }
        });
    }

    // погода по названию города
    private void requestRetrofitnameCity(String cityCountry, String metric, String keyApi, String lang) {
        WeatherViewModel weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        weatherViewModel.getWeatherInfo(cityCountry, metric, keyApi, lang).observe(this, new Observer<ResponseWeather>() {
            @Override
            public void onChanged(ResponseWeather responseWeather) {
                Boolean result = weatherViewModel.getSuccess().getValue();
                if (result && responseWeather != null) {
                    frameLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    long temp = responseWeather.getMain().getTemp();
                    textTemp.setText(Long.toString(temp) + " ℃");
                    String dis = responseWeather.getWeather().get(0).getDescription();
                    description.setText(dis);

                    pngUrl = (responseWeather.getWeather().get(0).getIcon());
                    picasso(pngUrl); // передаем "weather: icon"

                    String nameCity = responseWeather.getName();
                    inputCity.setText(nameCity);

                    date.setText((TimeDate.dateNow()));

                    double speedWind = responseWeather.getWind().getSpeed();
                    speed.setText(Double.toString(speedWind));

                    int nameClouds = responseWeather.getClouds().getAll();
                    clouds.setText(Integer.toString(nameClouds));

                    int nameHumidity = responseWeather.getMain().getHumidity();
                    humidity.setText(Integer.toString(nameHumidity));

                    int namePress = responseWeather.getMain().getPressure();
                    pressure.setText(Integer.toString(namePress));

                    String sunr = TimeDate.timeStamp(responseWeather.getSys().getSunrise());
                    sunriseName.setText(sunr);
                    String suns = TimeDate.timeStamp(responseWeather.getSys().getSunset());
                    sunsetName.setText(suns);
                } else {
                    DialogCity dialog = new DialogCity();
                    dialog.show(getSupportFragmentManager(), "custom");
                    frameLayout.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(2000);
                                Intent intent = new Intent(getApplicationContext(), CitySelection.class);
                                startActivity(intent);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    // определение погоды и населённый пункт пользователя с помощью геолокации "Текущая позиция"
    @SuppressLint("MissingPermission")
    private void requestLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locationManager.getBestProvider(criteria, true);

        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 10000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();// Широта
                    double lng = location.getLongitude();// Долгота
                    String latitude = Double.toString(lat);
                    String longitude = Double.toString(lng);
                    requestRetrofit(latitude, longitude, metric, BuildConfig.WEATHER_API_KEY, lang);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }
    }


    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            Intent intent = new Intent(this, CitySelection.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initDrawer(Toolbar toolbar) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.setScrimColor(android.graphics.Color.BLUE);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
        } else if (id == R.id.nav_location) {
            requestLocation();
        } else if (id == R.id.nav_geolocation) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, AddDataAcctivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void saveSharedPrefs() {
        String preferenceFileName = "name";
        SharedPreferences sharedPref = getSharedPreferences(preferenceFileName, MODE_PRIVATE);
        savePreferences(sharedPref);    // сохранить настройки
    }

    public void loadSharedPrefs() {
        String preferenceFileName = "name";
        SharedPreferences sharedPref = getSharedPreferences(preferenceFileName, MODE_PRIVATE);
        loadPreferences(sharedPref);
    }

    private void savePreferences(SharedPreferences sharedPref) {
        String keys = inputCity.getText().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NAME_CITY, keys);
        editor.apply();//сохраняет в backgraund потоке
    }

    private void loadPreferences(SharedPreferences sharedPref) {
        String keys = inputCity.getText().toString();
        String valueFirst = sharedPref.getString(NAME_CITY, keys);
        inputCity.setText(valueFirst);
    }

    public void result() {
        DatabaseHelper databaseHelper = App.getInstance().getDatabaseInstance();

        DataModel model = new DataModel();

        model.setCity(inputCity.getText().toString());
        model.setTemp(textTemp.getText().toString());
        model.setDate(date.getText().toString());

        databaseHelper.getDataDao().insert(model);
    }

}
