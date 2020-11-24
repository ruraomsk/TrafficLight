package ru.list.ruraomsk.trafficlight;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceControl extends ViewController {
    List<TextView> views = new ArrayList<>();
    List<Button> buttons = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_device_control, container, false);
        for (int i = 1; i < 12; i++) {
            if(i==9) continue;
            String tag="@+id/faza"+String.valueOf(i);
            buttons.add(v.findViewWithTag(tag));
            tag+="l";
            views.add(v.findViewWithTag(tag));
        }
        return v;
    }

    @Override
    public void View() {
        super.View();
    }

    @Override
    public void onStart() {
        Common.RegitrationFragment("control",this);
        super.onStart();
    }

    @Override
    public void onStop() {
        Common.UnRegitrationFragment("control");
        super.onStop();
    }
}