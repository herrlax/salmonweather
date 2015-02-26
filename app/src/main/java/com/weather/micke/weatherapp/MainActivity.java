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
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;


public class MainActivity extends Activity {

    private String API_KEY = "96fbe7109d5b5c82dedeca650dda67ac";
    private WeatherActivity weatherActivity;

    // UI components for main app
    private TextView cityTextView;
    private TextView tempTextView;
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
            weatherActivity.fetchWeather();
            System.out.println("Temp: " + weatherActivity.getModel().getTemp() + " (FETCHED)");

            // if no answer after 10 seconds time out
            if(seconds >= 10) {
                System.out.println("SYSTEM TIME OUT");
                spinnerDialog.cancel();
                //spinnerDialog.cancel();

            } else if(weatherActivity.getModel().getTemp() != -237) {

                // Updates UI
                updateUI();


            } else {

                updateHandler.postDelayed(runner, 1000); // Updates each 1 seconds

            }
        }
    };

    /**
     * Displaying spinner when loading weather.
     */
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
    protected void onResume() {
        super.onResume();

        System.out.println("Resumed activity");

        if(weatherActivity != null
                && weatherActivity.getModel() != null) {
            initUI();
            cityTextView.setText(weatherActivity.getModel().getCity());
            tempTextView.setText(weatherActivity.getModel().getTemp() + " °C");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        weatherActivity = new WeatherActivity(API_KEY);

        dialogText = new EditText(this);

        cityDialog = new AlertDialog.Builder(this);
        cityDialog.setMessage("Please enter city!");
        cityDialog.setCancelable(true);

        cityDialog.setView(dialogText);
        cityDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // resets data
                weatherActivity.getModel().resetData();

                weatherActivity.getModel().setCity("" + dialogText.getText());
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
        //resetModel();
        //setCity("London");

        initUI();

        // loadWeather(iconImageView);


    }
    public void initUI() {

        cityTextView = (TextView) findViewById(R.id.cityTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        //humTextView = (TextView) findViewById(R.id.humTextView);
        iconImageView = (ImageView) findViewById(R.id.imageView);

    }


    /**
     * Selecting city from app.
     * @param view view calling this function
     */
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
        // stops the spinner when weather is fetched
        spinnerDialog.cancel();
        cityTextView.setText(weatherActivity.getModel().getCity());
        tempTextView.setText(weatherActivity.getModel().getTemp() + " °C");
        //humTextView.setText("Humidity: " + weatherActivity.getModel().getHumidity() + "%");

        setWeatherIcon();
    }

    public void setWeatherIcon() {
        iconImageView.setImageResource(weatherActivity.getWeatherIcon());

        /*if(weatherActivity.getModel().getCloudy()) { // If it's cloudy..
            if(weatherActivity.getModel().getRainy()) { // and it's rainy..
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

        }*/
    }





}
