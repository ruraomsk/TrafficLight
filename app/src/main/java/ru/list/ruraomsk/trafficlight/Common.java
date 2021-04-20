package ru.list.ruraomsk.trafficlight;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ru.list.ruraomsk.trafficlight.DB.DB;
import ru.list.ruraomsk.trafficlight.DB.UpdateDb;

public class Common {
    static public String HostMain;
    static public String HostLogin;
    static public String HostPassword;
    static public int PortMain;
    static public DB db;
    static public UpdateDb updateDb;
    static public Set<String> hosts;
    static public String host;
    static public ServiceConnection sconn;
    public static boolean bound=false;
    public static ConcurrentMap<String,String> values;
    public static Intent intent;
    public static Device device;
    public static ConcurrentMap<String,ViewController> fragments;
    public static Context ctx;
    public static String DefaultIpGPRS;
    public static String DefaultPortGPRS;
    public static String DefaultIpLAN;
    public static String DefaultPortLAN;
    static SharedPreferences sPref;
//    public static String repeatMessage="#VPU.PHATU:0B";
    static public void run(Context c){
        ctx=c;
        values=new ConcurrentHashMap<>();
        values.put("#SYS.LOG","");
        fragments=new ConcurrentHashMap<>();
        sPref=ctx.getSharedPreferences("litr",Context.MODE_PRIVATE);

        HostMain=sPref.getString("hostMain",ctx.getString(R.string.main_host));
        HostLogin=sPref.getString("hostLogin",ctx.getString(R.string.main_login));
        HostPassword=sPref.getString("hostPassword",ctx.getString(R.string.main_password));
        PortMain=sPref.getInt("portMain",Integer.parseInt(ctx.getString(R.string.main_port)));
        DefaultIpGPRS=sPref.getString("defaultIpGPRS","092.255.180.080");
        DefaultPortGPRS=sPref.getString("defaultPortGPRS","1096");
        DefaultIpLAN=sPref.getString("defaultIpLAN","192.168.115.013");
        DefaultPortLAN=sPref.getString("defaultPortLAN","1096");

        db=new DB(ctx);
        db.open();
        if (db.loadAll().isEmpty()){
            Log.d("litrDebug", "DataTable is empty" );

            Thread second=new Thread(new Runnable(){
                @Override
                public void run() {
                    UpdateDb updateDb=new UpdateDb(db,HostMain,PortMain,HostLogin,HostPassword);
                    hosts=db.getHosts();
                }
            });
            second.start();
            try {
                second.join();
            } catch (InterruptedException e) {
                                    Log.d("litrDebug", e.getMessage() );
            }
        }
        hosts=db.getHosts();
    }
    static public void stopDevice(){
        ctx.stopService(intent);
    }
    static public void RegitrationFragment(String name,ViewController ctx){
        Log.d("litrDebug", "Добавили "+name );
        fragments.put(name,ctx);
    }
    static public void UnRegitrationFragment(String name){
        Log.d("litrDebug", "Убрали "+name );
        fragments.remove(name);
    }
    static public void OneStepUI(){

        for (ViewController ctrl:fragments.values()) {

            ctrl.View();
        }
    }
    static public void ViewData(TextView tv,String code){
        if (values.containsKey(code)) {
            tv.setText(values.get(code));
        } else {
            Log.d("litrDebug", "Нет "+code );
        }
    }
    static public void SetData(EditText etv, String code){
        if (values.containsKey(code)) {
            etv.setText(values.get(code));
        } else {
            Log.d("litrDebug", "Нет "+code );
        }
    }
    static public void SavePref(){
        SharedPreferences.Editor editor=sPref.edit();

        editor.putString("defaultIpGPRS",DefaultIpGPRS);
        editor.putString("defaultPortGPRS",DefaultPortGPRS);
        editor.putString("defaultPortLAN",DefaultPortLAN);
        editor.putString("defaultIpLAN",DefaultIpLAN);
        editor.putString("defaultIpLAN",DefaultIpLAN);
        editor.putString("hostMain",HostMain);
        editor.putString("hostLogin",HostLogin);
        editor.putString("hostPassword",HostPassword);
        editor.putInt("portMain",PortMain);

        editor.commit();
    }

    public static String[] GetLines(String key) {
        if(!values.containsKey(key)){
            Log.d("litrDebug", "Нет "+key );
            return new String[36];
        }
        return values.get(key).split(",");
    }
}
