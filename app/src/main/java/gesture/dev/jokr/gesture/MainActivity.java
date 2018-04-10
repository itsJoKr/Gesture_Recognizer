package gesture.dev.jokr.gesture;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import gesture.dev.jokr.gesture.dtw.FastDTW;
import gesture.dev.jokr.gesture.dtw.TimeWarpInfo;
import gesture.dev.jokr.gesture.timeseries.TimeSeries;
import gesture.dev.jokr.gesture.timeseries.TimeSeriesPoint;
import gesture.dev.jokr.gesture.utils.DistanceFunction;
import gesture.dev.jokr.gesture.utils.DistanceFunctionFactory;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView txtFirstPass;
    private TextView txtOutput;
    private Button btnStart;
    private Button btnEnd;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean firstPass = true;

    private TimeSeries timeSeries;
    private TimeSeries secondTimeSeries;

    private long startTimestamp;
    private double firstPassTimeInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        txtFirstPass = findViewById(R.id.txt_first_pass);
        txtOutput = findViewById(R.id.txtOutput);
        btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGesture();
            }
        });
        btnEnd = findViewById(R.id.btn_stop);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endGesture();
            }
        });

        if (mSensorManager != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            Toast.makeText(this, "Cheese is broken", Toast.LENGTH_SHORT).show();
        }

    }

    private void startGesture() {
        if (firstPass) {
            timeSeries = new TimeSeries(3);
            secondTimeSeries = new TimeSeries(3);
        }

        btnStart.setEnabled(false);
        btnEnd.setEnabled(true);
        Date d = new Date();
        startTimestamp = d.getTime();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void endGesture() {
        mSensorManager.unregisterListener(this);
        btnStart.setEnabled(true);
        btnEnd.setEnabled(false);

        if (firstPass) {
            txtFirstPass.setText("First pass done with " + timeSeries.numOfPts() + " points");
            firstPass = false;
            long endTimestamp = new Date().getTime();
            firstPassTimeInterval = (endTimestamp-startTimestamp)/1000.0F;
        } else {
            DistanceFunction distFunc = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
            TimeWarpInfo info = FastDTW.getWarpInfoBetween(timeSeries, secondTimeSeries, 10, distFunc);

            long endTimestamp = new Date().getTime();
            double secondsInterval = (endTimestamp-startTimestamp)/1000.0F;
            double avgSecondsInterval = (secondsInterval + firstPassTimeInterval) / 2;
            String seconds = String.format("%.2f", avgSecondsInterval);
            double distancePerSecond = info.getDistance() / avgSecondsInterval;
            String match = (distancePerSecond < 390) ? "✔" : "✗";

            firstPass = true;
            String output = txtOutput.getText().toString();
            output = output +  " m:" + match + " d: " + info.getDistance() + " - t:" + seconds +" \n ";
            txtOutput.setText(output);

            timeSeries.clear();
            secondTimeSeries.clear();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float xValue = sensorEvent.values[0];
        float yValue = sensorEvent.values[1];
        float zValue = sensorEvent.values[2];

        double[] values = {xValue, yValue, zValue};
        long time = sensorEvent.timestamp - startTimestamp;

        if (firstPass) {
            timeSeries.addLast(time, new TimeSeriesPoint(values));
        } else {
            secondTimeSeries.addLast(time, new TimeSeriesPoint(values));
        }

        Log.d("MainActivity", xValue + " " + yValue + " " + zValue);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
