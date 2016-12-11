package cn.edu.gdmec.s07150850.mycamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

public class AlbumActivity extends AppCompatActivity {
    private ViewFlipper flipper;
    private Bitmap[] mBgList;
    private long startTime=0;
    private SensorManager sm;
    private SensorEventListener sel;

    public String[] loadAlbum(){
        String pathName=android.os.Environment.getExternalStorageDirectory()
                .getPath()+"/mycamera";
        File file=new File(pathName);
        Vector<Bitmap> fileName=new Vector<Bitmap>();
        if (file.exists()&& file.isDirectory()){
            String[] str=file.list();
            for (String s:str){
                if (new File(pathName+"/"+s).isFile()){
                    fileName.addElement(loadImage(pathName+"/"+s));
                }
            }
            mBgList=fileName.toArray(new Bitmap[]{});
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        flipper= (ViewFlipper) findViewById(R.id.ViewFlipper01);
        loadAlbum();
        if (mBgList==null){
            Toast.makeText(this,"相册无照片",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else {
            for (int i=0;i<=mBgList.length-1;i++){
                flipper.addView(addImage(mBgList[i]),i,
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }

        sm=(SensorManager)this.getSystemService(SENSOR_SERVICE);
        Sensor sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sel=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x=event.values[SensorManager.DATA_X];
                if (x>10&&System.currentTimeMillis()>startTime+1000){
                    startTime=System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,
                            R.anim.push_right_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,
                            R.anim.push_right_out));
                    flipper.showPrevious();
                }else if (x<-10&&System.currentTimeMillis()>startTime+1000){
                    startTime=System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,
                            R.anim.push_left_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,
                            R.anim.push_left_out));
                    flipper.showNext();
            }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sm.registerListener(sel,sensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(sel);
    }

    public Bitmap loadImage(String patName){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        Bitmap bitmap=BitmapFactory.decodeFile(patName,options);
        WindowManager manager=getWindowManager();
        Display display=manager.getDefaultDisplay();
        int screeWidth=display.getWidth();
        options.inSampleSize=options.outWidth/screeWidth;
        options.inJustDecodeBounds=false;
        bitmap=BitmapFactory.decodeFile(patName,options);
       return bitmap;
    }

    private View addImage(Bitmap bitmap){
        ImageView img =new ImageView(this);
        img.setImageBitmap(bitmap);
        return img;
    }
}
