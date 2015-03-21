package com.weather.micke.weatherapp.utils;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        System.out.println();


        for(int i = 0; i < appWidgetIds.length; i++) {
            // getting current widget to update
            int appWidgetId = appWidgetIds[i];

            // Create an intent to launch the activity
            Intent intent = new Intent(context, WeatherActivity.class);
            System.out.println("GOT FROM INTENT: " + intent.getStringExtra("city") + " ****************");


            // Tell the appwidgetmanager to update the current app
            //appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context,
                MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}
