package com.weather.micke.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;


public class MainActivity extends Activity {

    private String API_KEY = "96fbe7109d5b5c82dedeca650dda67ac";
    private WeatherActivity weatherActivity;

    // UI components for main app
    private TextView cityTextView;
    private TextView tempTextView;
    private AutoCompleteTextView searcher;

    private ImageView iconImageView;
    private EditText dialogText;

    InputMethodManager keyboard;

    // Dialogs
    private ProgressDialog spinnerDialog;

    //private int seconds = 0;

    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable runner = new Runnable() {

        @Override
        public void run() {
            System.out.println("ENTERED RUN");
            /*System.out.println("run: " + seconds);
            seconds++;*/

            // Fetches weather data
            weatherActivity.fetchWeather();


            System.out.println("ERROR CODE: " + weatherActivity.getModel().ERROR_CODE);

            // If an error due to invalid indata or API-key
            if(weatherActivity.getModel().ERROR_CODE != 0) {

                // invalid indata
                if(weatherActivity.getModel().ERROR_CODE == -20000) {

                }

                // invalid api-key
                if(weatherActivity.getModel().ERROR_CODE == -20001) {

                }

                spinnerDialog.cancel();

                // display error dialog
                new AlertDialog.Builder(MainActivity.this).setTitle("Invalid location")
                        .setMessage("Can't find a match for entered location!")
                        .setPositiveButton("Okay", null)
                        .setIcon(R.drawable.ic_warning_grey600_48dp)
                        .show();

            } else if(weatherActivity.getModel().getTemp() != -237) {

                spinnerDialog.cancel();
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
        spinnerDialog = new ProgressDialog(MainActivity.this);
        spinnerDialog.setMessage("Loading weather");
        spinnerDialog.setTitle("Loading");
        spinnerDialog.setCancelable(true);
        spinnerDialog.show();
    }

    public void loadWeather(View view) {
        weatherActivity.getModel().ERROR_CODE = 0;
        startSpinner();
        //seconds = 0;
        runner.run();

    }

    public void focus(View view) {
        view.setBackground(getDrawable(R.drawable.focus_card));

        // if we clicked one card, unfocus the other
        /*if(view == findViewById(R.id.firstCard)) {
            findViewById(R.id.secondCard).setBackground(getDrawable(R.drawable.card));
        } else if(view == findViewById(R.id.secondCard)) {
            findViewById(R.id.firstCard).setBackground(getDrawable(R.drawable.card));
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("Resumed activity");

        /*if(weatherActivity != null
                && weatherActivity.getModel() != null) {
            initUI();
            cityTextView.setText(weatherActivity.getModel().getCity());
            if(weatherActivity.getModel().getTemp() > -200) {
                tempTextView.setText(weatherActivity.getModel().getTemp() + " °C");
            }
        }*/

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        weatherActivity = new WeatherActivity(API_KEY);

        searcher = (AutoCompleteTextView) findViewById(R.id.search_text_view);

        // If there's a preferred location set, load weather
        if(!getPrefPlace().equals("")) {
            weatherActivity.getModel().setCity(getPrefPlace());
            loadWeather(iconImageView);
            searcher.setHint(weatherActivity.getModel().getCity());
        }


        System.out.println("LOADING: " + weatherActivity.getModel().getCity());

        // Handles when user clicks "done" button on keyboard
        searcher.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                System.out.println("view: " + view);

                if(keyEvent.getAction() == keyEvent.ACTION_DOWN) {

                    // If done is clicked
                    if(i == 66) {
                        // resets data

                        weatherActivity.getModel().resetData();

                        weatherActivity.getModel().setCity(searcher.getText().toString().trim());

                        // if no city entered, set city to last entered city (i.e the hint text)
                        if(weatherActivity.getModel().getCity().equals("")) {
                            System.out.println("NO CITY ENTERED!!!!");
                            weatherActivity.getModel().setCity(searcher.getHint().toString().trim());
                        }

                        System.out.println("LOADING: " + weatherActivity.getModel().getCity());

                        loadWeather(iconImageView);


                        // Hides keyboard
                        keyboard.hideSoftInputFromWindow(searcher.getWindowToken(), 0);

                        // clear text
                        searcher.setText("");
                        searcher.setHint(weatherActivity.getModel().getCity());
                    }
                }



                return true;
            }
        });

        keyboard = (InputMethodManager) getSystemService(
                this.INPUT_METHOD_SERVICE);

        initUI();



    }
    public void initUI() {
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        //humTextView = (TextView) findViewById(R.id.humTextView);
        iconImageView = (ImageView) findViewById(R.id.imageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        // Detect what button in menu was clicked
        switch(id) {
            case R.id.action_file:
                loadPrefLocation();
                return true;

            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Setting location as preferred.
     * @param view pref button.
     */
    public void setPref(View view) {
        // Check for a valid location
        if(!((TextView)findViewById(R.id.cityTextView)).getText()
                .toString()
                .trim()
                .equals("")) {

            SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            // Set or remove saved place
            if(getPrefPlace().equals(cityTextView.getText().toString())) {
                // If the place is a saved place, UNSAVE it!
                ((ImageButton) findViewById(R.id.prefHeart)).setImageDrawable(getDrawable(R.drawable.ic_favorite_outline_grey600_48dp));
                editor.putString(getString(R.string.saved_place), "");

            } else {
                // If the place isn't saved already, S(H)AVE it!
                ((ImageButton) findViewById(R.id.prefHeart)).setImageDrawable(getDrawable(R.drawable.ic_favorite_grey600_48dp));
                editor.putString(getString(R.string.saved_place), ((TextView)findViewById(R.id.cityTextView)).getText().toString());
            }

            editor.commit();

            System.out.println("SAVED TO SHARED PREFERENCES: " + getPrefPlace());
        }

    }

    /**
     * Gets the save preferred location.
     * @return preferred location.
     */
    private String getPrefPlace() {
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_place);
        return sharedPref.getString(getString(R.string.saved_place), defaultValue);
    }

    /**
     * Open preferred location
     */
    public void loadPrefLocation() {

        // If there's a saved place
        if(!getPrefPlace().equals("")) {
            weatherActivity.getModel().resetData();

            weatherActivity.getModel().setCity(getPrefPlace());

            loadWeather(iconImageView);

            // clear text
            searcher.setText("");
            searcher.setHint(weatherActivity.getModel().getCity());
        } else {
            // display error dialog
            new AlertDialog.Builder(MainActivity.this).setTitle("No preferred location")
                    .setMessage("You haven't set a preferred location yet!")
                    .setPositiveButton("Okay", null)
                    .setIcon(R.drawable.ic_warning_grey600_48dp)
                    .show();
        }

    }


    /**
     * Updates UI components based on data in model.
     */
    public void updateUI() {
        // stops the spinner when weather is fetched
        spinnerDialog.cancel();
        // spinnerDialog.cancel();
        cityTextView.setText(weatherActivity.getModel().getCity());

        //if(weatherActivity.getModel().getTemp() > -200) {
            tempTextView.setText(weatherActivity.getModel().getTemp() + " °");
        //}
        //humTextView.setText("Humidity: " + weatherActivity.getModel().getHumidity() + "%");

        // setting pref place heart
        // TODO check if the city is a pref place

        if(getPrefPlace().equals(cityTextView.getText().toString())) {
            ((ImageButton) findViewById(R.id.prefHeart)).setImageDrawable(getDrawable(R.drawable.ic_favorite_grey600_48dp));
        } else {
            ((ImageButton) findViewById(R.id.prefHeart)).setImageDrawable(getDrawable(R.drawable.ic_favorite_outline_grey600_48dp));
        }


        setWeatherIcon();
    }


    /**
     * Sets type of degrees.
     * @param view Radiobutton corresponding to C or F
     */
    public void setDegreeType(View view) {
        //
        if(view == findViewById(R.id.celsiusButton)) {
            System.out.println("Setting degrees to C");
            weatherActivity.getModel().setCelsius(true);
            ((RadioButton)findViewById(R.id.fahrenheitButton)).setChecked(false);
        } else {
            System.out.println("Setting degrees to F");
            weatherActivity.getModel().setCelsius(false);
            ((RadioButton)findViewById(R.id.celsiusButton)).setChecked(false);
        }
    }

    public void setWeatherIcon() {
        iconImageView.setImageResource(weatherActivity.getWeatherIcon());
    }





}
