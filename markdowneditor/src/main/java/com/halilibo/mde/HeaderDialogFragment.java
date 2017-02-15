package com.halilibo.mde;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeaderDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeaderDialogFragment extends DialogFragment implements View.OnClickListener {


    private final HeaderDialogCallback callback;

    public HeaderDialogFragment() {
        // Required empty public constructor
        callback=null;
    }

    public HeaderDialogFragment(HeaderDialogCallback callback) {
        // Required empty public constructor
        super();
        this.callback = callback;
    }

    public static HeaderDialogFragment newInstance(HeaderDialogCallback callback) {
        return new HeaderDialogFragment(callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_header_dialog, container, false);

        rootView.findViewById(R.id.header_one).setOnClickListener(this);
        rootView.findViewById(R.id.header_two).setOnClickListener(this);
        rootView.findViewById(R.id.header_three).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.header_one) {
            callback.onHeader(1);
        } else if (i == R.id.header_two) {
            callback.onHeader(2);
        } else if (i == R.id.header_three) {
            callback.onHeader(3);
        }
        dismiss();
    }

    public interface HeaderDialogCallback{
        void onHeader(int level);
    }
}
