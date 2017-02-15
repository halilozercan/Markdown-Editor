package com.halilibo.mde;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.EditText;

import com.halilibo.mde.actions.MarkdownAction;

public interface UserMethods {

    void setCallback(MarkdownCallback callback);

    void showPreview();

    void showEditor();

    /**
     * Get the preview image as a Bitmap instance
     * @return: drawn bitmap of preview
     */
    Bitmap getBitmap();

    /**
     * Add a custom action that can be either TextAction or ImageAction
     * @param action
     * @param type
     * @return:
     */
    boolean addAction(MarkdownAction action, @MarkdownEditor.ActionType int type);

    /**
     * Get access to the EditText object inside this view
     * @return
     */
    EditText getEditText();

    /**
     * Add a custom style
     * @param name
     * @param cssFile
     * @param preview
     * @return
     */
    int addStyle(String name, String cssFile, Drawable preview);
}
