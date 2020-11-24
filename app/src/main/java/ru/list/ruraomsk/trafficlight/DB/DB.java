package ru.list.ruraomsk.trafficlight.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.list.ruraomsk.trafficlight.Common;


public class DB {
    private static final String DbName="litr";
    private static final int DbVersion=1;
    private static final String DbTable="tabled";
    private static final String ColumnHost="host";
    private static final String ColumnName="name";
    private static final String DbCreate=
    "create table "+DbTable+"("+ColumnHost+" text ,"+ColumnName+" text);";
    private final Context ctx;

    private DBHelper dbHelper=null;
    private SQLiteDatabase db=null;
    private HashMap<String,DevDef> mapDef =new HashMap<>();

    public DB(Context ctx){
        this.ctx=ctx;
    }
    public void open(){
        dbHelper=new DBHelper(ctx);
        db=dbHelper.getWritableDatabase();
    }

    public void close(){
        if (db!=null)       db.close();
        if (dbHelper!=null) dbHelper.close();
    }
    public String getName(String host){
        return mapDef.get(host).name;
    }
    public String getLogin(String host){
        return mapDef.get(host).login;
    }
    public String getPassword(String host){
        return mapDef.get(host).password;
    }
    public int getPort(String host){
        return mapDef.get(host).port;
    }
    public void clearAll(){
        mapDef =new HashMap<String, DevDef>();
        db.execSQL("drop table if exists "+DbTable+" ;");
        db.execSQL("create table "+DbTable+" (host text, port text,name text,login text, password text);");
        Log.d("litrDebug", "DataBase clean" );

//        db.delete(DbTable,null,null);
    }
    public void appendString(String str){
        Log.d("litrDebug", str );

        DevDef d=new DevDef(str);
        ContentValues cv=new ContentValues();
        cv.put("host",d.host);
        cv.put("port",d.port);
        cv.put("name",d.name);
        cv.put("login",d.login);
        cv.put("password",d.password);
        if (db.insert(DbTable,null,cv)>0)   mapDef.put(d.host,d);
    }
    public Set<String> getHosts(){
        return mapDef.keySet();
    }
    public Set<String> loadAll(){
        mapDef =new HashMap<String, DevDef>();

        String[] listNames= new String[]{"host", "port","name", "login", "password"};
        Cursor cursor;
        cursor = db.query(DbTable,listNames,null,null,null,null,null,null);
        while(cursor.moveToNext()) {
            DevDef d=new DevDef();
            d.host=cursor.getString(0);
            d.port=cursor.getInt(1);
            d.name=cursor.getString(2);
            d.login=cursor.getString(3);
            d.password=cursor.getString(4);
            mapDef.put(d.host,d);
        }
        return mapDef.keySet();
    }
    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context){
            super(context,"dbAir",null,1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "+DbTable+" (host text, port text,name text,login text, password text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table "+DbTable+";");
            db.execSQL("create table "+DbTable+" (host text, port text,name text,login text, password text);");

        }

    }
}
class DevDef{
    public String host;
    public int port;
    public String name;
    public String login;
    public String password;

    public DevDef (){

    }
    public DevDef(String str) {
        String[] l=str.split(":");
        if (l.length!=5) return;
        host=l[0];
        port=Integer.parseInt(l[1]);
        name=l[2];
        login=l[3];
        password=l[4];
    }
}
