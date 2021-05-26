package ru.list.ruraomsk.trafficlight;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import ru.list.ruraomsk.trafficlight.slot.ReadData;
import ru.list.ruraomsk.trafficlight.slot.Slot;

import static java.lang.Thread.sleep;

public class Device extends Service {
    private String login;
    private String password;
    private String host;
    private int port;
    public Slot slot=null;
    public boolean work=false;
    private DeviceBinder binder=new DeviceBinder();
    private ReadData second=null;
    private Thread thread=null;
    public void setDevice(String host,int port,String login,String password){
        this.host=host;
        this.port=port;
        this.login=login;
        this.password=password;


    }


    public void connect(){
        if(slot!=null) return;
        Log.d("litrDebug", this.host );
        try {
            slot=new Slot(host,port);
            slot.start();
            sleep(3000);
            if(!slot.isWork()) {
                Log.d("litrDebug","Slot не запустился");
                slot=null;
                return;
            }
            work=true;
        } catch (InterruptedException e) {
            Log.d("litrDebug",e.getMessage());
        }
        second=new ReadData(slot,this);
        second.start();
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(work){
                        sleep(10000);
                        //TODO
                        slot.writeMessage("#VPU.PHATU:0D");
                    }

                } catch (InterruptedException e) {
                    Log.d("litrDebug",e.getMessage());
                }

            }
        });
        thread.start();
    }
    public void disconnect(){
        if(slot==null) return;
        slot.writeMessage("#VPU.PHATU:FF");
        try {
            sleep(1000);
            slot.work=false;
            sleep(1000);
            while(slot.isWork()){
                sleep(500);
            }
            work=false;
            slot=null;
            thread=null;
            second=null;
        } catch (InterruptedException e) {
            Log.d("litr",e.getMessage());
        }
    }
    public boolean isWork(){
        if (slot==null) return false;
        return slot.isWork()&&work;
    }

    @Override
    public IBinder onBind(Intent intent) {
//        Log.d("litrDebug", "OnBind Device" );
        return binder;
    }

    public class DeviceBinder extends Binder{
        public Device getService(){
            return Device.this;
        }
    }
}
