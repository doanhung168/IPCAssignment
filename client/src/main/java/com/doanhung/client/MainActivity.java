package com.doanhung.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.doanhung.client.databinding.ActivityMainBinding;
import com.example.baseproject.ICallback;
import com.example.baseproject.IRemoteServer;
import com.example.baseproject.Item;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding mBinding;

    private IRemoteServer mRemoteService;
    private boolean mBound;

    private ICallback mGetItemCallback = new ICallback.Stub() {
        @Override
        public void onCallback(Item item) {
            Log.i(TAG, "p1 onCallback: " + item.mName);
            actionGetItem();
        }
    };


    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: ");
            mBound = true;
            mRemoteService = IRemoteServer.Stub.asInterface(service);
        }

        @Override
        public void onBindingDied(ComponentName name) {
            mBound = false;
            mRemoteService = null;
            mGetItemCallback = null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mRemoteService = null;
            mGetItemCallback = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.btnConnect.setOnClickListener(v -> connectProducer());
        mBinding.btnGetItem.setOnClickListener(v -> actionGetItem());
        mBinding.btnDisconnect.setOnClickListener(v -> disconnectProducer());
    }


    private void disconnectProducer() {
        if (mBound) {
            unbindService(mConnection);
        }
    }

    private void actionGetItem() {
        if (mBound) {
            try {
                mRemoteService.getItem(mGetItemCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(
                    MainActivity.this,
                    "Need connect producer in advance",
                    Toast.LENGTH_SHORT
            ).show();
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