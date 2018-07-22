package e.wilso.audiotester.modules;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import e.wilso.audiotester.detection.NoiseModel;
import e.wilso.audiotester.interfaces.DebugView;
import e.wilso.audiotester.recorders.AudioRecorder;

public class AudioView extends View implements DebugView {

   Paint paint;
   ArrayList<Double> points = null;
   ArrayList<Double[]> points2 = null;
   public static AudioView instance = null;
   public static float lux = 0;
   private int snore = 0;
   private int move = 0;
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

   public AudioView(Context context) {
      super(context);
      init();
   }

   public AudioView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public AudioView(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
      init();
   }

   @SuppressLint("SimpleDateFormat")
   private void init(){
      paint = new Paint();
      paint.setColor(Color.BLACK);
      paint.setStrokeWidth(1);
      paint.setStyle(Paint.Style.STROKE);
      paint.setStyle(Paint.Style.FILL);
      paint.setTextSize(80);

      points = new ArrayList<>();
      points2 = new ArrayList<>();
      RLH = new ArrayList<>();
      VAR = new ArrayList<>();
      RMS = new ArrayList<>();

      instance = this;

      sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      noiseModel = new NoiseModel();

      recorder = new AudioRecorder(noiseModel,this);
      recorder.start();
   }

   public void addPoint(Double point) {
      if(points.size() > 10) {
         points.remove(0);
      }
      points.add(point);
   }

   public void addPoint2(Double x, Double y) {
      if(points2.size() > 10) {
         points2.remove(0);
      }
      Double[] p = new Double[2];
      p[0] = x;
      p[1] = y;
      points2.add(p);
   }

   public void setLux(Float lux) {
      AudioView.lux = lux;
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
   protected void onDraw(Canvas canvas) {
      // 設置背景顏色
      setBackgroundColor(Color.BLACK);
      super.onDraw(canvas);

      for(int i = 0; i < points2.size(); i++) {
         Double[] p = points2.get(i);
         canvas.drawCircle((float)(500 + p[0]),(float)(500 + p[1]), 2, paint);
      }

      if(points2.size() > 0) {
         long ProcessTime = System.currentTimeMillis() - StartTime; // 計算處理時間
         //AverageTime += ProcessTime; // 累積計算時間
         long FinalTime = ProcessTime / 1000;

         date = new Date();
         paint.setColor(Color.GREEN);
         canvas.drawText("Date: " + sdf.format(date), 100f, 100f, paint);
         paint.setColor(Color.MAGENTA);
         canvas.drawText("Time: " + FinalTime, 100f, 200f, paint);

         Double[] curr = points2.get(points2.size() - 1);
         paint.setColor(Color.RED);
         canvas.drawText("RLH: " + curr[0], 100f, 300f, paint);
         paint.setColor(Color.YELLOW);
         canvas.drawText("VAR: " + curr[1], 100f, 400f, paint);
         paint.setColor(Color.BLUE);
         canvas.drawText("RMS: " + lux, 100f, 500f, paint);
         if(curr[1] > 1) { // Filter noise
            if(curr[0] > 2) {
               snore++;
            }
            else {
               if(lux > 0.5) {
                  move++;
               }
            }
         }
         paint.setColor(Color.WHITE);
         canvas.drawText("Snore: " + snore, 100f, 600f, paint);
         canvas.drawText("Active: " + move, 100f, 700f, paint);
         addRLH((curr[0] * 20 + 900));
         addVAR((curr[1] * 20  + 900));
         addRMS((double) (lux * 20 + 900));
         drawPoints(canvas);
      }
      this.i++;
   }

   protected void drawPoints(Canvas canvas) {
      for(int i = 0; i < RMS.size(); i++) {
         Paint paint = new Paint();
         paint.setColor(Color.BLUE);
         paint.setStrokeWidth(1);
         paint.setStyle(Paint.Style.STROKE);
         paint.setStyle(Paint.Style.FILL);
         paint.setTextSize(80);
         Double p = RMS.get(i);
         canvas.drawCircle(i * 4, p.floatValue(), 8, paint);
      }
      for(int i = 0; i < VAR.size(); i++) {
         Paint paint = new Paint();
         paint.setColor(Color.YELLOW);
         paint.setStrokeWidth(1);
         paint.setStyle(Paint.Style.STROKE);
         paint.setStyle(Paint.Style.FILL);
         paint.setTextSize(80);
         Double p = VAR.get(i);
         canvas.drawCircle(i * 4,p.floatValue(), 8, paint);
      }
      for(int i = 0;i < RLH.size(); i++) {
         Paint paint = new Paint();
         paint.setColor(Color.RED);
         paint.setStrokeWidth(1);
         paint.setStyle(Paint.Style.STROKE);
         paint.setStyle(Paint.Style.FILL);
         paint.setTextSize(80);
         Double p = RLH.get(i);
         canvas.drawCircle(i*4,p.floatValue(), 8, paint);
      }
   }

   public void stop() {
      recorder.close();
   }
}
