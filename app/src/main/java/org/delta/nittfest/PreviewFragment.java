package org.delta.nittfest;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class PreviewFragment extends Fragment {
    View view;
    OnClickListener listener;
    int times = 0;

    public PreviewFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PreviewFragment.OnClickListener)
            listener = (OnClickListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_preview, container, false);
        handleButtonClicks();
        return view;
    }


    public void handleButtonClicks() {
        final ImageView imageView = (ImageView) view.findViewById(R.id.popupimage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (times % 2 == 0) {
                    imageView.setImageResource(R.drawable.app_front);
                } else imageView.setImageResource(R.drawable.app_back);
                times++;

            }
        });
        Button button = (Button) view.findViewById(R.id.continue_button);
        button.setTypeface(Utilities.typefaceR);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.PreviewToGender();
            }
        });
    }

    public interface OnClickListener {
        public void PreviewToGender();
    }
}
