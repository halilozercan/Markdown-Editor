package com.halilibo.mde;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.EditText;

import com.halilibo.mde.actions.MarkdownAction;

/**
 * Created by halo on 10/1/16.
 */

public interface UserMethods {

    public void setCallback(MarkdownCallback callback);

    public void showPreview();

    public void showEditor();

    public Uri saveHugeTweet();

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent);

    public void addAction(MarkdownAction action);

    public EditText getEditText();

    public int addStyle(String name, String cssFile, Drawable preview);
}
