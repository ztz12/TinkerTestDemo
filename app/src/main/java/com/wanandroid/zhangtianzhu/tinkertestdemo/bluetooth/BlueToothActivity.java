package com.wanandroid.zhangtianzhu.tinkertestdemo.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

import java.util.Objects;

public class BlueToothActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private Button btnSearch;
    private TextView tvInit;
    private TextView tvBluePaired;
    private TextView tvBlueUnPaired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        initBlueTooth();
        initView();
    }

    private void initView(){
        btnSearch = findViewById(R.id.btn_search);
        tvInit = findViewById(R.id.tv_initial);
        tvBluePaired = findViewById(R.id.tv_blue_paired);
        tvBlueUnPaired = findViewById(R.id.tv_blue_unpaired);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断蓝牙是否打开
                if(!mBluetoothAdapter.isEnabled()){
                    //没有打开进行开启蓝牙
                    //注意：强制打开蓝牙设置情况有三种
                    // （1）没有任何提示，直接打开了蓝牙。如Nexus 5 Android 4.4.4 手机。
                    //（2）会弹出提示框，提示安全警告 “ ***应用尝试开启蓝牙”，可以选择“拒绝”或“允许”。大多数手机都是这样的。
                    //（3）强制打开蓝牙失败，并且没有任何提示。
                    mBluetoothAdapter.enable();
                }
                //开始广播，当广播是我们刚才注册的事件就会触发广播接收器，继而触发onReceive方法
                mBluetoothAdapter.startDiscovery();
                tvInit.setText("正在搜索。。。");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver,filter);
    }

    private void initBlueTooth(){
        //获得默认的蓝牙适配器 如果bluetoothAdapter返回为null，表示不支持蓝牙
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //BluetoothDevice 代表一个远程蓝牙设备，使用这个来与远程的BluetoothSocket连接，或者查询设备名称，地址，类或者连接状态信息
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device!=null) {
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        //显示已配对的设备
                        tvBluePaired.append("\n"+device.getName()+"=>"+device.getAddress()+"\n");
                    }else if(device.getBondState()!=BluetoothDevice.BOND_BONDED){
                        //显示没有配对
                        tvBlueUnPaired.append("\n"+device.getName()+"=>"+device.getAddress()+"\n");
                    }
                }
            }else if(Objects.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED,action)){
                tvInit.setText("搜索完成");
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

}
