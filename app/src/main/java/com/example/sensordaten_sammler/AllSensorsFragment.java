package com.example.sensordaten_sammler;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class AllSensorsFragment extends Fragment {

    Sensor accelerometerSensor, gyroskopSensor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allsensors, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Sensoren holen
        accelerometerSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroskopSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(accelerometerSensor == null)
            Toast.makeText(getActivity(), "Dein Gerät besitzt kein Accelerometer!", Toast.LENGTH_SHORT).show();
        if(gyroskopSensor == null)
            Toast.makeText(getActivity(), "Dein Gerät besitzt kein Gyroskop!", Toast.LENGTH_SHORT).show();

    }

    private class Accelerometer implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

}
