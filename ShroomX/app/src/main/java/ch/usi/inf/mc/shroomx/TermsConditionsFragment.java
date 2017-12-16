package ch.usi.inf.mc.shroomx;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 *
 */

public class TermsConditionsFragment extends Fragment implements SensorEventListener {
    private static final String TAG = "TermsConditionsActivity";

    private TextView mShakeToAgree;

    //private Animation anim;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.activity_terms, container, false);

        mShakeToAgree = (TextView) v.findViewById(R.id.shake_to_agree);

        final Animation anim = AnimationUtils.loadAnimation(container.getContext(),R.anim.rotate);
        anim.setStartOffset(2000);
        anim.setRepeatCount(Animation.INFINITE);

        senSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);


        return v;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChange(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

        }
    }
}
