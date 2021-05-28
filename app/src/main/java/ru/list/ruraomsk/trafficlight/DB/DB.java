package ru.list.ruraomsk.trafficlight.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class DB {
    private static final String DbName="litr";
    private static final int DbVersion=2;
    private static final String DbTable="tabled";
    private static final String ColumnHost="host";
    private static final String ColumnPort="port";
    private static final String ColumnPos="pos";
    private static final String ColumnName="name";
    private static final String ColumnSSID="ssid";
    private static final String ColumnPassID="passid";
    private static final String ColumnLogin="login";
    private static final String ColumnPassword="password";
    private static final String ColumnJSON="json";


    private static String[] listNames= new String[]{ColumnPos,ColumnName, ColumnHost,ColumnPort, ColumnSSID,ColumnPassID,ColumnLogin,ColumnPassword,ColumnJSON};

    private static final String DbCreate=
    "create table "+DbTable+"("+ColumnHost+" text ,"+ColumnPort+" integer, "+ColumnName+" text, "+ColumnPos+" text, "+ColumnSSID+" text, "+ColumnPassID+" text, "+ColumnLogin+" text, "+ColumnPassword+" text,"+ColumnJSON+" text "+");";
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

    public String getName(String pos){
        return mapDef.get(pos).name;
    }
    public String getLogin(String pos){
        return mapDef.get(pos).login;
    }
    public String getPassword(String pos){
        return mapDef.get(pos).password;
    }
    public String getPassID(String pos){
        return mapDef.get(pos).passid;
    }
    public String getSSID(String pos){
        return mapDef.get(pos).ssid;
    }
    public String getHost(String pos){
        return mapDef.get(pos).host;
    }
    public int getPort(String pos){
        return mapDef.get(pos).port;
    }
    public void clearAll(){
        mapDef =new HashMap<String, DevDef>();
        db.execSQL("drop table if exists "+DbTable+" ;");
        db.execSQL(DbCreate);
        Log.d("litrDebug", "DataBase clean" );

//        db.delete(DbTable,null,null);
    }
    public void appendString(String str){
        Log.d("litrDebug", str );

        DevDef d=new DevDef(str);
        ContentValues cv=new ContentValues();
        cv.put("pos",d.pos);
        cv.put("host",d.host);
        cv.put("port",d.port);
        cv.put("name",d.name);
        cv.put("login",d.login);
        cv.put("password",d.password);
        cv.put("ssid",d.ssid);
        cv.put("passid",d.passid);
        cv.put("json",d.toJSON());

        if (db.insert(DbTable,null,cv)>0)   mapDef.put(d.pos,d);
    }
    public Set<String> getPoses(){
        return mapDef.keySet();
    }
    public Set<String> loadAll(){
        mapDef =new HashMap<String, DevDef>();

        Cursor cursor;
        cursor = db.query(DbTable,listNames,null,null,null,null,null,null);
//        private static String[] listNames= new String[]{ColumnPos,ColumnName, ColumnHost,ColumnPort, ColumnSSID,ColumnPassID,ColumnLogin,ColumnPassword};
        while(cursor.moveToNext()) {
            DevDef d=new DevDef(cursor.getString(8));
            d.pos=cursor.getString(0);
            d.name=cursor.getString(1);
            d.host=cursor.getString(2);
            d.port=cursor.getInt(3);
            d.ssid= cursor.getString(4);
            d.passid=cursor.getString(5);
            d.login=cursor.getString(6);
            d.password=cursor.getString(7);
            mapDef.put(d.pos,d);
        }
        return mapDef.keySet();
    }
    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context){
            super(context,"dbAir",null,2);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DbCreate);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table "+DbTable+";");
            db.execSQL(DbCreate);

        }

    }
}
class Faza {
    public int num;
    public String name;
    public ArrayList<Byte> png;
    public Faza(JSONObject json) throws JSONException {
        num= json.getInt("number");
        name=json.getString("name");
        png=new ArrayList<>();
        JSONArray array=json.getJSONArray("png");
        if (array!=null){
            for (int i = 0; i < array.length(); i++) {
                png.add((Byte) array.get(i));
            }
        }
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("number",num);
        jsonObject.put("name",name);
        JSONArray ja=new JSONArray();
        for (int i = 0; i < png.size(); i++) {
            ja.put(png.get(i));
        }
        jsonObject.put("png",ja);
        return jsonObject;
    }
}
class DevDef{
    public String pos;
    public String host;
    public int port;
    public String name;
    public String ssid;
    public String passid;

    public String login;
    public String password;
    public ArrayList<Faza> fazas;
    public DevDef (){

    }
    public DevDef(String str) {
        int area,id;
        try {
            JSONObject jsonObject=new JSONObject(str);
            area=jsonObject.getInt("area");
            id=jsonObject.getInt("id");
            pos=String.valueOf(area)+":"+String.valueOf(id);
            port=jsonObject.getInt("port");
            host=jsonObject.getString("host");
            name=jsonObject.getString("name");
            login= jsonObject.getString("login");
            password=jsonObject.getString("password");
            ssid=jsonObject.getString("ssid");
            passid=jsonObject.getString("passid");
            fazas=new ArrayList<>();
            JSONArray array=jsonObject.getJSONArray("fazes");
            if (array!=null){
                for (int i = 0; i < array.length(); i++) {
                    fazas.add(new Faza((JSONObject) array.get(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String toJSON() {
        try {
        JSONObject jres=new JSONObject();
        JSONArray jarray=new JSONArray();
        for (int i = 0; i < fazas.size(); i++) {
                jarray.put(fazas.get(i).toJSON());
        }
        jres.put("fazes",jarray);
        return jres.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "{}";
    }

}
