package customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextViewDescription extends TextView {

    public MyTextViewDescription(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewDescription(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init();
    }

    public MyTextViewDescription(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Light.ttf");
            setTypeface(tf);
        }
    }

}