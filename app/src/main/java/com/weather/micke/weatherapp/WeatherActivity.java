package com.weather.micke.weatherapp;

import android.app.Activity;
import android.os.AsyncTask;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by Mikael Malmqvist on 2015-02-26.
 */
public class WeatherActivity extends Activity {
    private WeatherModel model;
    private WeatherPresenter presenter;
    private String API_KEY;

    public WeatherActivity() {
        model = new WeatherModel();
        presenter = new WeatherPresenter();
    }

    public WeatherActivity(String API_KEY) {
        model = new WeatherModel();
        presenter = new WeatherPresenter();
        this.API_KEY = API_KEY;
    }

    public WeatherModel getModel() {
        return model;
    }

    public void fetchWeather() {
        new FetchWeatherTask().execute();
    }

    /**
     * Async task to fetch weather data from OWM API.
     */
    private class FetchWeatherTask extends AsyncTask {

        OpenWeatherMap owm;
        CurrentWeather cwd;

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                fetchWeatherData();

            } catch (Exception e) {
                if (e instanceof IOException) {
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
            owm = new OpenWeatherMap(OpenWeatherMap.Units.METRIC, OpenWeatherMap.Language.SWEDISH, API_KEY);

            cwd = owm.currentWeatherByCityName(model.getCity());
            if (cwd.isValid()) {
                System.out.println("NOW UPDATING WEATHER");

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

                    if (cwd.hasMainInstance()) {
                        double temp = cwd.getMainInstance().getTemperature();
                        temp = presenter.round1dec(temp);
                        System.out.println("Temp: " + temp + " °C");


                        /*double humidity = cwd.getMainInstance().getHumidity();
                        System.out.println("Humidity: " + humidity + "%");


                        model.setHumidity(humidity);*/
                        System.out.println("JUST SET THE TEMP TO " + temp);
                        model.setTemp(temp);
                    }

                    if (cwd.hasCloudsInstance()) {
                        boolean cloudy = presenter.isCloudy(cwd.getCloudsInstance().getPercentageOfClouds());
                        System.out.println("CLOUDS: " + cwd.getCloudsInstance().getPercentageOfClouds() + "%");
                        model.setCloudy(cloudy);
                    }

                    if (cwd.hasRainInstance()) {
                        boolean rainy = presenter.isRainy(cwd.getRainInstance().getRain());
                        System.out.println("RAIN: " + cwd.getRainInstance().getRain() + " mm rain/hour");
                        model.setRainy(rainy);
                    }

                }

            } else {

                System.out.println("API KEY NOT VALID");
            }

        }
    }

    public int getWeatherIcon() {
        if(model.getCloudy()) { // If it's cloudy..
            if(model.getRainy()) { // and it's rainy..
                // TODO set clouds with rain icon
                System.out.println("IT'S RAINY");
                return(R.drawable.rainy);

            } else { // just cloudy..
                // TODO set cloud icon
                System.out.println("IT'S JUST CLOUDY");
                return(R.drawable.cloudy);
            }
        } else { // sunny nice day!
            // TODO set sun icon
            System.out.println("IT'S SUNNY");
            return(R.drawable.sun);

        }
    }

}