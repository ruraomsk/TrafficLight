package ru.list.ruraomsk.trafficlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Setting extends AppCompatActivity implements View.OnClickListener {
    private Button bOk;
    private EditText eHost;
    private EditText ePort;
    private EditText eLogin;
    private EditText ePassword;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ctx=this;
        bOk=findViewById(R.id.btnOk);
        bOk.setOnClickListener(this);
        eHost=findViewById(R.id.main_host);
        ePort=findViewById(R.id.main_port);
        eLogin=findViewById(R.id.main_login);
        ePassword=findViewById(R.id.main_password);
        SharedPreferences sPref=ctx.getSharedPreferences("litr",Context.MODE_PRIVATE);

        eHost.setText(sPref.getString("hostMain",ctx.getString(R.string.main_host)));

        ePort.setText(String.valueOf(sPref.getInt("portMain",Integer.parseInt(ctx.getString(R.string.main_port)))));
        eLogin.setText(sPref.getString("hostLogin",ctx.getString(R.string.main_login)));
        ePassword.setText(sPref.getString("hostPassword",ctx.getString(R.string.main_password)));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
                Intent intent=new Intent();
                SharedPreferences sPref=ctx.getSharedPreferences("litr", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sPref.edit();
                editor.putString("hostMain",eHost.getText().toString());
                editor.putInt("portMain",Integer.getInteger(ePort.getText().toString()));
                editor.putString("hostLogin",eLogin.getText().toString());
                editor.putString("hostPassword",ePassword.getText().toString());
                editor.commit();
                Toast.makeText(this,"Сохранено",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }
}