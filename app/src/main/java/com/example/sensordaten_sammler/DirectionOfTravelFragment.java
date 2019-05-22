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
import android.view.Gravity;
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
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class DirectionOfTravelFragment extends Fragment implements SensorEventListener {

    Button startStopBtnAcc;
    TextView directionOfTravelTV, getDirectionOfTravelDegreeTV;
    Sensor accelSensor, gyroSensor;
    int sensorDelay;
    int sampleCount;
    int sensorEventIndicatingLeftTurn = 0, sensorEventIndicatingRightTurn = 0, sensorEventIndicatingStraightDir = 0;
    Queue<Float> messdatenZGyro;
    double degreeV;
    long lastTime;
    boolean changeRL = false;

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
        getDirectionOfTravelDegreeTV = getActivity().findViewById(R.id.dirofTravelDegree);
        //Sensoren holen
        accelSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        messdatenZGyro = new LinkedList<>();
        sampleCount = 0;
        degreeV = 0;
        lastTime = System.currentTimeMillis();

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

//    public static int max(int first, int... rest) {
//        int ret = first;
//        for (int val : rest) {
//            ret = Math.max(ret, val);
//        }
//        return ret;
//    }

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
                messdatenZGyro.add(sensorEvent.values[2]);
                long currentTime = System.currentTimeMillis();
                degreeV = degreeV + (sensorEvent.values[2]*(currentTime-lastTime)/1000*57.2958);
                lastTime = System.currentTimeMillis();
                if(messdatenZGyro.size() > 3){
                    for(Float f : messdatenZGyro){
                        if(sensorEvent.values[2] > 0.4) {
                            sensorEventIndicatingLeftTurn++;
                        }
                        else if(sensorEvent.values[2] < -0.4){
                            sensorEventIndicatingRightTurn++;
                        }
                        else{
                            sensorEventIndicatingStraightDir++;
                        }
                    }

                    if(sensorEventIndicatingLeftTurn > sensorEventIndicatingRightTurn && sensorEventIndicatingLeftTurn > sensorEventIndicatingStraightDir){
                        directionOfTravelTV.setText("Links");
                        getDirectionOfTravelDegreeTV.setText(degreeV+"°");
                        changeRL = true;

                    }
                    else if(sensorEventIndicatingRightTurn > sensorEventIndicatingLeftTurn && sensorEventIndicatingRightTurn > sensorEventIndicatingStraightDir){
                        directionOfTravelTV.setText("Rechts");
                        getDirectionOfTravelDegreeTV.setText(Math.abs(degreeV)+"°");
                        changeRL = true;
                    }
                    else{
                        directionOfTravelTV.setText("Geradeaus");
                        if(changeRL) {
                            addTableRow();
                            changeRL = false;
                        }
                        degreeV = 0;
                    }
                    sensorEventIndicatingLeftTurn = 0;
                    sensorEventIndicatingRightTurn = 0;
                    sensorEventIndicatingStraightDir = 0;
                    messdatenZGyro.remove();
                }
//                if(sensorEvent.values[2] > 0.4)
//                    directionOfTravelTV.setText("Links");
//                else if(sensorEvent.values[2] < -0.4)
//                    directionOfTravelTV.setText("Rechts");
//                else
//                    directionOfTravelTV.setText("Geradeaus");
                break;
            default:
                Log.d("ERROR", "Fehler bei dem Typen:"+sensorEvent.sensor.getType());
                break;
        }
    }
    private void addTableRow(){
        TableRow row = new TableRow(getContext());
        TextView textNr = new TextView(getContext()), textDegree = new TextView(getContext());

        textDegree.setTextSize(12);
        textDegree.setGravity(Gravity.START);
        textDegree.setPadding(2,2,2,2);
        textDegree.setText(((double) Math.round(Math.abs(degreeV*100))/100)+"°");

        textNr.setTextSize(12);
        textNr.setGravity(Gravity.START);
        textNr.setPadding(2,2,2,2);
        textNr.setText("Nr.:"+(((TableLayout) getActivity().findViewById(R.id.tableDegree)).getChildCount()-3)+ "  |  ");

        row.addView(textNr);
        row.addView(textDegree);
        ((TableLayout) getActivity().findViewById(R.id.tableDegree)).addView(row, 3);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
