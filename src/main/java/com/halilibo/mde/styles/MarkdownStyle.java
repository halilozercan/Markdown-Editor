package com.halilibo.mde.styles;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by halo on 10/1/16.
 */

public class MarkdownStyle implements Serializable{

    final private String name;
    final private String address;
    final private Drawable preview;

    public MarkdownStyle(String name, String address, Drawable preview) {
        this.name = name;
        this.address = address;
        this.preview = preview;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof MarkdownStyle){
            if(((MarkdownStyle) o).getName().equals(this.name))
                return true;
        }
        return false;
    }

    public Drawable getPreview() {
        return preview;
    }
}
