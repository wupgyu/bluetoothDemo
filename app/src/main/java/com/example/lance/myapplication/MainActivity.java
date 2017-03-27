package com.example.lance.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button save;
    Button send;
    Button stop;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        save=(Button)findViewById(R.id.button_save);
        send=(Button)findViewById(R.id.button_send);
        stop=(Button)findViewById(R.id.button_stop);

        save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "save",Toast.LENGTH_SHORT).show();
            }
        });
        send.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "send",Toast.LENGTH_SHORT).show();
            }
        });
        stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "stop",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_item:
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
                break;
            case R.id.setting_item:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            default:
        }
        return true;
    }
}
