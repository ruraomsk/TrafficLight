package ru.list.ruraomsk.trafficlight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ListViewAutoScrollHelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Set;

import ru.list.ruraomsk.trafficlight.DB.DB;
import ru.list.ruraomsk.trafficlight.DB.UpdateDb;

public class ChoiceDevice extends AppCompatActivity implements View.OnClickListener {

    Button bOk;
    private ListView listView;
    private String[] aHosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_device);
        bOk=findViewById(R.id.btnAccept);

        bOk.setOnClickListener(this);
        Common.run(this);
        Log.d("litrDebug", Common.HostMain );
        String[] names=new String[Common.hosts.size()];
        aHosts=new String[Common.hosts.size()];
        int i=0;
        for (String host:Common.hosts) {
            aHosts[i]=host;
            names[i++]=Common.db.getName(host);
        }
        listView=findViewById(R.id.ldevices);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,names);
        listView.setAdapter(adapter);
        ListViewAutoScrollHelper scrollHelper=new ListViewAutoScrollHelper(listView);
        scrollHelper.setEnabled(true);
        listView.setOnTouchListener(scrollHelper);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()){
            case R.id.btnAccept:
            intent.putExtra("host",aHosts[listView.getCheckedItemPosition()]);
            setResult(RESULT_OK,intent);
            finish();
            break;
        }
    }

}