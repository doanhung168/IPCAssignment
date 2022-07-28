package com.example.baseproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RemoteService extends Service {

    private static final String TAG = "RemoteService";
    private boolean mInit;
    private boolean mIsRun;
    private BlockingQueue<Item> mBlockingQueue;
    private Random mRandom;

    private IRemoteServer mRemoteServer = new IRemoteServer.Stub() {

        @Override
        public void getItem(ICallback callback) {
            try {
                Item item = mBlockingQueue.take();
                Log.i(TAG, "export item: " + item.mName);
                callback.onCallback(item);
            } catch (InterruptedException | RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (!mInit) {
            mBlockingQueue = new ArrayBlockingQueue<>(10);
            mRandom = new Random();
            generatorItem();
            mInit = true;
        }
    }

    private void generatorItem() {
        mIsRun = true;
        new Thread(() -> {
            while (mIsRun) {
                long id = System.currentTimeMillis();
                Item item = new Item();
                item.mId = id;
                item.mName = "Item " + id;
                item.mPrice = 2000.0 + mRandom.nextInt(1000);
                try {
                    mBlockingQueue.put(item);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sleep();
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return mRemoteServer.asBinder();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        mIsRun = false;
        mRemoteServer = null;
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
