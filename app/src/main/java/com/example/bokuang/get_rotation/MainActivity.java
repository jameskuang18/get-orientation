package com.example.bokuang.get_rotation;
//why it was been transform into .java class
import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.security.cert.LDAPCertStoreParameters;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.example.bokuang.get_rotation.FileSave;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//确认reoprt to Soc的速度
//给定频率，调度是否正常
//存数据

public class MainActivity extends Activity implements  SensorEventListener{

    private TextView orientationView;
    private Timer m_timer;//计时器
    //flag1 flag2，加速度和磁场传感器是否支持FIFO
    int flag1=0;
    int flag2=0;
    StringBuffer magnetometerValue;
    String show_content="方位角为：\r";//也是FileSave传入的数据，存入文本文件
//at last         myTextView.setText(myText+"  Add");
    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];//加速度传感器Value
    private final float[] magnetometerReading = new float[3];//磁场传感器Value

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private float []I=new float[9];//unused旋转矩阵
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button butDump = (Button) this.findViewById(R.id.dump);
        butDump.setText("保存数据");
        butDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileSave.saveFile(show_content);
                System.out.println("save");
            }
        });
        m_timer=new Timer("timer");
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateData();
            }
        },0,5000);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        orientationView=findViewById(R.id.direction_angle);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        //注册传感器事件
        super.onResume();
        //加速度Sensor, 注意磁场Sensor没有FIFO
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
           flag1=accelerometer.getFifoMaxEventCount();
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI );//test for register rate at 5s
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            flag2=accelerometer.getFifoMaxEventCount();
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL,SensorManager.SENSOR_DELAY_UI );//test for register rate at 5s. 查看调用，磁传感器周期是否为5s
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    @Override
    //改变以变化刷新，改成周期刷新
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
        updateOrientationAngles();
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        //return 旋转矩阵rotationMatrix和旋转矩阵I(未使用)
        // cclerometerReading一维3floats的加速度传感器数据，magnetor磁场传感器数据
        boolean flag1=SensorManager.getRotationMatrix(rotationMatrix,I,accelerometerReading,magnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles);
       // angles=String.format("%s\n",Arrays.toString(orientationAngles));
       // show_content+=angles;
      //  orientationView.setText(show_content);
        //myTextView.setText("方向角是： "+Arrays.toString(orientationAngles)+"\r");
        // "mOrientationAngles" now has up-to-date information.
    }
    private void updateData(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StringBuffer angles=new StringBuffer();
                if(orientationAngles==null)
                    return;
                for(int i=0;i<3;++i)
                {
                    angles.append(orientationAngles[i]+" ");
                }
                angles.append("\n");
                System.out.println("这次采集到的angles： "+angles+"\n");
                show_content+=angles;
                orientationView.setText(show_content);
                /*angles=String.format("%s\n",Arrays.toString(orientationAngles));
                show_content+=angles;
                orientationView.setText(show_content);
                */
            }
        });
    }


}
