package com.avatlantik.cooperative.ekomilk;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.avatlantik.cooperative.model.db.Milk;
import com.avatlantik.cooperative.util.Consumer;
import com.avatlantik.cooperative.util.PropertyUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

class WifiEkoMilk extends EkoMilk {
    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static SocketConfig socketConfig;

    WifiEkoMilk(Context context) {
        super(context);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Properties properties = PropertyUtils.getProperties(APPLICATION_PROPERTIES, context);
            socketConfig = new SocketConfig();
            socketConfig.server = properties.getProperty("black-box.server");
            socketConfig.port = Integer.parseInt(properties.getProperty("black-box.port"));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read auth properties", e);
        }
    }

    @Override
    protected EkoMilkDetectorCallbacks getDetectorCallbacks(String action) {
        return new WiFiEcomilkDetectorCallbacks(action);
    }

    private class WiFiEcomilkDetectorCallbacks extends EkoMilkDetectorCallbacks<Socket> {

        WiFiEcomilkDetectorCallbacks(String action) {
            super(action);
        }

        @Override
        public void onLoadFinished(Loader<Socket> loader, Socket socket) {
            switch (action) {
                case EKOMILK_INIT_LOADER:
                    break;
                case EKOMILK_LOAD_ACTION_ECOMILK:
                    loaderManager
                            .restartLoader(EKOMILK_LOADER_ID, new Bundle(), new WiFiLoaderCallbacks(socket, dataConsumer, action))
                            .forceLoad();
                    break;
                case EKOMILK_LOAD_ACTION_FLOWMETER:
                    loaderManager
                            .restartLoader(EKOMILK_LOADER_ID, new Bundle(), new WiFiLoaderCallbacks(socket, dataConsumer, action))
                            .forceLoad();
                    break;
                case EKOMILK_PRINT_ACTION:
                    try {
                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                        outputStream.write(bundle.getByteArray(PRINT_REQUEST));
                    } catch (IOException e) {
                        Log.e(TAGLOG, "Cannot write to socket for printing");
//                    } finally {
//                        if (socket.isConnected()) try {
//                            socket.close();
//                        } catch (IOException e) {
//                            Log.e(TAGLOG, "Cannot close socket on printing", e);
//                        }
                    }
                    break;
                case EKOMILK_CANCEL_ACTION:
                    try {
                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                        outputStream.write(new CancelRequest().toBytes());
                    } catch (IOException e) {
                        Log.e(TAGLOG, "Cannot write to socket for cancel");
                    }
                    break;
                case EKOMILK_STOP_ACTION:
                    if (socket.isConnected()) try {
                            socket.close();
                        } catch (IOException e) {
                        Log.e(TAGLOG, "Cannot close socket on printing", e);
                    }
                    break;
            }
        }

        @Override
        protected EkoMilkDetector<Socket> getDetector() {
            return new WiFiSocketDetector(context);
        }
    }

    private static class WiFiSocketDetector extends EkoMilkDetector<Socket> {
        WiFiSocketDetector(Context context) {
            super(context);
        }

        @Override
        protected Socket scanDevice() {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(socketConfig.server, socketConfig.port), 500);
                return socket;
            } catch (IOException e) {
                Log.e(TAGLOG, e.toString());
                return null;
            }
        }
    }

    private class WiFiLoaderCallbacks extends EkoMilkLoaderCallbacks<Socket> {
        WiFiLoaderCallbacks(Socket socket, Consumer<Milk> consumer, String action) {
            super(socket, consumer, action);
        }

        @Override
        protected EkoMilkLoader<Socket> getLoader() throws IOException {
            return new WiFiSocketLoader(context, device, action);
        }
    }

    private static class WiFiSocketLoader extends EkoMilkLoader<Socket> {

        private Socket socket;

        WiFiSocketLoader(Context context, Socket device, String action) {
            super(context, device, action);
            this.socket = device;
            this.action = action;
        }

        @Override
        protected int ekomilkResponseSize() {
            return 18 * 2;
        }

        @Override
        protected byte[] readDevice(int size) throws IOException {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            byte[] buffer = new byte[size];
            input.read(buffer);
            return buffer;
        }

        @Override
        protected int writeDevice(byte[] buffer) throws IOException {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(buffer);
            return 0;
        }

        @Override
        protected double[] parseEkomilk(byte[] bytes) {
            double[] values = new double[ekomilkResponseSize() / 4];
            for (int i = 0; i < bytes.length; i += 4) {
                values[i / 4] = asciiToInt(bytes[i]) * 10 + asciiToInt(bytes[i + 1])
                        + (asciiToInt(bytes[i + 2]) * 10 + asciiToInt(bytes[i + 3])) / 100d;
            }
            return values;
        }

        @Override
        protected boolean isNullResponse(byte[] bytes) {
            for (byte oneByte : bytes) {
                if (asciiToInt(oneByte) != 0) return false;
            }
            return true;
        }

        private int asciiToInt(byte ascii) {
            return Character.getNumericValue((int) ascii);
        }

        @Override
        protected void close() throws IOException {
            if (socket.isConnected()) socket.close();
        }
    }

    private static class SocketConfig {
        String server;
        int port;
    }
}
