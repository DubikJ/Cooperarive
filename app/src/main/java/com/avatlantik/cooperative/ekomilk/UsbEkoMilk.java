package com.avatlantik.cooperative.ekomilk;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import com.avatlantik.cooperative.model.db.Milk;
import com.avatlantik.cooperative.model.db.MilkParam;
import com.avatlantik.cooperative.util.BCDParser;
import com.avatlantik.cooperative.util.Consumer;
import com.hoho.android.usbserial.driver.FtdiSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;

class UsbEkoMilk extends EkoMilk {

    private BroadcastReceiver receiver;

    UsbEkoMilk(final Context context) {
        super(context);
        receiver = new EkomilkReceiver();
        context.registerReceiver(receiver, usbActions());
    }

    @Override
    public void stop() {
        super.stop();
        context.unregisterReceiver(receiver);
    }

    @Override
    protected EkoMilkDetectorCallbacks getDetectorCallbacks(String action) {
        return new UsbEkomilkDetectorCallbacks(action);
    }

    private IntentFilter usbActions() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EKOMILK_LOAD_ACTION_ECOMILK);
        intentFilter.addAction(EKOMILK_LOAD_ACTION_FLOWMETER);
        intentFilter.addAction(EKOMILK_PRINT_ACTION);
        return intentFilter;

    }

    private class UsbEkomilkDetectorCallbacks extends EkoMilkDetectorCallbacks<UsbSerialDriver> {

        UsbEkomilkDetectorCallbacks(String action) {
            super(action);
        }

        @Override
        public void onLoadFinished(Loader<UsbSerialDriver> loader, UsbSerialDriver device) {
            UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            Intent intent = new Intent(action);
            if (bundle != null)
                intent.putExtras(bundle);
            if (device != null) {
                manager.requestPermission(
                        device.getDevice(),
                        PendingIntent.getBroadcast(context, 0, intent, 0));
            }
        }

        @Override
        protected UsbDetector getDetector() {
            return new UsbDetector(context);
        }
    }

    private static class UsbDetector extends EkoMilkDetector<UsbSerialDriver> {

        private UsbManager manager;

        private UsbDetector(Context context) {
            super(context);
            manager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        }

        @Override
        protected UsbSerialDriver scanDevice() {
            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
            if (availableDrivers != null && availableDrivers.size() > 0) {
                return availableDrivers.get(0);
            } else {
                return null;
            }
        }
    }

    private class UsbLoaderCallbacks extends EkoMilkLoaderCallbacks<UsbDevice> {

        UsbLoaderCallbacks(UsbDevice device, Consumer<Milk> consumer, String action) {
            super(device, consumer, action);
        }

        @Override
        protected EkoMilkLoader<UsbDevice> getLoader() throws IOException {
            return new UsbLoader(context, device, action);
        }
    }

    private static class UsbLoader extends EkoMilkLoader<UsbDevice> {

        private UsbManager manager;
        private EkoMilkConnector connector;

        private UsbLoader(Context context, UsbDevice usbDevice, String action) throws IOException {
            super(context, usbDevice, action);
            manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            connector = new EkoMilkConnector(manager, usbDevice);
        }

        @Override
        protected int ekomilkResponseSize() {
            return 18;
        }

        @Override
        protected byte[] readDevice(int size) throws IOException {
            byte[] buffer = new byte[16384];
            connector.read(buffer, 500);
            byte[] result = new byte[size];
            System.arraycopy(buffer, 0, result, 0, size);
            return result;
        }

        @Override
        protected int writeDevice(byte[] buffer) throws IOException {
            return connector.write(buffer, 500);
        }

        @Override
        protected double[] parseEkomilk(byte[] bytes) {
            return BCDParser.decode(bytes);
        }

        protected void close() throws IOException {
            if (connector != null)
                connector.close();
        }
    }

    private class EkomilkReceiver extends BroadcastReceiver {
        public EkomilkReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case EKOMILK_INIT_LOADER:
                    break;
                case EKOMILK_LOAD_ACTION_ECOMILK:
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {
                                Loader<MilkParam> milkParamLoader = loaderManager
                                        .initLoader(EKOMILK_LOADER_ID, new Bundle(), new UsbLoaderCallbacks(device, dataConsumer, EKOMILK_LOAD_ACTION_ECOMILK));
                                milkParamLoader.forceLoad();
                            }
                        } else {
                            Log.d(TAGLOG, "Permission denied for Ekomilk(get analysis to USB)");
                        }
                    }
                    break;
                case EKOMILK_LOAD_ACTION_FLOWMETER:
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {
                                Loader<MilkParam> milkParamLoader = loaderManager
                                        .initLoader(EKOMILK_LOADER_ID, new Bundle(), new UsbLoaderCallbacks(device, dataConsumer, EKOMILK_LOAD_ACTION_FLOWMETER));
                                milkParamLoader.forceLoad();
                            }
                        } else {
                            Log.d(TAGLOG, "Permission denied for Ekomilk(get analysis to USB)");
                        }
                    }
                    break;
                case EKOMILK_PRINT_ACTION:
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            Bundle bundle = intent.getExtras();
                            EkoMilkConnector connector = null;
                            try {
                                connector = new EkoMilkConnector(
                                        (UsbManager) context.getSystemService(Context.USB_SERVICE),
                                        device);
                                connector.write(bundle.getByteArray(PRINT_REQUEST), 500);
                            } catch (IOException e) {
                                Log.e(TAGLOG, "Unable to send print request to Ekomilk", e);
//                            } finally {
//                                if (connector != null)
//                                    try {
//                                        connector.close();
//                                    } catch (IOException e) {
//                                        Log.e(TAGLOG, "Cannot close usb connector on printing", e);
//                                    }
                            }
                        } else {
                            Log.d(TAGLOG, "Permission denied for Ekomilk(print to USB)");
                        }
                    }
                    break;
                case EKOMILK_CANCEL_ACTION:
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            EkoMilkConnector connector = null;
                            try {
                                connector = new EkoMilkConnector(
                                        (UsbManager) context.getSystemService(Context.USB_SERVICE),
                                        device);
                                connector.write(new CancelRequest().toBytes(), 500);
                            } catch (IOException e) {
                                Log.e(TAGLOG, "Unable to send cancel request to Ekomilk", e);
                            }
                        } else {
                            Log.d(TAGLOG, "Permission denied for Ekomilk(cancel to USB)");
                        }
                    }
                    break;
                case EKOMILK_STOP_ACTION:
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        EkoMilkConnector connector = null;
                        try {
                            connector = new EkoMilkConnector(
                                    (UsbManager) context.getSystemService(Context.USB_SERVICE),
                                    device);
                            if (connector != null)
                                    try {
                                        connector.close();
                                    } catch (IOException e) {
                                        Log.e(TAGLOG, "Cannot close usb connector on printing", e);
                                   }
                        } catch (IOException e) {
                            Log.e(TAGLOG, "Cannot close usb connector to Ekomilk", e);
                        }
                    }
            }
        }
    }

    private static class EkoMilkConnector {
        private UsbSerialPort port;
        private UsbDeviceConnection connection;

        EkoMilkConnector(UsbManager manager, UsbDevice device) throws IOException {
            connection = manager.openDevice(device);
            if (connection != null) {
                FtdiSerialDriver ftdiSerialDriver = new FtdiSerialDriver(device);
                port = ftdiSerialDriver.getPorts().get(0);
                port.open(connection);
                port.setParameters(1200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_EVEN);
            } else {
                throw new IOException("Cannot open connection to EkoMilk");
            }
        }

        int read(byte[] buffer, int timeout) throws IOException {
            return port.read(buffer, timeout);
        }

        int write(byte[] buffer, int timeout) throws IOException {
            return port.write(buffer, timeout);
        }

        void close() throws IOException {
            if (port != null)
                port.close();
            if (connection != null)
                connection.close();
        }
    }
}
