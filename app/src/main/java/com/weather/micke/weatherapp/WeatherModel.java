package com.weather.micke.weatherapp;

/**
 * Created by Mikael Malmqvist on 2015-01-07.
 * Model containing necessary data.
 */
public class WeatherModel {
    private double temp;
    private double humidity;
    private String city;
    private boolean rainy;
    private boolean cloudy;
    private boolean celsius = true;
    int ERROR_CODE;

    public WeatherModel() {
        resetData();
    }

    public void resetData() {
        temp = -237;
        city = "";
        humidity = 101.0;
        rainy = false;
        cloudy = false;

        System.out.println("______________________ RESET DATA _________________________");
    }

    public void setRainy(boolean rainy) {
        this.rainy = rainy;
    }

    public void setCloudy(boolean cloudy) {
        this.cloudy = cloudy;
    }

    public void setTemp (double temp) {
        this.temp = temp;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public void setCity (String city) {
        this.city = city;
    }

    public String getCity (){
        return city;
    }

    public double getTemp () {
        return temp;
    }

    public double getHumidity() {
        return humidity;
    }

    public boolean getCloudy() {
        return cloudy;
    }

    public boolean getRainy() {
        return rainy;
    }

    public void setCelsius(boolean celsius) {
        this.celsius = celsius;
    }

    public boolean useCelsius() {
        return celsius;
    }
}
