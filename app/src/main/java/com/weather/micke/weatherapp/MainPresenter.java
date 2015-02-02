package com.weather.micke.weatherapp;

/**
 * Created by Mikael Malmqvist on 2015-01-09.
 * Handles logic for the activity based on input data.
 */
public class MainPresenter {

    /**
     * Decides if it's rainy or not.
     * @param rainHours hours of rain this day.
     * @return true if it's 8h or more rain this day.
     */
    public boolean isRainy(float rainHours) {
        return (rainHours >= 0.1);
    }

    /**
     * Decides if it's cloudy or not.
     * @param percentClouds percent of clouds right now.
     * @return true if it's 30 % or more clouds right now.
     */
    public boolean isCloudy(float percentClouds) {
        return (percentClouds >= 50);
    }

    /**
     * Converts fahrenheit to Celsius.
     * @param fahrenheit degrees in fahrenheit.
     * @return degrees in celsius
     */
    public double fahrenheitToCelsius(double fahrenheit) {
        double value = ((fahrenheit - 32)/1.8) + 0.05;
        value = (int) (value * 10);

        return value / 10.0;

    }

    /**
     * Rounds to 1 decimal.
     * @param value to round
     * @return rounded value
     */
    public double round1dec(double value) {
        value += 0.05;

        value = (int) (value * 10);
        return value / 10.0;

    }


}
