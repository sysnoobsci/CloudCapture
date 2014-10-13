package com.ci.systemware.cloudcapture.ExtendedClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by john.williams on 5/5/2014.
 */
public class GothicTextView extends TextView {
    public GothicTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public GothicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GothicTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            try {
                setTypeface(Typefaces.get(this.getContext(),"GOTHIC.TTF"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
