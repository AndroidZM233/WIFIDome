package com.speedata.wifidome;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.serialport.SerialPort;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.speedata.wifidome.adapter.MainActAdapter;
import com.speedata.wifidome.bean.WifiBean;
import com.speedata.wifidome.utils.ByteUtils;
import com.speedata.wifidome.utils.WifiDeviceControl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {

    private RecyclerView mRvContent;
    private MainActAdapter mAdapter;
    private List<WifiBean> datas;
    private SerialPort serialPort;
    private ReadThread readThread;
    private Button mBtn1;
    private Button mBtn2;
    private WifiDeviceControl wifiDeviceControl;
    private Button mBtn3;
    private int fd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initRV();

    }

    private void openSerialPort() {
        serialPort = new SerialPort();
        try {
            serialPort.OpenSerial("/dev/ttyMT2", 115200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSerialPort() {
        serialPort.CloseSerial(fd);
    }

    private void initRV() {
        datas = new ArrayList<>();
        mAdapter = new MainActAdapter(R.layout.rv_title, datas);
        mRvContent.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        layoutManager.setReverseLayout(true);//列表翻转
        mRvContent.setLayoutManager(layoutManager);
        mRvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    private void initView() {
        mRvContent = findViewById(R.id.rv_content);
        mBtn1 = findViewById(R.id.btn1);
        mBtn1.setOnClickListener(this);
        mBtn2 = findViewById(R.id.btn2);
        mBtn2.setOnClickListener(this);
        mBtn3 = findViewById(R.id.btn3);
        mBtn3.setOnClickListener(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                try {
                    wifiDeviceControl = new WifiDeviceControl(WifiDeviceControl.PowerType.MAIN_AND_EXPAND, new int[]{119, 7});
                    wifiDeviceControl.PowerOnDevice();

                    openSerialPort();

                    Toast.makeText(this, "上电成功", Toast.LENGTH_SHORT).show();
                    readThread = new ReadThread();
                    readThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "上电失败，" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn2:
                try {
                    wifiDeviceControl = new WifiDeviceControl(WifiDeviceControl.PowerType.MAIN_AND_EXPAND, new int[]{85, 7});
                    wifiDeviceControl.PowerOnDevice();

                    openSerialPort();
                    Toast.makeText(this, "上电成功", Toast.LENGTH_SHORT).show();
                    readThread = new ReadThread();
                    readThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "上电失败，" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn3:
                close();
                break;
        }
    }

    private void close() {
        try {
            wifiDeviceControl.PowerOffDevice();
            closeSerialPort();
            readThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                try {
                    fd = serialPort.getFd();
                    if (fd == -1) {
                        readThread.interrupt();
                        return;
                    }
                    byte[] bytes = serialPort.ReadSerial(fd, 49);

                    if (bytes != null) {
                        String factory = ByteUtils.toAsciiString(bytes);
                        String[] split = factory.split("\\|");
                        if (split.length == 6) {
                            WifiBean wifiBean = new WifiBean();
                            wifiBean.setYuanMAC(split[0]);
                            wifiBean.setMuMAC(split[1]);
                            wifiBean.setZhenZhu(split[2]);
                            wifiBean.setZhenZi(split[3]);
                            wifiBean.setXinDao(split[4]);
                            wifiBean.setXinHao(split[5]);
                            datas.add(wifiBean);
                            handler.sendMessage(handler.obtainMessage());
                        }

                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyDataSetChanged();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiDeviceControl != null) {
            close();
        }
    }
}
