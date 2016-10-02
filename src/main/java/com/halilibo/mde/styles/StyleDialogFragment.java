package com.halilibo.mde.styles;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.halilibo.mde.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StyleDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StyleDialogFragment extends DialogFragment{


    private StyleDialogCallback callback;
    private ArrayList<MarkdownStyle> mStyles;

    public StyleDialogFragment() {
        // Required empty public constructor
        callback=null;
        mStyles = null;
    }

    public static StyleDialogFragment newInstance(StyleDialogCallback callback,
                                                  ArrayList<MarkdownStyle> styles) {
        StyleDialogFragment fragment = new StyleDialogFragment();
        fragment.setCallback(callback);
        fragment.setStyles(styles);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_style_dialog, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        StyleAdapter adapter = new StyleAdapter(getActivity());
        adapter.setData(mStyles);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                callback.onStyle(position);
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public void setCallback(StyleDialogCallback callback) {
        this.callback = callback;
    }

    public void setStyles(ArrayList<MarkdownStyle> styles) {
        this.mStyles = styles;
    }

    public interface StyleDialogCallback{
        public void onStyle(int choice);
    }

    public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.ViewHolder> {

        private final Context context;
        private List<MarkdownStyle> data;
        private OnItemClickListener listener;

        public StyleAdapter(Context con){
            this.context = con;
        }

        public void setData(List<MarkdownStyle> data){
            this.data = new ArrayList<>();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        public List<MarkdownStyle> getData() {
            return data;
        }

        public MarkdownStyle getItem(int position) {
            return data.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public View rootView;
            public TextView textView;
            public ImageView imageView;
            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                super(itemView);
                this.rootView = itemView;
                this.textView = (TextView) itemView.findViewById(R.id.textView);
                this.imageView = (ImageView) itemView.findViewById(R.id.imageView);

                this.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onClick(getAdapterPosition());
                    }
                });
            }
        }

        @Override
        public StyleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the custom layout
            View itemView;

            itemView = LayoutInflater.from(context).
                    inflate(R.layout.item_view_style, parent, false);

            // Return a new holder instance
            return new StyleAdapter.ViewHolder(itemView);
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(final StyleAdapter.ViewHolder holder, int position) {

            final MarkdownStyle style = getItem(position);

            holder.textView.setText(style.getName());
            holder.imageView.setImageDrawable(style.getPreview());

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

    public interface OnItemClickListener {
        public void onClick(int position);
    }

}
