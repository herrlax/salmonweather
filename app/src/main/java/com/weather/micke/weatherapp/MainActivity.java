package com.weather.micke.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.DailyForecast;
import net.aksingh.owmjapis.HourlyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.IllegalFormatCodePointException;


public class MainActivity extends Activity {

    private String API_KEY = "96fbe7109d5b5c82dedeca650dda67ac";
    private MainModel model;
    private MainPresenter presenter;

    // UI components
    private TextView cityTextView;
    private TextView tempTextView;
    private TextView humTextView;
    private ImageView iconImageView;

    private EditText dialogText;

    // Dialogs
    private ProgressDialog spinnerDialog;
    private AlertDialog.Builder cityDialog;

    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable runner = new Runnable() {
        int seconds = 0;

        @Override
        public void run() {
            seconds++;

            // Fetches weather data
            new FetchWeatherTask().execute();

            // if no answer after 20 seconds time out
            if(seconds >= 10) {
                System.out.println("SYSTEM TIME OUT");
                spinnerDialog.cancel();

            } else if(model.getTemp() != -237 && model.getHumidity() != 101.0) {

                // Updates UI
                updateUI();

                spinnerDialog.cancel();

            } else {

                updateHandler.postDelayed(runner, 1000); // Updates each 1 seconds

            }
        }
    };

    public void startSpinner() {

        spinnerDialog = new ProgressDialog(this);
        spinnerDialog.setMessage("Loading weather");
        spinnerDialog.setTitle("Loading");
        spinnerDialog.setCancelable(true);
        spinnerDialog.show();
    }

    public void loadWeather(View view) {

        startSpinner();
        runner.run();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new MainModel();
        presenter = new MainPresenter();

        dialogText = new EditText(this);

        cityDialog = new AlertDialog.Builder(this);
        cityDialog.setMessage("Please enter city!");
        cityDialog.setCancelable(true);

        cityDialog.setView(dialogText);
        cityDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // resets data
                model.resetData();

                model.setCity("" + dialogText.getText());
                loadWeather(iconImageView);

            }
        });

        cityDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("CANCEL");
            }
        });

        //selectCity(iconImageView);
        cityDialog.show();

        cityTextView = (TextView) findViewById(R.id.cityTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        humTextView = (TextView) findViewById(R.id.humTextView);
        iconImageView = (ImageView) findViewById(R.id.imageView);

        // loadWeather(iconImageView);


    }

    public void selectCity(View view) {
        dialogText = new EditText(this);
        cityDialog.setView(dialogText);
        cityDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates UI components based on data in model.
     */
    public void updateUI() {
        cityTextView.setText(model.getCity());
        tempTextView.setText(model.getTemp() + " °C");
        humTextView.setText("Humidity: " + model.getHumidity() + "%");

        setWeatherIcon();
    }

    public void setWeatherIcon() {
        if(model.getCloudy()) { // If it's cloudy..
            if(model.getRainy()) { // and it's rainy..
                // TODO set clouds with rain icon
                iconImageView.setImageResource(R.drawable.rainy);
                System.out.println("IT'S RAINY");
            } else { // just cloudy..
                // TODO set cloud icon
                iconImageView.setImageResource(R.drawable.cloudy);
                System.out.println("IT'S JUST CLOUDY");
            }
        } else { // sunny nice day!
            // TODO set sun icon
            iconImageView.setImageResource(R.drawable.sun);
            System.out.println("IT'S SUNNY");

        }
    }

    /**
     * Async task to fetch weather data from OWM API.
     */
    private class FetchWeatherTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                fetchWeatherData();

            } catch (Exception e){
                if(e instanceof IOException) {
                    // insert fake data
                    model.setTemp(-230);
                    model.setHumidity(99.0);
                }


                System.out.println("SOMETHING WENT WRONG!!!**************** " + e.getClass());


            }


            return null;
        }

        public void fetchWeatherData() throws IOException, MalformedURLException, JSONException {
            // declaring object of "OpenWeatherMap" class

            OpenWeatherMap owm = new OpenWeatherMap(OpenWeatherMap.Units.METRIC, OpenWeatherMap.Language.SWEDISH, API_KEY);

            CurrentWeather cwd = owm.currentWeatherByCityName(model.getCity());


            if(cwd.isValid()) {

                // checking if city name is available
                if (cwd.hasCityName()) {
                    //printing city name from the retrieved data
                    System.out.println("City: " + cwd.getCityName());
                    model.setCity(cwd.getCityName());

                } else {
                    System.out.println("Illegal name");
                    throw new IOException();
                }

                // If weather data is available
                if (cwd.hasWeatherInstance()) {
                    //int temp = Math.round((cwd.getMainData_Object().getTemperature() - 32)/18);
                    // double temp = cwd.getMainData_Object().getTemperature();

                    if(cwd.hasMainInstance()) {
                        double temp = cwd.getMainInstance().getTemperature();
                        temp = presenter.round1dec(temp);
                        System.out.println("Temp: " + temp + " °C");


                        double humidity = cwd.getMainInstance().getHumidity();
                        System.out.println("Humidity: " + humidity + "%");


                        model.setHumidity(humidity);
                        model.setTemp(temp);
                    }


                    if(cwd.hasCloudsInstance()) {
                        boolean cloudy = presenter.isCloudy(cwd.getCloudsInstance().getPercentageOfClouds());
                        System.out.println("CLOUDS: " + cwd.getCloudsInstance().getPercentageOfClouds() + "%");
                        model.setCloudy(cloudy);
                    }

                    if(cwd.hasRainInstance()) {
                        boolean rainy = presenter.isRainy(cwd.getRainInstance().getRain());
                        System.out.println("RAIN: " + cwd.getRainInstance().getRain() + "mm rain/hour");
                        model.setRainy(rainy);
                    }

                }

            } else {

                System.out.println("API KEY NOT VALID");
            }


        }

    }
}
