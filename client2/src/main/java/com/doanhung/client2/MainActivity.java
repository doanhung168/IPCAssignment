package com.doanhung.client2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.doanhung.client2.databinding.ActivityMainBinding;
import com.example.baseproject.ICallback;
import com.example.baseproject.IRemoteServer;
import com.example.baseproject.Item;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding mBinding;

    private IRemoteServer mRemoteService;
    private boolean mBound;

    private List<Item> mItemList;
    private ItemAdapter mItemAdapter;

    private boolean mRequestEnable;


    private ICallback mGetItemCallback = new ICallback.Stub() {
        @Override
        public void onCallback(Item item) {
            runOnUiThread(() -> {
                mItemList.add(item);
                mItemAdapter.setData(mItemList);
            });
            Log.i(TAG, "client 2 receive item: " + item.mId);
            actionGetItem();
        }
    };


    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: ");
            mBound = true;
            mRemoteService = IRemoteServer.Stub.asInterface(service);
            mRequestEnable = true;
            actionGetItem();
        }

        @Override
        public void onBindingDied(ComponentName name) {
            mBound = false;
            mRemoteService = null;
            mRequestEnable = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mRemoteService = null;
            mRequestEnable = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.swGetItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                connectProducer();
            } else {
                disconnectProducer();
            }
        });

        setUpRcvItem();
    }

    private void setUpRcvItem() {
        mItemList = new ArrayList<>();
        mItemAdapter = new ItemAdapter(mItemList);
        mBinding.rcvItems.setLayoutManager(
                new LinearLayoutManager(MainActivity.this)
        );
        mBinding.rcvItems.setAdapter(mItemAdapter);
    }


    private void disconnectProducer() {
        if (mBound) {
            mRequestEnable = false;
            unbindService(mConnection);
        }
    }

    private void actionGetItem() {
        if(mRequestEnable) {
            try {
                mRemoteService.getItem(mGetItemCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    private void connectProducer() {
        Intent intent = new Intent();
        intent.setComponent(
                new ComponentName("com.example.baseproject", "com.example.baseproject.RemoteService")
        );
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
        if (mBound) {
            unbindService(mConnection);
        }
    }
}