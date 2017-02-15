package com.halilibo.mde.actions;

public class TextAction extends MarkdownAction {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isValid() {
        return getName()!=null && getText()!=null;
    }
}
