package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Bluetooth {
    //static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //static final UUID mUUID = UUID.randomUUID();
    private BluetoothDevice telescope = null;
    public BluetoothAdapter btAdapter;
    private static final String TAG = "Bluetooth";
    private int mState;
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private final Handler mHandler;
    private int mNewState;

    public Bluetooth(Context ct, Handler mHandler){
        this.mHandler = mHandler;
        //btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothManager bluetoothManager = ct.getSystemService(BluetoothManager.class);
        btAdapter =  bluetoothManager.getAdapter();
        mNewState = mState;
    }

    @SuppressLint("MissingPermission")
    public void btScan(){
        for (BluetoothDevice item : btAdapter.getBondedDevices()) {
            System.out.println(item.getName() + " " + item.getAddress());
            if ("Telescope".equals(item.getName())) {
                telescope = btAdapter.getRemoteDevice(item.getAddress());
            }
        }
    }

    public void con_telescope(){
        start();
        btScan();
        if (telescope != null) {
            connect(telescope, true);
        }else {
            Log.d(TAG, "telescope BluetoothDevice NULL");
        }
    }

    public void disconect(){
        stop();
    }


    @SuppressLint("MissingPermission")
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();
        Message msg = Message.obtain();
        msg.what = Constants.MESSAGE_DEVICE_NAME;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.obj = bundle;
        mHandler.sendMessage(msg);
        updateUserInterfaceTitle();
    }

    /**
     * Update UI title according to the current state of the chat connection
     */
    private synchronized void updateUserInterfaceTitle() {
        mState = getState();
        Log.d(TAG, "updateUserInterfaceTitle() " + mNewState + " -> " + mState);
        mNewState = mState;
        Message message = Message.obtain(mHandler, Constants.MESSAGE_STATE_CHANGE, mNewState, -1);
        message.sendToTarget();

    }

    public synchronized int getState() {
        return mState;
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see Bluetooth.ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        Bluetooth.ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        mState = STATE_NONE;
        updateUserInterfaceTitle();
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new Bluetooth.ConnectThread(device, secure);
        mConnectThread.start();
        updateUserInterfaceTitle();
    }

    public synchronized void start() {
        Log.d(TAG, "start");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        updateUserInterfaceTitle();
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        mState = STATE_NONE;
        updateUserInterfaceTitle();
        Bluetooth.this.start();
    }

    private void connectionLost() {
        mState = STATE_NONE;
        updateUserInterfaceTitle();
        Bluetooth.this.start();
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final String mSocketType;

        @SuppressLint("MissingPermission")
        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";
            try {
                tmp = device.createRfcommSocketToServiceRecord(mUUID);
                //tmp = device.createInsecureRfcommSocketToServiceRecord(mUUID);
                mState = STATE_CONNECTING;
            } catch (Exception e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);
            btAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (Exception e) {
                try {
                    mmSocket.close();
                } catch (Exception e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }
            synchronized (Bluetooth.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (Exception e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                mState = STATE_CONNECTED;
            } catch (Exception e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[2048];
            StringBuilder messageString = new StringBuilder();
            int bytes;
            while (mState == STATE_CONNECTED) {
                try {
                    if (mmInStream != null) {
                        bytes = mmInStream.read(buffer);
                        if (bytes != -1) {
                            for (int i = 0; i < bytes; i++) {
                                char c = (char) buffer[i];
                                if (c == '\r') {
                                    Message message = Message.obtain(mHandler, Constants.MESSAGE_READ, bytes, -1, messageString);
                                    message.sendToTarget();
                                    messageString = new StringBuilder();
                                } else if (c != '\n') {
                                    messageString.append(c);
                                }
                            }
                        }
                    }
                } catch (IOException e){
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                } catch (Exception e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                Message message = Message.obtain(mHandler, Constants.MESSAGE_WRITE, -1, -1, buffer);
                message.sendToTarget();
            } catch (Exception e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (Exception e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
