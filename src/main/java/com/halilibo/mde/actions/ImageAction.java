package com.halilibo.mde.actions;

import android.graphics.drawable.Drawable;

/**
 * Created by halo on 10/2/16.
 */

public class ImageAction extends MarkdownAction {

    private Drawable drawable;

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public boolean isValid() {
        return getName()!=null && getDrawable()!=null;
    }
}
