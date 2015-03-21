package com.weather.micke.weatherapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Created by Mikael Malmqvist on 2015-02-26.
 */
public class WeatherConfigActivity extends Activity {

    private String API_KEY = "96fbe7109d5b5c82dedeca650dda67ac";
    private EditText cityText;
    private Button okButton;
    private int mAppWidgetId = 0;
    private Intent parentIntent;
    private WeatherActivity weatherActivity;
    private ProgressDialog spinnerDialog;
    private AppWidgetManager appWidgetManager;
    private RemoteViews views;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);

        setResult(RESULT_CANCELED);

        // Get the widget id from the intent that started this Activity

        parentIntent = getIntent();
        Bundle extras = parentIntent.getExtras();

        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        initUI();

    }

    public void initUI() {
        cityText = (EditText) findViewById(R.id.editCityText);
        okButton = (Button) findViewById(R.id.button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Finished Config with city: " + cityText.getText());


                appWidgetManager = AppWidgetManager.getInstance(WeatherConfigActivity.this);

                // Creates the new view for our widget
                views = new RemoteViews(WeatherConfigActivity.this.getPackageName(),
                        R.layout.wideget_layout);

                // Adds the city to it
                //views.setTextViewText(R.id.cityTextView, cityText.getText());
                views.setTextViewText(R.id.cityTextView, cityText.getText());
                //views.setTextViewText(R.id.tempTextView, "45°");
                //views.setTextViewText(R.id.tempTextView, weatherActivity.getModel().getTemp() + "°");


                // Binds a shortcut into settings to it
                PendingIntent pendingIntent = PendingIntent.getActivity(WeatherConfigActivity.this, 0, parentIntent, 0);
                views.setOnClickPendingIntent(R.id.cityTextView, pendingIntent);

                // Fetching weather
                weatherActivity = new WeatherActivity(API_KEY);
                weatherActivity.getModel().setCity(cityText.getText().toString());
                //weatherActivity.fetchWeather();

                loadWeather(cityText);
                //views.setTextViewText(R.id.tempTextView, "4.3°");



            }
        });
    }

    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable runner = new Runnable() {
        int seconds = 0;

        @Override
        public void run() {
            seconds++;

            // Fetches weather data
            weatherActivity.fetchWeather();

            // if no answer after 10 seconds time out
            if(seconds >= 10) {
                System.out.println("SYSTEM TIME OUT");
                spinnerDialog.cancel();
                //spinnerDialog.cancel();

            } else if(weatherActivity.getModel().getTemp() != -237) {


                //views.setTextViewText(R.id.tempTextView, weatherActivity.getModel().getTemp() + "°");


                views.setImageViewResource(R.id.imageView, weatherActivity.getWeatherIcon());

                spinnerDialog.cancel();
                appWidgetManager.updateAppWidget(mAppWidgetId, views);


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

    public void updateUI() {

        // Calling the widgetProvider
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        resultValue.putExtra("city", cityText.getText());
        setResult(RESULT_OK, resultValue);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);


        System.out.println("Updated UI");
        System.out.println(weatherActivity.getModel().getTemp() + "");
        sendBroadcast(resultValue);
        //views.setTextViewText(R.id.cityTextView, weatherActivity.getModel().getCity());
        //views.setTextViewText(R.id.cityTextView, "ANUS");

        finish();
    }
}
