package com.taxibookingdriver.Font;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 5/16/2017.
 */

@SuppressLint("AppCompatCustomView")
public class TahomaItalic extends TextView {

    private final static String CONDENSED_FONT = "fonts/Tahoma Faux Italic.ttf";

    public TahomaItalic(Context context                                                                                             , AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TahomaItalic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TahomaItalic(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), CONDENSED_FONT);
        setTypeface(tf);
    }


}
