package com.wanandroid.zhangtianzhu.tinkertestdemo.bluetooth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wanandroid.zhangtianzhu.tinkertestdemo.BuildConfig;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.bluetooth.adapter.BlueListAdapter;
import com.wanandroid.zhangtianzhu.tinkertestdemo.bluetooth.bean.BlueDevice;
import com.wanandroid.zhangtianzhu.tinkertestdemo.bluetooth.util.BluetoothUtil;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.AssistStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivityTwo extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    private static final String TAG = "BluetoothActivity";
    private CheckBox ck_bluetooth;
    private TextView tv_discovery;
    private ListView lv_bluetooth; // 声明一个用于展示蓝牙设备的列表视图对象
    private BluetoothAdapter mBluetooth; // 声明一个蓝牙适配器对象
    private BlueListAdapter mListAdapter; // 声明一个蓝牙设备的列表适配器对象
    private ArrayList<BlueDevice> mDeviceList = new ArrayList<BlueDevice>(); // 蓝牙设备队列
    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private int mOpenCode = 1; // 是否允许扫描蓝牙设备的选择对话框返回结果代码
    private final int REQUEST_PERMISSION_ACCESS_LOCATION = 0;
    private List<String> permissions = new ArrayList<>();
    private int mPermissionRequestCount = 0;
    private int MAX_NUMBER_REQUEST_PERMISSIONS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_two);
        initBluetooth(); // 初始化蓝牙适配器
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        tv_discovery = findViewById(R.id.tv_discovery);
        lv_bluetooth = findViewById(R.id.lv_bluetooth);
        ck_bluetooth.setOnCheckedChangeListener(this);
        if (BluetoothUtil.getBlueToothStatus(this)) {
            ck_bluetooth.setChecked(true);
        }
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_CONTACTS);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        requestPermissionIfNecessary();
    }

    // 初始化蓝牙适配器
    private void initBluetooth() {
        // Android从4.3开始增加支持BLE技术（即蓝牙4.0及以上版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 从系统服务中获取蓝牙管理器
            BluetoothManager bm = (BluetoothManager)
                    getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetooth = bm.getAdapter();
        } else {
            // 获取系统默认的蓝牙适配器
            mBluetooth = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetooth == null) {
            Toast.makeText(this, "本机未找到蓝牙功能", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 初始化蓝牙设备列表
    private void initBlueDevice() {
        mDeviceList.clear();
        // 获取已经配对的蓝牙设备集合
        Set<BluetoothDevice> bondedDevices = mBluetooth.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), device.getBondState()));
        }
        if (mListAdapter == null) { // 首次打开页面，则创建一个新的蓝牙设备列表
            mListAdapter = new BlueListAdapter(this, mDeviceList);
            lv_bluetooth.setAdapter(mListAdapter);
            lv_bluetooth.setOnItemClickListener(this);
        } else { // 不是首次打开页面，则刷新蓝牙设备列表
            mListAdapter.notifyDataSetChanged();
        }
    }

    private Runnable mDiscoverable = new Runnable() {
        public void run() {
            // Android8.0要在已打开蓝牙功能时才会弹出下面的选择窗
            if (BluetoothUtil.getBlueToothStatus(BluetoothActivityTwo.this)) {
                // 弹出是否允许扫描蓝牙设备的选择对话框
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //设置检测蓝牙时间，默认为120s，通过以下方式设置为100s
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 100);
                startActivityForResult(intent, mOpenCode);
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_bluetooth) {
            if (isChecked) { // 开启蓝牙功能
                ck_bluetooth.setText("蓝牙开");
                if (!BluetoothUtil.getBlueToothStatus(this)) {
                    BluetoothUtil.setBlueToothStatus(this, true); // 开启蓝牙功能
                }
                mHandler.post(mDiscoverable);
            } else { // 关闭蓝牙功能
                ck_bluetooth.setText("蓝牙关");
                cancelDiscovery(); // 取消蓝牙设备的搜索
                BluetoothUtil.setBlueToothStatus(this, false); // 关闭蓝牙功能
                initBlueDevice(); // 初始化蓝牙设备列表
            }
        }
    }

    private void requestPermissionIfNecessary() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkAllPermission()) {
                if (mPermissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                    mPermissionRequestCount += 1;
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_ACCESS_LOCATION);
                } else {
                    Toast.makeText(this, "缺失权限", Toast.LENGTH_LONG).show();
                }
            } else {
                initBlueDevice(); // 初始化蓝牙设备列表
            }
        } else {
            initBlueDevice(); // 初始化蓝牙设备列表
        }
    }

    private boolean checkAllPermission() {
        boolean hasPermission = true;
        for (String permission : permissions) {
            hasPermission = hasPermission && ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION: {
                if (checkAllPermission()) {
                    Log.d(TAG, "开启权限permission granted!");
                    //做下面该做的事
                    initBlueDevice(); // 初始化蓝牙设备列表
                } else {
                    showSettingDialog();
                }
            }
            break;
            default:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("帮助");
        builder.setMessage("当前应用缺少权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + BluetoothActivityTwo.this.getPackageName()));
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == mOpenCode) { // 来自允许蓝牙扫描的对话框
            // 延迟50毫秒后启动蓝牙设备的刷新任务
            mHandler.postDelayed(mRefresh, 50);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "允许本地蓝牙被附近的其它蓝牙设备发现",
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "不允许蓝牙被附近的其它蓝牙设备发现",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 定义一个刷新任务，每隔两秒刷新扫描到的蓝牙设备
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            beginDiscovery(); // 开始扫描周围的蓝牙设备
            // 延迟2秒后再次启动蓝牙设备的刷新任务
//            mHandler.postDelayed(this, 2000);
        }
    };

    // 开始扫描周围的蓝牙设备
    private void beginDiscovery() {
        // 如果当前不是正在搜索，则开始新的搜索任务
        if (!mBluetooth.isDiscovering()) {
            initBlueDevice(); // 初始化蓝牙设备列表
            tv_discovery.setText("正在搜索蓝牙设备");
            mBluetooth.startDiscovery(); // 开始扫描周围的蓝牙设备
        }
    }

    // 取消蓝牙设备的搜索
    private void cancelDiscovery() {
        mHandler.removeCallbacks(mRefresh);
        tv_discovery.setText("取消搜索蓝牙设备");
        // 当前正在搜索，则取消搜索任务
        if (mBluetooth.isDiscovering()) {
            mBluetooth.cancelDiscovery(); // 取消扫描周围的蓝牙设备
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mRefresh, 50);
        // 需要过滤多个动作，则调用IntentFilter对象的addAction添加新动作
        IntentFilter discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        // 注册蓝牙设备搜索的广播接收器
        registerReceiver(discoveryReceiver, discoveryFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelDiscovery(); // 取消蓝牙设备的搜索
        // 注销蓝牙设备搜索的广播接收器
        unregisterReceiver(discoveryReceiver);
    }

    // 蓝牙设备的搜索结果通过广播返回
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive action=" + action);
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) { // 发现新的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "name=" + device.getName() + ", state=" + device.getBondState());
                refreshDevice(device, device.getBondState()); // 将发现的蓝牙设备加入到设备列表
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) { // 搜索完毕
                //mHandler.removeCallbacks(mRefresh); // 需要持续搜索就要注释这行
                tv_discovery.setText("蓝牙设备搜索完成");
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) { // 配对状态变更
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    tv_discovery.setText("正在配对" + device.getName());
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    tv_discovery.setText("完成配对" + device.getName());
                    mHandler.postDelayed(mRefresh, 50);
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    tv_discovery.setText("取消配对" + device.getName());
                    refreshDevice(device, device.getBondState());
                }
            }
        }
    };

    // 刷新蓝牙设备列表
    private void refreshDevice(BluetoothDevice device, int state) {
        int i;
        for (i = 0; i < mDeviceList.size(); i++) {
            BlueDevice item = mDeviceList.get(i);
            if (item.address.equals(device.getAddress())) {
                item.state = state;
                mDeviceList.set(i, item);
                break;
            }
        }
        if (i >= mDeviceList.size()) {
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), device.getBondState()));
        }
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //cancelDiscovery();
        BlueDevice item = mDeviceList.get(position);
        // 根据设备地址获得远端的蓝牙设备对象
        BluetoothDevice device = mBluetooth.getRemoteDevice(item.address);
        Log.d(TAG, "getBondState=" + device.getBondState() + ", item.state=" + item.state);
        if (device.getBondState() == BluetoothDevice.BOND_NONE) { // 尚未配对
            boolean isSuccess = BluetoothUtil.createBond(device); // 创建配对信息
            if (isSuccess) {
                AssistStatic.showToast(BluetoothActivityTwo.this, "配对成功");
            }
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) { // 已经配对
            boolean isSucc = BluetoothUtil.removeBond(device); // 移除配对信息
            if (isSucc) {
                refreshDevice(device, BluetoothDevice.BOND_NONE);
            }
        }
    }
}
