package com.example.stopwatch;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button startButton, pauseButton, resetButton, lapButton;
    private ListView lapListView;

    private long startTime, elapsedTime, pausedTime;
    private boolean isRunning;
    private Handler handler;
    private LapListAdapter lapListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        lapButton = findViewById(R.id.lapButton);
        lapListView = findViewById(R.id.lapListView);

        handler = new Handler();
        lapListAdapter = new LapListAdapter(this, new ArrayList<String>());
        lapListView.setAdapter(lapListAdapter);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopwatch();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseStopwatch();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetStopwatch();
            }
        });

        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lapStopwatch();
            }
        });
    }

    private void startStopwatch() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            handler.postDelayed(updateTimer, 0);
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            resetButton.setEnabled(true);
            lapButton.setEnabled(true);
            isRunning = true;
        }
    }

    private void pauseStopwatch() {
        if (isRunning) {
            handler.removeCallbacks(updateTimer);
            pausedTime = System.currentTimeMillis();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            isRunning = false;
        }
    }

    private void resetStopwatch() {
        elapsedTime = 0;
        lapListAdapter.clear();
        updateTimerText();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
        lapButton.setEnabled(false);
        isRunning = false;
    }

    private void lapStopwatch() {
        if (isRunning) {
            long lapTime = System.currentTimeMillis() - startTime;
            lapListAdapter.insert(formatTime(lapTime), 0);
        }
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimerText();
            handler.postDelayed(this, 100);
        }
    };

    private void updateTimerText() {
        timerTextView.setText(formatTime(elapsedTime));
    }

    private String formatTime(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        int milliseconds = (int) (timeInMillis % 1000);

        return String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
    }

    private class LapListAdapter extends ArrayAdapter<String> {

        public LapListAdapter(Context context, List<String> lapList) {
            super(context, 0, lapList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String lapTime = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, parent, false);
            }

            TextView lapTextView = convertView.findViewById(R.id.lapListView);
            lapTextView.setText(lapTime);

            return convertView;
        }
    }
}
