// IRemoteServer.aidl
package com.example.baseproject;
import com.example.baseproject.ICallback;

interface IRemoteServer {
    oneway void getItem(in ICallback callback);
}