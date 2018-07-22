package e.wilso.audiotester.modules;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import e.wilso.audiotester.R;
import e.wilso.audiotester.detection.NoiseModel;
import e.wilso.audiotester.interfaces.DebugView;
import e.wilso.audiotester.recorders.AudioRecorder;

public class AudioViewLayout extends AppCompatActivity implements DebugView/*, View.OnClickListener*/ {

   private static final int msgKey1 = 1;

   Paint paint;
   ArrayList<Double> points = null;
   ArrayList<Double[]> points2 = null;
   //public static AudioView instance = null;
   public static AudioViewLayout instance = null;

   public static float lux = 0;
   private int snore = 0;
   private int active = 0;
   private int i = 0;
   ArrayList<Double> RLH = null;
   ArrayList<Double> VAR = null;
   ArrayList<Double> RMS = null;

   private AudioRecorder recorder;
   private NoiseModel noiseModel;

   private SimpleDateFormat sdf;
   private Date date;

   long AverageTime = 0; // 計算平均處理時間使用
   long StartTime = System.currentTimeMillis(); // 取出目前時間

   private TextView date_v, time_v, RLH_v, VAR_v, RMS_v, snore_v, active_v;
   private Button btn_play, btn_stop;
   private MediaPlayer mediaPlayer;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_audio_view_layout);

      init();

      btn_play.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(AudioViewLayout.this, R.raw.snoring);
            mediaPlayer.start();
         }
      });

      btn_stop.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            mediaPlayer.stop();
            mediaPlayer.reset();
         }
      });

      new TimeThread().start();
   }

   @SuppressLint("SimpleDateFormat")
   private void init() {
      date_v = findViewById(R.id.date_view);
      time_v = findViewById(R.id.time_view);
      RLH_v = findViewById(R.id.rlh_view);
      VAR_v = findViewById(R.id.var_view);
      RMS_v = findViewById(R.id.rms_view);
      snore_v = findViewById(R.id.snore_view);
      active_v = findViewById(R.id.active_view);

      btn_play = findViewById(R.id.btn_play);
      btn_stop = findViewById(R.id.btn_stop);

      points = new ArrayList<>();
      points2 = new ArrayList<>();
      RLH = new ArrayList<>();
      VAR = new ArrayList<>();
      RMS = new ArrayList<>();

      //instance = this;
      instance = this;

      sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      noiseModel = new NoiseModel();

      recorder = new AudioRecorder(noiseModel,this);
      recorder.start();
   }

   @Override
   public void addPoint2(Double x, Double y) {
      if(points2.size() > 10) {
         points2.remove(0);
      }
      Double[] p = new Double[2];
      p[0] = x;
      p[1] = y;
      points2.add(p);
   }

   @Override
   public void setLux(Float lux) {
      AudioViewLayout.lux = lux;
   }

   public void addRMS(Double p) {
      if(RMS.size() > 300) {
         RMS.remove(0);
      }
      RMS.add(p);
   }

   public void addRLH(Double p) {
      if(RLH.size() > 300) {
         RLH.remove(0);
      }
      RLH.add(p);
   }

   public void addVAR(Double p) {
      if(VAR.size() > 300) {
         VAR.remove(0);
      }
      VAR.add(p);
   }

   @Override
   public void invalidate() {

   }

   @Override
   public boolean post(Runnable runnable) {
      return false;
   }

   /*@Override
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.btn_play:
            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(this, R.raw.snoring);
            mediaPlayer.start();
            break;
         case R.id.btn_stop:
            mediaPlayer.stop();
            mediaPlayer.reset();
            break;
      }
   }*/

   public class TimeThread extends Thread {
      @Override
      public void run () {
         do {
            try {
               Thread.sleep(1000);
               Message msg = new Message();
               msg.what = msgKey1;
               mHandler.sendMessage(msg);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         } while(true);
      }
   }

   @SuppressLint("HandlerLeak")
   private Handler mHandler = new Handler() {
      @Override
      public void handleMessage (Message msg) {
         super.handleMessage(msg);
         switch (msg.what) {
            case msgKey1:
               StartTracking();
               break;
            default:
               break;
         }
      }
   };

   private void StartTracking() {
      for(int i = 0; i < points2.size(); i++) {
         Double[] p = points2.get(i);
      }

      if(points2.size() > 0) {
         long sysTime = System.currentTimeMillis();
         long ProcessTime = sysTime - StartTime; // 計算處理時間
         long FinalTime = ProcessTime / 1000;
         CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd hh:mm:ss", sysTime);
         date_v.setText(sysTimeStr);
         time_v.setText(String.valueOf(FinalTime));

         Double[] curr = points2.get(points2.size() - 1);
         RLH_v.setText(String.valueOf(curr[0]));
         VAR_v.setText(String.valueOf(curr[1]));
         RMS_v.setText(String.valueOf(lux));

         if(curr[1] > 1) { // Filter noise
            if(curr[0] > 1.5) {
               snore++;
            }
            else {
               if(lux > 0.5) {
                  active++;
               }
            }
         }
         snore_v.setText(String.valueOf(snore));
         active_v.setText(String.valueOf(active));
         addRLH((curr[0] * 20 + 900));
         addVAR((curr[1] * 20  + 900));
         addRMS((double) (lux * 20 + 900));


      }
      this.i++;
   }

   public void stop() {
      recorder.close();
   }
}

