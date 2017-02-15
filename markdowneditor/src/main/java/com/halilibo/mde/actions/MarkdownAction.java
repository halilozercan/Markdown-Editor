package com.halilibo.mde.actions;

public abstract class MarkdownAction {

    private MarkdownActionCallback callback;
    private String name;

    MarkdownAction(){
        callback = null;
        name = null;
    }

    public MarkdownActionCallback getCallback() {
        return callback;
    }

    public void setCallback(MarkdownActionCallback callback) {
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract boolean isValid();

    public interface MarkdownActionCallback{
        void onActivate(MarkdownAction action);
    }

    @Override
    public boolean equals(Object o){
        return o instanceof MarkdownAction && ((MarkdownAction) o).getName().equals(this.getName());
    }
}
