package ru.list.ruraomsk.trafficlight;

import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceControl extends ViewController implements View.OnClickListener {
    List<TextView> views = new ArrayList<>();
    List<Button> buttons = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_device_control, container, false);
        buttons.add(v.findViewById(R.id.faza1));
        buttons.add(v.findViewById(R.id.faza2));
        buttons.add(v.findViewById(R.id.faza3));
        buttons.add(v.findViewById(R.id.faza4));
        buttons.add(v.findViewById(R.id.faza5));
        buttons.add(v.findViewById(R.id.faza6));
        buttons.add(v.findViewById(R.id.faza7));
        buttons.add(v.findViewById(R.id.faza8));
        buttons.add(v.findViewById(R.id.faza10));
        buttons.add(v.findViewById(R.id.faza11));
        views.add(v.findViewById(R.id.faza1l));
        views.add(v.findViewById(R.id.faza2l));
        views.add(v.findViewById(R.id.faza3l));
        views.add(v.findViewById(R.id.faza4l));
        views.add(v.findViewById(R.id.faza5l));
        views.add(v.findViewById(R.id.faza6l));
        views.add(v.findViewById(R.id.faza7l));
        views.add(v.findViewById(R.id.faza8l));
        views.add(v.findViewById(R.id.faza10l));
        views.add(v.findViewById(R.id.faza11l));
        for(Button b:buttons){
            b.setOnClickListener(this);
        }
        return v;
    }
    void setViews (){
        int i=1;
        String s=Common.values.get("#VPU.PHATS:2");
        if (s==null){
            s="FF";
        }
        int mask=Integer.valueOf(s,16);
        for (Button b:buttons ) {
            if ((mask&1)==1 || i>8){
                b.setVisibility(View.VISIBLE);
            } else {
                    b.setVisibility(View.INVISIBLE);

            }
            b.setBackgroundColor(Color.GRAY);
            mask=mask>>1;
            i++;
        }
        mask=Integer.valueOf(s,16);
        i=1;
        for (TextView t:views ) {
            if ((mask&1)==1 || i>8){
                t.setVisibility(View.VISIBLE);
            } else {
                t.setVisibility(View.INVISIBLE);
            }
            t.setBackgroundColor(Color.WHITE);
            mask=mask>>1;
            i++;
        }
    }
    @Override
    public void View()
    {
        String s=Common.values.get("#VPU.PHATS:3");
        if (s==null){
            s="00";
        }
        int fazet=Integer.valueOf(s,16);
        if (Common.values==null) return;
        setViews();
        if (fazet>0){
            if (fazet<9 ){
                TextView t=views.get(fazet-1);
                t.setBackgroundColor(Color.GREEN);
            } else {
                TextView t=views.get(fazet-2);
                t.setBackgroundColor(Color.GREEN);
            }
        }
        s=Common.values.get("#VPU.PHATS");

        if (s==null){
            s="01";
        }
        int fazez=Integer.valueOf(s,16);

        if (fazez>0){
            if (fazez<9 ){
                Button b=buttons.get(fazez-1);
                b.setBackgroundColor(Color.YELLOW);
            } else {
                if (fazez>9){
                    Button b=buttons.get(fazez-2);
                    b.setBackgroundColor(Color.YELLOW);
                }
            }
        }
        if(fazet!=0 && fazez!=0 && fazet==fazez){
            if (fazet<9 ){
                Button b=buttons.get(fazet-1);
                b.setBackgroundColor(Color.GREEN);
            } else {
                if (fazez>9 && fazet<12){
                    Button b=buttons.get(fazet-2);
                    b.setBackgroundColor(Color.GREEN);
                }
            }
        }
        super.View();
    }

    @Override
    public void onStart() {
        Common.RegistrationFragment("control",this);
        super.onStart();
    }

    @Override
    public void onStop() {
        Common.UnRegistrationFragment("control");
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        Button b;
        switch (v.getId()){
            case R.id.faza1:
                Common.device.slot.writeMessage("#VPU.PHATU:01");
                b=buttons.get(0);
                b.setBackgroundColor(Color.BLUE);
                break;
            case R.id.faza2:
                Common.device.slot.writeMessage("#VPU.PHATU:02");
                b=buttons.get(1);
                b.setBackgroundColor(Color.BLUE);
                break;
            case R.id.faza3:
                Common.device.slot.writeMessage("#VPU.PHATU:03");
                b=buttons.get(2);
                b.setBackgroundColor(Color.BLUE);
                break;
            case R.id.faza4:
                b=buttons.get(3);
                b.setBackgroundColor(Color.BLUE);
                Common.device.slot.writeMessage("#VPU.PHATU:04");
                break;
            case R.id.faza5:
                b=buttons.get(4);
                b.setBackgroundColor(Color.BLUE);
                Common.device.slot.writeMessage("#VPU.PHATU:05");
                break;
            case R.id.faza6:
                Common.device.slot.writeMessage("#VPU.PHATU:06");
                b=buttons.get(5);
                b.setBackgroundColor(Color.BLUE);
                break;
            case R.id.faza7:
                Common.device.slot.writeMessage("#VPU.PHATU:07");
                b=buttons.get(6);
                b.setBackgroundColor(Color.BLUE);
                break;
            case R.id.faza8:
                b=buttons.get(7);
                b.setBackgroundColor(Color.BLUE);
                Common.device.slot.writeMessage("#VPU.PHATU:08");
                break;
            case R.id.faza10:
                b=buttons.get(8);
                b.setBackgroundColor(Color.BLUE);
                Common.device.slot.writeMessage("#VPU.PHATU:0A");
                break;
            case R.id.faza11:
                b=buttons.get(9);
                b.setBackgroundColor(Color.BLUE);
                Common.device.slot.writeMessage("#VPU.PHATU:0B");
                break;
        }
    }
}