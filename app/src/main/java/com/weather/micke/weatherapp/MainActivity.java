package com.weather.micke.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
    private AlertDialog.Builder cityDialog;

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

            } /*else if(seconds >= 10) { // if no answer after 10 seconds time out
>>>>>>> develop
                System.out.println("SYSTEM TIME OUT");
                spinnerDialog.cancel();
                //spinnerDialog.cancel();

<<<<<<< HEAD
            } else if(weatherActivity.getModel().getTemp() != -237) {
=======
            } */else if(weatherActivity.getModel().getTemp() != -237) {

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

        // Sets default weather in Miami
        weatherActivity.getModel().setCity("Miami");
        loadWeather(iconImageView);
        searcher.setHint(weatherActivity.getModel().getCity());


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


        /*dialogText = new EditText(this);

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

        cityDialog.show();*/

        initUI();



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
        // spinnerDialog.cancel();
        cityTextView.setText(weatherActivity.getModel().getCity());

        //if(weatherActivity.getModel().getTemp() > -200) {
            tempTextView.setText(weatherActivity.getModel().getTemp() + " °");
        //}
        //humTextView.setText("Humidity: " + weatherActivity.getModel().getHumidity() + "%");

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
