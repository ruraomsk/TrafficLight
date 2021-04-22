package ru.list.ruraomsk.trafficlight.slot;

import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;

import ru.list.ruraomsk.trafficlight.Common;
import ru.list.ruraomsk.trafficlight.Device;

public class ReadData  extends Thread{
    Slot slot;
    Device device;
    public ReadData(Slot slot, Device device) {
        this.slot=slot;
        this.device=device;
    }
    @Override
    public void run() {
        try {
            while(slot.isWork()){
                while (!slot.isMessage()){
                    if (!slot.isWork()) return;
                    sleep(100);
                }
                //Пришло сообшение от устройства
                String message=slot.getMessage();
                //Разбираем его на имя и текст
                if (!message.startsWith("#") || !message.contains(":") ||!message.contains(".") ){
                    Log.d("litrDebug","Не данные !"+message);
                    Date date=new Date(System.currentTimeMillis());
                    SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
                    Common.values.put("#SYS.LOG",formatter.format(date)+" "+message+"\n"+Common.values.get("#SYS.LOG"));
                    continue;
                }
//                Log.d("litrDebug","<"+message+">");
                String[] list=message.split(":");
//
                String last="";
                if (list.length ==4){
                    //Тут еще и время есть
                    // Выделяем его и остаток в last
                    String time=list[1]+":"+list[2]+":"+list[3].substring(0,2);
                    last=list[3].substring(3);
                    Common.values.put(list[0]+".TIME",time);
//                    Log.d("litrDebug","Time "+list[0]+".TIME"+"  <"+time+">");
                } else {
                    if(list.length ==1) last=" ";
                    else {
                        String[] l=list[1].split(",");
                        if (l.length==3) {
                            //Есть второй и третий параметр
                            Common.values.put(list[0]+":2",l[1]);
                            Common.values.put(list[0]+":3",l[2]);
                            list[1]=l[0];
                        }
                        last+=list[1];
                    }
                }
                if(last.length()==0) last=" ";
                Common.values.put(list[0],last);
//                Log.d("litrDebug","Ключ "+list[0]+"  <"+last+">");
            }
        } catch (InterruptedException e) {
            if(e!=null) Log.d("litrDebug",e.getMessage());
        }
        Log.d("litrDebug","Stop ReadData");

    }
}
