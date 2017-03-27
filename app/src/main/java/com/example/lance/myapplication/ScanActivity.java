package com.example.lance.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ScanActivity extends AppCompatActivity {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805B7D34EA");
    UUID testUUID=null;
    public static final int CONNECT_SUCCESS=1;
    public static final int SOCKET_FAILED=2;
    public static final int REQUEST_ENABLE_BT = 1;
    private List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ScanActivity.CONNECT_SUCCESS:
                    Toast.makeText(ScanActivity.this, "Bluetooth Connected!", Toast.LENGTH_SHORT).show();
                    break;
                case ScanActivity.SOCKET_FAILED:
                    Toast.makeText(ScanActivity.this, "Failed to create socket!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        //lv= (ListView) findViewById(R.id.list_view);
        //Log.e("test", String.valueOf(MY_UUID));
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
            for(BluetoothDevice device : pairedDevices)
                deviceList.add(device);

        //String[] mString = (String[]) deviceList.toArray();
        DeviceAdapter adapter = new DeviceAdapter(ScanActivity.this, R.layout.device_item, deviceList);
        ListView listview = (ListView) findViewById(R.id.list_view);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = deviceList.get(position);
                DisplayToast("Connecting "+ device.getName());
                ParcelUuid test = device.getUuids()[position];
                testUUID = test.getUuid();
                new ConnectThread(mBluetoothAdapter.getRemoteDevice(device.getAddress())).start();
            }
        });

    }
    public class DeviceAdapter extends ArrayAdapter<BluetoothDevice>{
        private int resourceId;
        public DeviceAdapter(Context context, int textViewResourceId, List<BluetoothDevice> objects){
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            BluetoothDevice device = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            TextView deviceName = (TextView) view.findViewById(R.id.device_name);
            deviceName.setText(device.getName());
            return view;
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private OutputStream outStream;
        private InputStream inStream;
        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(testUUID);
                Log.e("testUUID", String.valueOf(testUUID));
            } catch (IOException e) {
                DisplayToastThread(ScanActivity.SOCKET_FAILED);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
           // manageConnectedSocket(mmSocket);
            try {
                outStream = mmSocket.getOutputStream();
                inStream = mmSocket.getInputStream();
            }
            catch(IOException e){
            }
            DisplayToastThread(ScanActivity.CONNECT_SUCCESS);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }

    private void DisplayToast(String s) {
        Toast.makeText(ScanActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * 在子线程中使用toast方法
     * @param s
     */
    private void DisplayToastThread(int s){
        Message msg = mHandler.obtainMessage();
        msg.what = s;
        mHandler.sendMessage(msg);
    }
}
