package com.example.smartmedicationapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    private final int REFRESH_INTERVAL = 5000; // 5 seconds
    MediaPlayer mediaPlayer;

    private ImageView heartImage;
    private ImageView temperatureImage;
    private ImageView medicineImage;
    private ImageView emergencyImage;
    private ImageView boxImage;

    private TextView time;
    private ImageView refresh;
    private TextView temperatureTextView;
    private TextView heartBeatTextView;
    private TextView irSensorTextView;
    private TextView emergencyTextView;
    private TextView boxLevelTextView;
    private int counter = 0;
    private int previousEntryId = 1;

    private static final String URL = "https://api.thingspeak.com/channels/2604017/feeds.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = findViewById(R.id.time);
        refresh = findViewById(R.id.refresh);
        temperatureTextView = findViewById(R.id.temperature_value);
        heartBeatTextView = findViewById(R.id.heart_beat_value);
        irSensorTextView = findViewById(R.id.medicine_status);
        emergencyTextView = findViewById(R.id.emergency_status);
        boxLevelTextView = findViewById(R.id.box_level_status);

        heartImage = findViewById(R.id.heart_image);
        temperatureImage = findViewById(R.id.temperature_image);
        medicineImage = findViewById(R.id.medicine_image);
        emergencyImage = findViewById(R.id.emergency_image);
        boxImage = findViewById(R.id.box_level_image);

        // Initialize Handler and Runnable
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fetchData();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        };

        // Start fetching data
        handler.post(runnable);

        // Set up the refresh button click listener
        refresh.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            fetchData();
        });
    }

    private void fetchData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, response -> {
                    try {
                        JSONObject channel = response.getJSONObject("channel");
                        JSONArray feeds = response.getJSONArray("feeds");

                        // Check if feeds array is empty
                        if (feeds.length() == 0) {
                            Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Get the latest feed
                        JSONObject latestFeed = feeds.getJSONObject(feeds.length() - 1);

                        String field1 = latestFeed.optString("field1", "N/A");
                        String field2 = latestFeed.optString("field2", "N/A");
                        String field3 = latestFeed.optString("field3", "N/A");
                        String field4 = latestFeed.optString("field4", "N/A");
                        String field5 = latestFeed.optString("field5", "N/A");
                        String createdAt = latestFeed.optString("created_at", "N/A");
                        previousEntryId = Integer.parseInt(latestFeed.optString("entry_id"));

                        // Set Time (IST)
                        time.setText("Updated: " + getTime(createdAt));

                        // Temperature
                        temperatureTextView.setText(field1);
                        float tempValue = Float.parseFloat(field1);
                        Log.e("temp", field1);
                        if(tempValue > 98 && counter != previousEntryId) {
                            temperatureImage.setImageResource(R.drawable.red_cross);
                            playTune();
                            NotificationHelper.sendNotification(
                                    getApplicationContext(),
                                    "Temperature Alert",
                                    "Temperature is too high!"
                            );
                            counter = previousEntryId;
                        } else {
                            temperatureImage.setImageResource(R.drawable.temperature);
                        }

                        // Heart Rate
                        heartBeatTextView.setText(field2);
                        int heartRateValue = Integer.parseInt(field2);
                        if((heartRateValue > 100 || heartRateValue < 60) && counter != previousEntryId) {
                            heartImage.setImageResource(R.drawable.red_cross);
                            playTune(); // The app is stopping here
                            String message;
                            if(heartRateValue > 100) message = "Heart Rate is too high!";
                            else message = "Heart Rate is too low!";
                            NotificationHelper.sendNotification(
                                    getApplicationContext(),
                                    "Heart Rate Alert",
                                    message
                            );
                            counter = previousEntryId;
                        } else {
                            heartImage.setImageResource(R.drawable.heart_rate);
                        }

                        // Box Level
                        boxLevelTextView.setText(field3);
                        int boxLevelValue = Integer.parseInt(field3);
                        if(boxLevelValue >= 8 && counter != previousEntryId) {
                            boxLevelTextView.setText("EMPTY");
                            boxImage.setImageResource(R.drawable.red_cross);
                            playTune();
                            NotificationHelper.sendNotification(
                                    getApplicationContext(),
                                    "Pills Box Alert",
                                    "Box is Empty, Kindly fill the medicines!"
                            );
                            counter = previousEntryId;
                        } else {
                            boxImage.setImageResource(R.drawable.medicine_box);
                        }

                        // Emergency Alert
                        int emergencyValue = Integer.parseInt(field4);
                        if(emergencyValue < 1 && counter != previousEntryId) {
                            emergencyTextView.setText("EMERGENCY");
                            emergencyImage.setImageResource(R.drawable.red_cross);
                            playTune();
                            NotificationHelper.sendNotification(
                                    getApplicationContext(),
                                    "Emergency Alert",
                                    "There is an emergency, Patient needs help!"
                            );
                            counter = previousEntryId;
                        } else {
                            emergencyImage.setImageResource(R.drawable.emergency_call);
                        }

                        // Pills taken/ Not taken
                        irSensorTextView.setText(field5);
                        int pillValue = Integer.parseInt(field5);
                        if(pillValue > 0 && counter != previousEntryId) {
                            irSensorTextView.setText("NOT TAKEN");
                            medicineImage.setImageResource(R.drawable.red_cross);
                            NotificationHelper.sendNotification(
                                    getApplicationContext(),
                                    "Pills Alert",
                                    "Patient has not taken medicines!"
                            );
                            counter = previousEntryId;
                        } else {
                            medicineImage.setImageResource(R.drawable.pills);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_LONG).show();
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void playTune() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null; // Null out the mediaPlayer reference
            }

            // Ensure the audio file exists and is correct
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);

            if (mediaPlayer != null) {
                mediaPlayer.start();
            } else {
                Log.e("MediaPlayer", "Failed to create MediaPlayer instance. Check if the audio file exists and is correct.");
            }
        } catch (Exception e) {
            Log.e("MediaPlayer", "Error initializing MediaPlayer", e);
        }
    }

    private String getTime(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = inputFormat.parse(timestamp);

            if (date != null) {
                String formattedTime = timeFormat.format(date);
                String formattedDate = dateFormat.format(date);
                return formattedTime + " | " + formattedDate;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}
