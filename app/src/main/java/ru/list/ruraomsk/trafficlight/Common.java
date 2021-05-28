package ru.list.ruraomsk.trafficlight;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import ru.list.ruraomsk.trafficlight.DB.DB;
import ru.list.ruraomsk.trafficlight.DB.UpdateDb;

public class Common {
    static public String HostMain;
    static public String HostLogin;
    static public String HostPassword;
    static public int PortMain;
    static public DB db;
    static public UpdateDb updateDb;
    static public Set<String> poses;
    static public String pos;
    static public ServiceConnection sconn;
    public static boolean bound=false;
    public static ConcurrentMap<String,String> values;
    public static Intent intent;
    public static Device device;
    public static ConcurrentMap<String,ViewController> fragments;
    public static Context ctx;
    static SharedPreferences sPref;
    static public void run(Context c){
        ctx=c;
        values=new ConcurrentHashMap<>();
        values.put("#SYS.LOG","");
        fragments=new ConcurrentHashMap<>();
        sPref=ctx.getSharedPreferences("litr",Context.MODE_PRIVATE);

        HostMain=sPref.getString("hostMain",ctx.getString(R.string.main_host));
        PortMain=sPref.getInt("portMain",Integer.parseInt(ctx.getString(R.string.main_port)));
        HostLogin=sPref.getString("hostLogin",ctx.getString(R.string.main_login));
        HostPassword=sPref.getString("hostPassword",ctx.getString(R.string.main_password));
        db=new DB(ctx);
        db.open();
        if (db.loadAll().isEmpty()){
            Log.d("litrDebug", "DataTable is empty" );

            Thread second=new Thread(new Runnable(){
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    updateDb=new UpdateDb(db,HostMain,PortMain,HostLogin,HostPassword);
                    poses=db.getPoses();
                }
            });
            second.start();
            try {
                second.join();
            } catch (InterruptedException e) {
                                    Log.d("litrDebug", e.getMessage() );
            }
        }
        poses=db.getPoses();
    }
    static public void stopDevice(){
        ctx.stopService(intent);
    }
    static public void RegistrationFragment(String name, ViewController ctx){
        Log.d("litrDebug", "Добавили "+name );
        fragments.put(name,ctx);
    }
    static public void UnRegistrationFragment(String name){
        Log.d("litrDebug", "Убрали "+name );
        fragments.remove(name);
    }
    static public void OneStepUI(){

        for (ViewController ctrl:fragments.values()) {

            ctrl.View();
        }
    }
    final static private int LenIV=16;
    final static private String Key="TZPtSIacEJG18IrUrAkTE6luYmnCNKgR";
    static public String Decode(String message64) {
        try {
            byte[] message=android.util.Base64.decode(message64, android.util.Base64.DEFAULT);
            SecretKey secretKey= new SecretKeySpec(Key.getBytes(),"AES");
            final Cipher cipher=Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec iv=new IvParameterSpec(message,0,LenIV);
            cipher.init(Cipher.DECRYPT_MODE,secretKey,iv);
            byte[] res=cipher.doFinal(message,LenIV,message.length-LenIV);
            String str=new String(res);
            return str.trim();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            Log.d("litrDebug", e.getMessage() );
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    static public String Encode(String message) {
        try {
            int l=message.length();
            while(l%LenIV!=0){
                l++;
            }
            ByteBuffer ciphered= ByteBuffer.allocate(l);
            ciphered.put(message.getBytes());
            for (int i = message.length(); i < l; i++) {
                ciphered.put((byte) 0x20);
            }

            final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKey secretKey= new SecretKeySpec(Key.getBytes(),"AES");
            byte[]iv=new byte[LenIV];
            SecureRandom secureRandom=new SecureRandom();
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec=new IvParameterSpec(iv);
//            GCMParameterSpec parameterSpec=new GCMParameterSpec(cipher.getBlockSize(),iv);
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,ivSpec);
            byte[] cipherText=cipher.doFinal(ciphered.array());
            ByteBuffer byteBuffer=ByteBuffer.allocate(iv.length+cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

//    static public void ViewData(TextView tv,String code){
//        if (values.containsKey(code)) {
//            tv.setText(values.get(code));
//        } else {
//            Log.d("litrDebug", "Нет "+code );
//        }
//    }
//    static public void SetData(EditText etv, String code){
//        if (values.containsKey(code)) {
//            etv.setText(values.get(code));
//        } else {
//            Log.d("litrDebug", "Нет "+code );
//        }
//    }
//    static public void SavePref(){
//        SharedPreferences.Editor editor=sPref.edit();
//
//        editor.putString("hostMain",HostMain);
//        editor.putString("hostLogin",HostLogin);
//        editor.putString("hostPassword",HostPassword);
//        editor.putInt("portMain",PortMain);
//
//        editor.commit();
//    }

//    public static String[] GetLines(String key) {
//        if(!values.containsKey(key)){
//            Log.d("litrDebug", "Нет "+key );
//            return new String[36];
//        }
//        return values.get(key).split(",");
//    }
}
