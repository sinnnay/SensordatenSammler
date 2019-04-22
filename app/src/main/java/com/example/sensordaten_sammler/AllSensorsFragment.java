package com.example.sensordaten_sammler;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.sensordaten_sammler.rest.ConnectionRest;

import org.json.JSONException;
import org.json.JSONObject;

public class AllSensorsFragment extends Fragment implements SensorEventListener {

    Sensor accelerometerSensor, gyroskopSensor, gravitySensor, magnetometerSensor, pressureSensor, humiditySensor;
    Switch accelSwitch, gyroSwitch, lightSwitch, gravitySwitch, magnetoSwitch, pressureSwitch, humiditySwitch;
    Spinner accelSpinner, gyroSpinner, lightSpinner, gravitySpinner, magnetoSpinner, pressureSpinner, humiditySpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allsensors, container, false);
        accelSwitch = view.findViewById(R.id.switchAccel);
        gyroSwitch = view.findViewById(R.id.switchGyro);
        lightSwitch = view.findViewById(R.id.switchLicht);
        magnetoSwitch = view.findViewById(R.id.switchMagne);

        accelSpinner = view.findViewById(R.id.spinnerAccel);
        gyroSpinner = view.findViewById(R.id.spinnerGyro);
        lightSpinner = view.findViewById(R.id.spinnerLicht);
        magnetoSpinner = view.findViewById(R.id.spinnerMagne);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Sensoren holen
        accelerometerSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroskopSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gravitySensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        magnetometerSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pressureSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        humiditySensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        if (accelerometerSensor == null) {
            accelSwitch.setClickable(false);
            accelSwitch.setEnabled(false);
        }
        if (gyroskopSensor == null){
            gyroSwitch.setClickable(false);
            gyroSpinner.setEnabled(false);
        }

        accelSpinner.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                accelSwitch.setChecked(false);
            }
            view1.performClick();
            return true;
        });

        accelSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = accelSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, accelerometerSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, accelerometerSensor);
            }
        });
        magnetoSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = magnetoSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, magnetometerSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, magnetometerSensor);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        switch(sensorEvent.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
            JSONObject data = new JSONObject();
            try {
                data.put("x", sensorEvent.values[0]);
                data.put("y", sensorEvent.values[1]);
                data.put("z", sensorEvent.values[2]);
                data.put("session_id", Session.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ConnectionRest().execute("accelerometer", data.toString());
            Log.d("newTEST","accelerometer__"+sensorEvent.values[0]+"-"+sensorEvent.values[1]+"-"+sensorEvent.values[2]);
            break;
        case Sensor.TYPE_MAGNETIC_FIELD:
            Log.d("newTEST","compass__"+Math.round(sensorEvent.values[0]));
            break;
        case Sensor.TYPE_GRAVITY:
            break;
        default:
            break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
