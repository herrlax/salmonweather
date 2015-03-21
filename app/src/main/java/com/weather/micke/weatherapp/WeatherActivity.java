package com.weather.micke.weatherapp;

import android.app.Activity;
import android.os.AsyncTask;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;

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

    private FetchWeatherTask task;

    public void fetchWeather() {

        if(task == null || task.getStatus() != AsyncTask.Status.RUNNING) {
            System.out.println("EXECUTED SHIT");
            task = new FetchWeatherTask();
            task.execute();
        }

        System.out.println("STATUS: " + task.getStatus());

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
                // Resetting error boolean
                fetchWeatherData();

            } catch (Exception e) {

                // If user entered invalid indata
                if (e instanceof IOException) {
                    System.out.println(e.getMessage());

                    // setting error boolean for invalid input (-20000)
                    model.ERROR_CODE = -20000;


                }

                // If the API-key is invalid
                if(e instanceof InvalidKeyException) {
                    System.out.println(e.getMessage());

                    // setting error boolean for invalid API_key (-20001)
                    model.ERROR_CODE = -20001;
                }

                System.out.println("SOMETHING WENT WRONG!!!**************** " + e.getClass());
            }


            return null;
        }

        public void fetchWeatherData() throws IOException, MalformedURLException, JSONException, InvalidKeyException {

            OpenWeatherMap.Units system = OpenWeatherMap.Units.IMPERIAL;

            // Setting metric or imperial ..
            if(model.useCelsius()) {
                system = OpenWeatherMap.Units.METRIC;
            }


            // declaring object of "OpenWeatherMap" class
            owm = new OpenWeatherMap(system, OpenWeatherMap.Language.ENGLISH, API_KEY);


            cwd = owm.currentWeatherByCityName(model.getCity());


            if(!cwd.isValid() || !cwd.hasCityName()) {
                //throw new IOException("INVALID CITY NAME!");
                System.out.println("INVALID INPUT!!!!!!!!!!!!!!!!!!!!!!!!");
                model.ERROR_CODE = -20000;

            } else {

                System.out.println("NOW UPDATING WEATHER");

                System.out.println("City: " + cwd.getCityName());
                model.setCity(cwd.getCityName());

                // If weather data is available
                if (cwd.hasWeatherInstance()) {
                    //int temp = Math.round((cwd.getMainData_Object().getTemperature() - 32)/18);
                    // double temp = cwd.getMainData_Object().getTemperature();

                    if (cwd.hasMainInstance()) {
                        double temp = cwd.getMainInstance().getTemperature();

                        // If using celsius convert temp
                        temp = presenter.round1dec(temp);


                        System.out.println("Temp: " + temp + " °");


                        /*double humidity = cwd.getMainInstance().getHumidity();
                        System.out.println("Humidity: " + humidity + "%");


                        model.setHumidity(humidity);*/
                        model.setTemp(temp);
                    }

                    if (cwd.hasCloudsInstance()) {
                        boolean cloudy = presenter.isCloudy(cwd.getCloudsInstance().getPercentageOfClouds());
                        System.out.println("CLOUDS: " + cwd.getCloudsInstance().getPercentageOfClouds() + "%");
                        model.setCloudy(cloudy);
                    }

                    if (cwd.hasRainInstance()) {
                        boolean rainy = presenter.isRainy(cwd.getRainInstance().getRain3h());
                        System.out.println("RAIN: " + cwd.getRainInstance().getRain3h() + " mm rain/hour");
                        model.setRainy(rainy);
                    }

                }

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