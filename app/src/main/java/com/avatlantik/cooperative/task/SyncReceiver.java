package com.avatlantik.cooperative.task;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class SyncReceiver extends ResultReceiver {
    private Receiver mReceiver;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public SyncReceiver(Handler handler, Receiver mReceiver) {
        super(handler);
        this.mReceiver = mReceiver;
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
