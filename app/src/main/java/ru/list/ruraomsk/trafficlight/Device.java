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
    private boolean work=false;
    private DeviceBinder binder=new DeviceBinder();
    private ReadData second=null;
    private Thread thread=null;
    public void setDevice(String host,int port,String login,String password){
        this.host=host;
        this.port=port;
        this.login=login;
        this.password=password;


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("litrDebug", "OnStartCommand Device" );

        return super.onStartCommand(intent, flags, startId);
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
//            slot.writeMessage("login="+login+" password="+password);
//            while (slot.isWork()&&!slot.isMessage()){
//                sleep(100);
//            }
//            String message=slot.getMessage();
//            if (message==null) return;
//            Log.d("litrDebug","Получил:"+message+":");
//            if (message.contains("OK")&&slot.isWork()) {
//                Log.d("litrDebug","Прошел верификацию");
//                work=true;
//            } else {
//                Log.d("litrDebug","Верификацию не прошел");
//                return;
//            }
        } catch (InterruptedException e) {
            Log.d("litrDebug",e.getMessage());
        }
        second=new ReadData(slot,this);
        second.start();
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    //TODO

                } catch (InterruptedException e) {
                    Log.d("litrDebug",e.getMessage());
                }

            }
        });
        thread.start();
    }
    public void disconnect(){
        if(slot==null) return;
//        slot.writeMessage("exit");
        try {
            sleep(1000);
            second.interrupt();
//            thread.interrupt();
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
    public void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("litrDebug", "OnCrete Device" );
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
