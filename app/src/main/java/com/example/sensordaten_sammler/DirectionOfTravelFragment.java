package com.example.sensordaten_sammler;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensordaten_sammler.rest.ConnectionRest;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DirectionOfTravelFragment extends Fragment implements SensorEventListener {

    Button startStopBtnAcc;
    TextView directionOfTravelTV;
    Sensor accelSensor, gyroSensor;
    int sensorDelay;

    Timer timer = new Timer();
//    private static final String fileName = "ACCFile.csv";
    List<Double> accAbsolutesList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dir_of_travel, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        directionOfTravelTV = getActivity().findViewById(R.id.dirOfTravel);
        //Sensoren holen
        accelSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //startet das speichern beim ausführen des switch indem es die sensoren registriert
        sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
        MainActivity.sensorManager.registerListener(this, accelSensor, sensorDelay);
        MainActivity.sensorManager.registerListener(this, gyroSensor, sensorDelay);


    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.sensorManager.registerListener(this, accelSensor, sensorDelay);
        MainActivity.sensorManager.registerListener(this, gyroSensor, sensorDelay);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.sensorManager.unregisterListener(this, accelSensor);
        MainActivity.sensorManager.unregisterListener(this, gyroSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch(sensorEvent.sensor.getType()) {
//            case Sensor.TYPE_ACCELEROMETER:
//                directionOfTravelTV.setText("Links");
//                directionOfTravelTV.setText("Rechts");
//                directionOfTravelTV.setText("Geradeaus");
//                break;
            case Sensor.TYPE_GYROSCOPE:
                Log.d("newTEST","gyro: "+sensorEvent.values[0]+"-"+sensorEvent.values[1]+"-"+sensorEvent.values[2]);
                if(sensorEvent.values[2] > 0.4)
                    directionOfTravelTV.setText("Links");
                else if(sensorEvent.values[2] < -0.4)
                    directionOfTravelTV.setText("Rechts");
                else
                    directionOfTravelTV.setText("Geradeaus");
                break;
            default:
                Log.d("ERROR", "Fehler bei dem Typen:"+sensorEvent.sensor.getType());
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
