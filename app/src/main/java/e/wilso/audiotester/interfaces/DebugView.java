package e.wilso.audiotester.interfaces;

/**
 * Created by paulmohr on 08.06.15.
 */
public interface DebugView {
   void addPoint2(Double x, Double y);
   void setLux(Float lux);
   void invalidate();
   boolean post(Runnable runnable);
}
