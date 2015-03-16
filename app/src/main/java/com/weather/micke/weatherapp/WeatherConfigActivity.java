package com.weather.micke.weatherapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Created by Mikael Malmqvist on 2015-02-26.
 */
public class WeatherConfigActivity extends Activity {

    private EditText cityText;
    private Button okButton;
    private int mAppWidgetId = 0;
    private Intent parentIntent;


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


                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WeatherConfigActivity.this);

                RemoteViews views = new RemoteViews(WeatherConfigActivity.this.getPackageName(),
                        R.layout.wideget_layout);

                views.setTextViewText(R.id.cityTextView, cityText.getText());

                appWidgetManager.updateAppWidget(mAppWidgetId, views);

                // Calling the widgetProvider
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                resultValue.putExtra("city", cityText.getText());
                setResult(RESULT_OK, resultValue);
                finish();

            }
        });
    }
}
