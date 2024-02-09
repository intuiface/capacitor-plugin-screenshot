package com.intuiface.plugins.screenshot;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ScreenCaptureManager {

    private ScreenCaptureService mService;
    private Context mContext;
    private State currentState = State.UNBIND_SERVICE;

    /** Defines callbacks for service binding, passed to bindService() */
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to ScreenCaptureService, cast the IBinder and get ScreenCaptureService instance
            ScreenCaptureService.LocalBinder binder = (ScreenCaptureService.LocalBinder) service;
            mService = binder.getService();
            if (currentState == State.START_FOREGROUND) {
                mService.startForeground();
            } else {
                currentState = State.BIND_SERVICE;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    /**
     * An enum describing the possible states of a ScreenCaptureManager.
     */
    public enum State {
        BIND_SERVICE,
        START_FOREGROUND,
        END_FOREGROUND,
        UNBIND_SERVICE
    }

    public ScreenCaptureManager(Context context) {
        mContext = context;
        bindService();
    }

    private void bindService() {
        Intent intent = new Intent(mContext, ScreenCaptureService.class);
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void startForeground() {
        if (mService != null) {
            mService.startForeground();
        }
        currentState = State.START_FOREGROUND;
    }

    public void endForeground() {
        mService.endForeground();
        currentState = State.END_FOREGROUND;
    }

    public void unbindService() {
        mContext.unbindService(connection);
        currentState = State.UNBIND_SERVICE;
    }
}
