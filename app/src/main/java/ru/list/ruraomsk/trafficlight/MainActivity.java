package ru.list.ruraomsk.trafficlight;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.IBinder;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_CODE_DEVICES=1;
    final int REQUEST_CODE_SETTING=2;
    private TextView tvDeviceName;

    private Thread second;
    TableLayout table;
    Context ctx;
    Button fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        table=findViewById(R.id.table);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab.getText().toString().equals("Начать работу")){
                    table.setVisibility(View.VISIBLE);
                    fab.setText("Закончить работу");
                } else {
                    fab.setText("Начать работу");
                    table.setVisibility(View.INVISIBLE);
                }

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Common.run(this);
        Common.intent=new Intent(this, Device.class);
        ComponentName name=startService(new Intent(this, Device.class));
        if(name!=null) Log.d("litrDebug", name.getPackageName()+""+name.getClassName() );

        Common.sconn=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("litrDebug", "onServerConnected" );
                Common.device=((Device.DeviceBinder) service).getService();
                Common.bound=true;

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Common.bound=false;
            }
        };
        if(!bindService(Common.intent,Common.sconn,0)){
            Log.d("litrDebug", "not binding" );

        };

        tvDeviceName=findViewById(R.id.device_name);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_update:
                //Обновляем БД устройств
                Common.db.clearAll();
                Common.run(this);
                break;
            case R.id.menu_choice:
                //выбираем устройство
                startActivityForResult(new Intent(this,ChoiceDevice.class),REQUEST_CODE_DEVICES);
                break;
            case R.id.menu_option:
                //выбираем устройство
                startActivityForResult(new Intent(this,Setting.class),REQUEST_CODE_SETTING);
                break;
            case R.id.menu_open:
                //Подключаем устройство
                fab.setVisibility(View.INVISIBLE);
                table.setVisibility(View.INVISIBLE);
                if(!Common.bound){
                    Toast.makeText(this, "Сервис не запущен", Toast.LENGTH_LONG).show();
                    break;
                }
                if(Common.host==null){
                    Toast.makeText(this, "Выберите устройство", Toast.LENGTH_LONG).show();
                    break;
                }
                Common.device.setDevice(Common.host,Common.db.getPort(Common.host),Common.db.getLogin(Common.host),Common.db.getPassword(Common.host));
                Common.device.connect();
                if(Common.device.isWork())  {
                    Toast.makeText(this, "Устанавлено соединение", Toast.LENGTH_LONG).show();
                    tvDeviceName.setTextColor(Color.GREEN);
                    fab.setVisibility(View.VISIBLE);
                    second=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true){
                                    Thread.sleep(1000);
                                    Date date=new Date(System.currentTimeMillis());
                                    SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
                                    Common.values.put("#SYSTEM.TIME",formatter.format(date));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Common.OneStepUI();
                                        }
                                    });
                                }
                            } catch (InterruptedException e) {
//                                Log.d("litrDebug", e.getMessage());
                            }

                        }
                    });
                    second.start();
                }
                else {
                    Toast.makeText(this, "Соединение не установлено", Toast.LENGTH_LONG).show();
                    tvDeviceName.setTextColor(Color.RED);
                }
                break;
            case R.id.menu_close:
                //Отключаем устройство

                fab.setVisibility(View.INVISIBLE);
                table.setVisibility(View.INVISIBLE);

                if (Common.device==null) break;
                if(!Common.bound){
                    Toast.makeText(this, "Сервис не запущен", Toast.LENGTH_LONG).show();
                    break;
                }
                if (second==null) {
                    Toast.makeText(this, "Подключения не было", Toast.LENGTH_LONG).show();
                    break;
                }
                second.interrupt();
                Common.device.disconnect();
                Toast.makeText(this, "Отключаем соединение", Toast.LENGTH_LONG).show();
                if(Common.device.isWork())  {
                    tvDeviceName.setTextColor(Color.GREEN);
                }
                else {
                    tvDeviceName.setTextColor(Color.RED);
                    Common.values.clear();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
//        Log.d("litrDebug", "requestCode = " + requestCode + ", resultCode = " + resultCode);

        if(resultCode==RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_DEVICES:
                    Common.host=data.getStringExtra("host");
//                    Log.d("litrDebug", "host = " + Common.host);

                    tvDeviceName.setText(Common.db.getName(Common.host));
                    break;
                case REQUEST_CODE_SETTING:
                    Common.run(ctx);
                    break;
            }
        }else {
            Toast.makeText(this, "Отмена", Toast.LENGTH_SHORT).show();
            Common.host=null;
            tvDeviceName.setText("Устройство не выбрано");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}