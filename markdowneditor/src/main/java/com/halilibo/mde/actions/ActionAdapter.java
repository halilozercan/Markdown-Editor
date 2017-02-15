package com.halilibo.mde.actions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.halilibo.mde.R;

import java.util.ArrayList;
import java.util.List;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {

    private static final int IMAGE_VIEW_TYPE = 0x0;
    private static final int TEXT_VIEW_TYPE = 0x1;
    private final Context context;
    private List<MarkdownAction> data;
    private int themeColor;

    public ActionAdapter(Context con){
        this.context = con;
    }

    public void setData(List<MarkdownAction> data){
        this.data = new ArrayList<>();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public List<MarkdownAction> getData() {
        return data;
    }

    private MarkdownAction getItem(int position) {
        return data.get(position);
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        View rootView;
        Button textButton;
        ImageButton imageButton;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            try {
                this.textButton = (Button) itemView.findViewById(R.id.text_button);
                this.imageButton = (ImageButton) itemView.findViewById(R.id.image_button);
            }catch (Exception ignored){}
        }
    }

    @Override
    public int getItemViewType(int position){
        if(data.get(position) instanceof ImageAction)
            return IMAGE_VIEW_TYPE;
        else if(data.get(position) instanceof TextAction)
            return TEXT_VIEW_TYPE;

        return IMAGE_VIEW_TYPE;
    }

    @Override
    public ActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = null;

        if(viewType == IMAGE_VIEW_TYPE) {
            itemView = LayoutInflater.from(context).
                    inflate(R.layout.image_action_button_layout, parent, false);
        }
        else if(viewType == TEXT_VIEW_TYPE) {
            itemView = LayoutInflater.from(context).
                    inflate(R.layout.text_action_button_layout, parent, false);
        }

        // Return a new holder instance
        return new ActionAdapter.ViewHolder(itemView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ActionAdapter.ViewHolder holder, final int position) {

        final MarkdownAction action = getItem(position);

        if(getItemViewType(position) == IMAGE_VIEW_TYPE) {
            holder.imageButton.setImageDrawable(((ImageAction)action).getDrawable());
            holder.imageButton.setBackgroundColor(themeColor);
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(action.getCallback()!=null) {
                        action.getCallback().onActivate(action);
                    }
                }
            });
        }
        else if(getItemViewType(position) == TEXT_VIEW_TYPE){
            holder.textButton.setText(((TextAction)action).getText());
            holder.textButton.setBackgroundColor(themeColor);
            holder.textButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(action.getCallback()!=null) {
                        action.getCallback().onActivate(action);
                    }
                }
            });
        }

    }

    // Return the total count of items
    @Override
    public int getItemCount() {

        if(data == null){
            return 0;
        }

        return data.size();
    }
}