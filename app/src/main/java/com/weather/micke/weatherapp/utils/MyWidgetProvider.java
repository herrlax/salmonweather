package com.weather.micke.weatherapp.utils;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.weather.micke.weatherapp.MainActivity;
import com.weather.micke.weatherapp.R;
import com.weather.micke.weatherapp.WeatherActivity;

/**
 * Created by Mikael Malmqvist on 2015-01-09.
 */
public class MyWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        System.out.println("NOW UPDATING WIDGET");

        for(int i = 0; i < appWidgetIds.length; i++) {
            // getting current widget to update
            int appWidgetId = appWidgetIds[i];

            // Create an intent to launch the activity
            Intent intent = new Intent(context, WeatherActivity.class);

            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews myNewView = new RemoteViews(context.getPackageName(), R.layout.wideget_layout);

            // WeatherActivity to fetch weather from
            WeatherActivity weatherActivity = new WeatherActivity();
            weatherActivity.getModel().setCity("gothenburg");

            weatherActivity.fetchWeather();

            int ost = 0;
            while(ost < 999999999) {
                ost++;
                // waiting for valid weather data to be fetched
                // not the most glorious way of doing this though..
            }

            myNewView.setTextViewText(R.id.cityTextView, weatherActivity.getModel().getCity());
            myNewView.setTextViewText(R.id.tempTextView, weatherActivity.getModel().getTemp() + " °C");
            myNewView.setImageViewResource(R.id.imageView, weatherActivity.getWeatherIcon());

            /*mainActivity.init();
            mainActivity.resetModel();
            mainActivity.setCity("Stockholm");
            mainActivity.loadWeather(null);

            myNewView.setTextViewText(R.id.cityTextView, mainActivity.getModel().getCity());
            myNewView.setTextViewText(R.id.humTextView, mainActivity.getModel().getHumidity() + " %");
            myNewView.setTextViewText(R.id.tempTextView, mainActivity.getModel().getTemp() + " °C");
            myNewView.setImageViewResource(R.id.imageView, mainActivity.getWeatherIcon());*/



            //myNewView.setOnClickPendingIntent(R.id.imageView, pendingIntent);

            // Tell the appwidgetmanager to update the current app
            appWidgetManager.updateAppWidget(appWidgetId, myNewView);

        }
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context,
                MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}
