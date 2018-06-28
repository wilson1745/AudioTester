package e.wilso.audiotester.recorders;

import android.content.Context;
import android.media.AudioRecord;

import java.util.List;

import e.wilso.audiotester.detection.FeatureExtractor;
import e.wilso.audiotester.detection.NoiseModel;
import e.wilso.audiotester.interfaces.DebugView;
import io.github.privacystreams.audio.Audio;
import io.github.privacystreams.audio.AudioOperators;
import io.github.privacystreams.core.Callback;
import io.github.privacystreams.core.UQI;
import io.github.privacystreams.core.purposes.Purpose;

public class AudioRecorder extends Thread {

   private boolean stopped = false;
   private static AudioRecord recorder = null;
   private static int N = 0;
   private NoiseModel noiseModel;
   private DebugView debugView;
   private short[] buffer;
   private FeatureExtractor featureExtractor;
   private Context mContext;
   private long durationPerRecord = 100;
   long interval = 0;

   public AudioRecorder(Context mContext, NoiseModel noiseModel, DebugView debugView) {
      this.noiseModel = noiseModel;
      this.debugView = debugView;
      this.featureExtractor = new FeatureExtractor(noiseModel);
      this.mContext = mContext;
   }

   @Override
   public void run() {
      capture(mContext);
   }

   private void capture(Context context) {
      //聲音線程的最高級別，優先程度較THREAD_PRIORITY_AUDIO要高。代碼中無法設置為該優先級。值為-19
      android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
      //all types of personal data can be accessed and processed with a UQI
      UQI uqi = new UQI(context);

      uqi.getData(Audio.recordPeriodic(durationPerRecord, interval), Purpose.HEALTH("Sleep monitoring"))
              .setField("amp", AudioOperators.getAmplitudeSamples(Audio.AUDIO_DATA))
              .forEach("amp", new Callback<List<Integer>>() {
                 @Override
                 protected void onInput(List<Integer> input) {
                    short shortArray[] = new short[input.size()];
                    for (int i = 0; i < shortArray.length; ++i) {
                       shortArray[i] = input.get(i).shortValue();
                    }
                    process(shortArray);
                 }
              });
   }

   private void process(short[] buffer) {
      featureExtractor.update(buffer);

      if (debugView != null) {
         /*debugView.addPoint2(noiseModel.getNormalizedRLH(), noiseModel.getNormalizedVAR());
         debugView.setLux((float) (noiseModel.getNormalizedRMS()));*/
         debugView.addPoint2(noiseModel.getLastRLH(), noiseModel.getNormalizedVAR());
         debugView.setLux((float) (noiseModel.getLastRMS()));
         debugView.post(new Runnable() {
            @Override
            public void run() {
               debugView.invalidate();
            }
         });
      }
   }

   public void close() {
      stopped = true;
   }
}