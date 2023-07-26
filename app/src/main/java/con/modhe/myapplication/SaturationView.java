package con.modhe.myapplication;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;


//    https://rangotec.com/archives/811
public class SaturationView {
    private final Paint paint = new Paint();
    private final ColorMatrix cm = new ColorMatrix();
    private SaturationView(){
    }
    private static SaturationView instance;
    public static SaturationView getInstance(){
        synchronized (SaturationView.class) {
            if (instance == null) {
                instance = new SaturationView();
            }
        }
        return instance;
    }
    public void saturationView(View view, float saturation){
        cm.setSaturation(saturation);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }
}