package com.example.sensordaten_sammler;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.sensordaten_sammler.rest.ConnectionRest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;


public class AllSensorsFragment extends Fragment implements SensorEventListener, LocationListener {

    private static final int FINE_LOCATION_PERMISSION_CODE = 1;
    Sensor accelSensor, gyroSensor, magnetoSensor, lightSensor, envTempSensor, pressureSensor, wifiSensor, stepCounterSensor, compassSensor, batterieSensor;
    Switch localSwitch, accelSwitch, gyroSwitch, lightSwitch, magnetoSwitch, envTempSwitch, pressureSwitch, wifiSwitch, stepCounterSwitch, compassSwitch, batterieSwitch;
    Spinner localSpinner, accelSpinner, gyroSpinner, lightSpinner, magnetoSpinner, envTempSpinner, pressureSpinner, wifiSpinner, stepCounterSpinner, compassSpinner, batterieSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allsensors, container, false);
       // Switch GUI init
        accelSwitch = view.findViewById(R.id.switchAccel);
        gyroSwitch = view.findViewById(R.id.switchGyro);
        lightSwitch = view.findViewById(R.id.switchLicht);
        magnetoSwitch = view.findViewById(R.id.switchMagne);
        localSwitch = view.findViewById(R.id.switchLokal);
        envTempSwitch = view.findViewById(R.id.switchUmgebungstemp);
        pressureSwitch = view.findViewById(R.id.switchUmgebungsluftdruck);
        //wifiSwitch = view.findViewById(R.id.switchWifi);
        stepCounterSwitch = view.findViewById(R.id.switchSchrittzaehler);
        compassSwitch = view.findViewById(R.id.switchKompass);
        batterieSwitch = view.findViewById(R.id.switchBatterie);

        // Spinner GUI init
        accelSpinner = view.findViewById(R.id.spinnerAccel);
        gyroSpinner = view.findViewById(R.id.spinnerGyro);
        lightSpinner = view.findViewById(R.id.spinnerLicht);
        magnetoSpinner = view.findViewById(R.id.spinnerMagne);
        localSpinner = view.findViewById(R.id.spinnerLokal);
        envTempSpinner = view.findViewById(R.id.spinnerUmgebungstemp);
        pressureSpinner = view.findViewById(R.id.spinnerUmgebungsluftdruck);
        //wifiSpinner = view.findViewById(R.id.spinnerWifi);
        stepCounterSpinner = view.findViewById(R.id.spinnerSchrittzaehler);
        compassSpinner = view.findViewById(R.id.spinnerKompass);
        batterieSpinner = view.findViewById(R.id.spinnerBatterie);

        saveFile("time,x,y,z"+"\n", true,"GyroFile.csv");
        saveFile("time,x,y,z"+"\n", true,"AccFile.csv");


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Sensoren holen
        accelSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetoSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pressureSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        lightSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        envTempSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        //wifiSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_);
        stepCounterSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        compassSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        batterieSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);





        // ausgrauen der nicht vorhandenen Sensoren
        if (accelSensor == null) {
            accelSwitch.setClickable(false);
            accelSwitch.setEnabled(false);
        }
        if (gyroSensor == null){
            gyroSwitch.setClickable(false);
            gyroSpinner.setEnabled(false);
        }
        if (magnetoSensor == null){
            magnetoSwitch.setClickable(false);
            magnetoSpinner.setEnabled(false);
        }
        if (lightSensor == null){
            lightSwitch.setClickable(false);
            lightSpinner.setEnabled(false);
        }
        if (envTempSensor == null){
            envTempSwitch.setClickable(false);
            envTempSpinner.setEnabled(false);
        }
        if (pressureSensor == null){
            pressureSwitch.setClickable(false);
            pressureSpinner.setEnabled(false);
        }
        if (stepCounterSensor == null){
            stepCounterSwitch.setClickable(false);
            stepCounterSpinner.setEnabled(false);
        }
        if (compassSensor == null){
            compassSwitch.setClickable(false);
            compassSpinner.setEnabled(false);
        }
        if (batterieSensor == null){
            batterieSwitch.setClickable(false);
            batterieSpinner.setEnabled(false);
        }


        //aenderung der sample_frequenz stopt das speichern, damit die aenderung auch aktualisiert wird
        accelSpinner.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                accelSwitch.setChecked(false);
            }
            view1.performClick();
            return true;
        });

        magnetoSpinner.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                magnetoSwitch.setChecked(false);
            }
            view1.performClick();
            return true;
        });

        lightSpinner.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                lightSwitch.setChecked(false);
            }
            view1.performClick();
            return true;
        });

        envTempSpinner.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                envTempSwitch.setChecked(false);
            }
            view1.performClick();
            return true;
        });

        pressureSpinner.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                pressureSwitch.setChecked(false);
            }
            view1.performClick();
            return true;
        });

        stepCounterSpinner.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                stepCounterSwitch.setChecked(false);
            }
            view1.performClick();
            return true;
        });

        batterieSpinner.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                batterieSwitch.setChecked(false);
            }
            view1.performClick();
            return true;
        });

        compassSpinner.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                compassSwitch.setChecked(false);
            }
            view1.performClick();
            return true;
        });



        //startet das speichern beim ausführen des switch indem es die sensoren registriert


        localSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = localSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            }
            if(b){
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestFineLocationPermission();
                } else {
                    MainActivity.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, sensorDelay, 0, this);
                }
            }
            else{
                MainActivity.locationManager.removeUpdates(this);
            }
        });


        accelSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = accelSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[2])) {
                sensorDelay = 25 * 1000;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[3])) {
                sensorDelay = 50 * 1000;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[4])) {
                sensorDelay = 100 * 1000;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, accelSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, accelSensor);
            }
        });

        gyroSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = gyroSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[2])) {
                sensorDelay = 25 * 1000;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[3])) {
                sensorDelay = 50 * 1000;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[4])) {
                sensorDelay = 100 * 1000;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, gyroSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, gyroSensor);
            }
        });

        magnetoSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = magnetoSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[2])) {
                sensorDelay = 25 * 1000;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[3])) {
                sensorDelay = 50 * 1000;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[4])) {
                sensorDelay = 100 * 1000;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, magnetoSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, magnetoSensor);
            }
        });
        lightSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = lightSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, lightSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, lightSensor);
            }
        });
        envTempSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = envTempSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, envTempSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, envTempSensor);
            }
        });
        pressureSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = pressureSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, pressureSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, pressureSensor);
            }
        });
        stepCounterSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = stepCounterSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, stepCounterSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, stepCounterSensor);
            }
        });
        compassSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = compassSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[2])) {
                sensorDelay = 25 * 1000;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[3])) {
                sensorDelay = 50 * 1000;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[4])) {
                sensorDelay = 100 * 1000;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, compassSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, compassSensor);
            }
        });
        batterieSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b)-> {
            String sampleFreq = batterieSpinner.getSelectedItem().toString();
            int sensorDelay = 1000;
            if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[0])) {
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
            } else if (sampleFreq.equals(getResources().getStringArray(R.array.sampling_frequencies)[1])) {
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
            }
            if(b){
                MainActivity.sensorManager.registerListener(this, batterieSensor, sensorDelay);
            }
            else{
                MainActivity.sensorManager.unregisterListener(this, batterieSensor);
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
        JSONObject data = new JSONObject();
        switch(sensorEvent.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
            saveFile(System.currentTimeMillis() + "," + sensorEvent.values[0] + "," + sensorEvent.values[1] + "," + sensorEvent.values[2]+"\n", true, "AccFile.csv");
           /* try {
                data.put("x", sensorEvent.values[0]);
                data.put("y", sensorEvent.values[1]);
                data.put("z", sensorEvent.values[2]);
                data.put("timestamp", System.currentTimeMillis());
                data.put("session_id", Session.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new ConnectionRest().execute("accelerometer", data.toString());
            Log.d("newTEST","accelerometer__"+sensorEvent.values[0]+"-"+sensorEvent.values[1]+"-"+sensorEvent.values[2]);
            break;
            */
        case Sensor.TYPE_MAGNETIC_FIELD:
            try {
                data.put("x", sensorEvent.values[0]);
                data.put("y", sensorEvent.values[1]);
                data.put("z", sensorEvent.values[2]);
                data.put("timestamp", System.currentTimeMillis());
                data.put("session_id", Session.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ConnectionRest().execute("magnetometer", data.toString());
            Log.d("newTEST","compass__"+Math.round(sensorEvent.values[0]));
            break;
        case Sensor.TYPE_GRAVITY:
            try {
                data.put("value", sensorEvent.values[0]);
                data.put("timestamp", System.currentTimeMillis());
                data.put("session_id", Session.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ConnectionRest().execute("schwerkraft", data.toString());
            break;
        case Sensor.TYPE_GYROSCOPE:
            saveFile(System.currentTimeMillis() + "," + sensorEvent.values[0] + "," + sensorEvent.values[1] + "," + sensorEvent.values[2]+"\n", true, "GyroFile.csv");
            /*try {

                data.put("x", sensorEvent.values[0]);
                data.put("y", sensorEvent.values[1]);
                data.put("z", sensorEvent.values[2]);
                data.put("timestamp", System.currentTimeMillis());
                data.put("session_id", Session.getID());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ConnectionRest().execute("gyroskop", data.toString());
            break;
            */
        case Sensor.TYPE_LIGHT:
            try {
                data.put("value", sensorEvent.values[0]);
                data.put("timestamp", System.currentTimeMillis());
                data.put("session_id", Session.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ConnectionRest().execute("licht", data.toString());
            break;
        case Sensor.TYPE_AMBIENT_TEMPERATURE:
            try {
                data.put("value", sensorEvent.values[0]);
                data.put("timestamp", System.currentTimeMillis());
                data.put("session_id", Session.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ConnectionRest().execute("umgebungstemperatur", data.toString());
            break;
        case Sensor.TYPE_PRESSURE:
            try {
                data.put("value", sensorEvent.values[0]);
                data.put("timestamp", System.currentTimeMillis());
                data.put("session_id", Session.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ConnectionRest().execute("umgebungsluftdruck", data.toString());
            break;
        case Sensor.TYPE_STEP_COUNTER:
            try {
                data.put("value", sensorEvent.values[0]);
                data.put("timestamp", System.currentTimeMillis());
                data.put("session_id", Session.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ConnectionRest().execute("schrittzaehler", data.toString());
            break;
        case Sensor.TYPE_ORIENTATION:
            try {
                data.put("value", sensorEvent.values[0]);
                data.put("timestamp", System.currentTimeMillis());
                data.put("session_id", Session.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ConnectionRest().execute("kompass", data.toString());
            break;
        default:
            Log.d("ERROR", "Fehler bei dem Typen:"+sensorEvent.sensor.getType());
            break;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // GPS implementation
    @Override
    public void onLocationChanged(Location newestLocation) {
        if (newestLocation != null) {
            if(newestLocation.getProvider().equalsIgnoreCase("gps")) {
                JSONObject data = new JSONObject();
                try {
                    data.put("latitude", newestLocation.getLatitude());
                    data.put("longitude", newestLocation.getLongitude());
                    data.put("altitude", newestLocation.getAltitude());
                    data.put("timestamp", System.currentTimeMillis());
                    data.put("session_id", Session.getID());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new ConnectionRest().execute("gps", data.toString());
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Erlaubnis für Zugriff auf Standort-Informationen ist nun hiermit erteilt worden", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Erlaubnis für Zugriff auf Standort-Informationen nicht erteilt", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestFineLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(getActivity()).setTitle("Erlaubnis benötigt").setMessage("Zum Anzeigen der GPS-Daten, wird deine Erlaubnis benötigt")
                    .setPositiveButton("ok", (dialog, which)->
                            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_CODE))
                    .setNegativeButton("cancel", (dialog, which)->dialog.dismiss())
                    .create().show();
        } else
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_CODE);
    }
    public void saveFile(String text, boolean append,String fileName)
    {
        FileOutputStream fos = null;

        try {
            if(append)
                fos = getActivity().openFileOutput(fileName,getActivity().MODE_APPEND);
            else
                fos = getActivity().openFileOutput(fileName,getActivity().MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            //Toast.makeText(getActivity(), "Gespeichert!", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
