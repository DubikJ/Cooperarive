package com.avatlantik.cooperative.ekomilk;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import com.avatlantik.cooperative.model.db.Milk;
import com.avatlantik.cooperative.model.db.MilkParam;
import com.avatlantik.cooperative.repository.DataRepository;
import com.avatlantik.cooperative.repository.DataRepositoryImpl;
import com.avatlantik.cooperative.service.SettingsService.ConnectionType;
import com.avatlantik.cooperative.service.SettingsService.DeviceType;
import com.avatlantik.cooperative.service.SettingsServiceImpl;
import com.avatlantik.cooperative.util.Consumer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static com.avatlantik.cooperative.common.Consts.ECOMILK_SYNC;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_AUTOMATIC_START;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_DEVICE_TYPE;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_START_ANALYSE;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_START_AUTO_ANALYSE;
import static com.avatlantik.cooperative.common.Consts.FLOWMETER_START_ANALYSE;
import static com.avatlantik.cooperative.common.Consts.MILK_ADDEDWATER;
import static com.avatlantik.cooperative.common.Consts.MILK_CONDUCTIVITY;
import static com.avatlantik.cooperative.common.Consts.MILK_DENCITY;
import static com.avatlantik.cooperative.common.Consts.MILK_FAT;
import static com.avatlantik.cooperative.common.Consts.MILK_FP;
import static com.avatlantik.cooperative.common.Consts.MILK_PROTEIN;
import static com.avatlantik.cooperative.common.Consts.MILK_SNF;
import static com.avatlantik.cooperative.common.Consts.MILK_VOLUME;

public abstract class EkoMilk {

    static final String EKOMILK_INIT_LOADER = "com.android.ekomilk.INIT_LOADER";
    static final String EKOMILK_LOAD_ACTION_ECOMILK = "com.android.ekomilk.LOAD_ECOMILK";
    static final String EKOMILK_LOAD_ACTION_FLOWMETER = "com.android.ekomilk.LOAD_FLOWMETER";
    static final String EKOMILK_CANCEL_ACTION = "com.android.ekomilk.CANCEL_ACTION";
    static final String EKOMILK_STOP_ACTION = "com.android.ekomilk.STOP_ACTION";
    static final String EKOMILK_PRINT_ACTION = "com.android.ekomilk.PRINT_ACTION";

    private static final double EKOMILK_EXEPTION = 0.09;
    protected static final String OK = "1";
    protected static final String PRINT_REQUEST = "printRequest";

    protected final static String TAGLOG = "EkoMilk";

    private static final int EKOMILK_DETECTOR_ID = 1;
    static final int EKOMILK_LOADER_ID = 2;

    protected Context context;
    Consumer<Milk> dataConsumer;
    LoaderManager loaderManager;

    public static EkoMilk via(ConnectionType connectionType, Context context) {
        if (connectionType == null) {
            throw new IllegalArgumentException("Connection type is null");
        }
        switch (connectionType) {
            case WIFI:
                return new WifiEkoMilk(context);
            case USB:
                return new UsbEkoMilk(context);
            default:
                throw new IllegalStateException("Connection type is undefined");
        }
    }



    EkoMilk(Context context) {
        this.context = context;
    }

    public void initLoaderManager(final LoaderManager loaderManager, Consumer<Milk> onData) {
        dataConsumer = onData;
        this.loaderManager = loaderManager;
        loaderManager.initLoader(EKOMILK_DETECTOR_ID, new Bundle(), getDetectorCallbacks(EKOMILK_INIT_LOADER)).forceLoad();
    }

    public void start(Consumer<Milk> onData, DeviceType deviceType) {
        String nameLoadAction = deviceType == DeviceType.ECOMILK ? EKOMILK_LOAD_ACTION_ECOMILK : EKOMILK_LOAD_ACTION_FLOWMETER;
        dataConsumer = onData;
        loaderManager.restartLoader(EKOMILK_DETECTOR_ID, new Bundle(), getDetectorCallbacks(nameLoadAction)).forceLoad();
    }

    public void print(String name, Milk milk) {
        Bundle bundle = new Bundle();
        bundle.putByteArray(PRINT_REQUEST, new PrintRequest(name, milk).toBytes());
        loaderManager.restartLoader(EKOMILK_DETECTOR_ID, bundle, getDetectorCallbacks(EKOMILK_PRINT_ACTION)).forceLoad();
    }

    public void cancel() {
        if (loaderManager == null) return;
        loaderManager.restartLoader(EKOMILK_DETECTOR_ID, new Bundle(), getDetectorCallbacks(EKOMILK_CANCEL_ACTION)).forceLoad();
        if (loaderManager.getLoader(EKOMILK_LOADER_ID) != null) {
            loaderManager.getLoader(EKOMILK_LOADER_ID).cancelLoad();
        }
    }

    public void stop() {
        if (loaderManager == null) return;
        loaderManager.restartLoader(EKOMILK_DETECTOR_ID, new Bundle(), getDetectorCallbacks(EKOMILK_STOP_ACTION)).forceLoad();
        if (loaderManager.getLoader(EKOMILK_DETECTOR_ID) != null) {
            loaderManager.getLoader(EKOMILK_DETECTOR_ID).cancelLoad();
        }
        if (loaderManager.getLoader(EKOMILK_LOADER_ID) != null) {
            loaderManager.getLoader(EKOMILK_LOADER_ID).cancelLoad();
        }
    }

    protected abstract EkoMilkDetectorCallbacks getDetectorCallbacks(String action);

    abstract class EkoMilkDetectorCallbacks<T> implements LoaderManager.LoaderCallbacks<T> {

        protected String action;
        protected Bundle bundle;

        EkoMilkDetectorCallbacks(String action) {
            this.action = action;
        }

        @Override
        public Loader<T> onCreateLoader(int id, Bundle args) {
            this.bundle = args;
            if (id == EKOMILK_DETECTOR_ID) {
                return getDetector();
            } else {
                return null;
            }
        }

        @Override
        public abstract void onLoadFinished(Loader<T> loader, T device);

        @Override
        public void onLoaderReset(Loader<T> loader) {
        }

        protected abstract EkoMilkDetector<T> getDetector();
    }

    abstract static class EkoMilkDetector<T> extends AsyncTaskLoader<T> {

        EkoMilkDetector(Context context) {
            super(context);
        }

        @Override
        public T loadInBackground() {
            T device;
            while ((device = scanDevice()) == null && !isLoadInBackgroundCanceled()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return device;
        }

        protected abstract T scanDevice();
    }

    abstract static class EkoMilkLoaderCallbacks<T> implements LoaderManager.LoaderCallbacks<MilkParam> {

        T device;
        Consumer<Milk> consumer;
        protected String action;

        EkoMilkLoaderCallbacks(T device, Consumer<Milk> consumer, String action) {
            this.device = device;
            this.consumer = consumer;
            this.action = action;
        }

        @Override
        public Loader<MilkParam> onCreateLoader(int id, Bundle args) {
            if (id == EKOMILK_LOADER_ID) {
                try {
                    return getLoader();
                } catch (IOException e) {
                    Log.e(TAGLOG, "Cannot create loader", e);
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<MilkParam> loader, MilkParam data) {
            consumer.apply(data);
        }

        @Override
        public void onLoaderReset(Loader<MilkParam> loader) {
        }

        protected abstract EkoMilkLoader<T> getLoader() throws IOException;
    }

    abstract static class EkoMilkLoader<T> extends AsyncTaskLoader<MilkParam> {

        protected final static String TAGLOG = "EkoMilkLoader";
        protected Context context;
        protected String action;
        T device;

        EkoMilkLoader(Context context, T device, String action) {
            super(context);
            this.context = context;
            this.device = device;
            this.action = action;
        }

        protected abstract int ekomilkResponseSize();

        protected abstract byte[] readDevice(int size) throws IOException;

        protected abstract int writeDevice(byte[] buffer) throws IOException;

        protected abstract double[] parseEkomilk(byte[] bytes);

        protected abstract void close() throws IOException;

        protected boolean isNullResponse(byte[] bytes) {
            for (byte oneByte : bytes) {
                if (oneByte != 0) return false;
            }
            return true;
        }

        protected boolean isErrorResponse(byte[] bytes) {
            return isNullResponse(Arrays.copyOf(bytes, bytes.length - 1)) && bytes[bytes.length - 1] == 0x09;
        }

        @Override
        public MilkParam loadInBackground() {
            byte buffer[];
            if(action == EKOMILK_INIT_LOADER){
                return null;
            }
            try {
                ScanRequest scanRequest = new ScanRequest(EKOMILK_START_ANALYSE);
                if(action == EKOMILK_LOAD_ACTION_FLOWMETER) {
                    scanRequest = new ScanRequest(FLOWMETER_START_ANALYSE);
                }else {
                    if (isUsingAutoStartEcomilk()){
                        scanRequest = new ScanRequest(EKOMILK_START_AUTO_ANALYSE);
                    }else {
                        scanRequest = new ScanRequest(EKOMILK_START_ANALYSE);
                    }
                }
                writeDevice(scanRequest.toBytes());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.d(TAGLOG, "Reading is interrupted");
                    return null;
                }
                byte[] readed;
                if (!OK.equals(new String(readed = readDevice(1), Charset.forName("UTF-8")))) {
                    Log.d(TAGLOG, "Acknowledge = " + new String(readed, Charset.forName("UTF-8")));
                    Log.d(TAGLOG, "Acknowledge = " + Integer.toBinaryString(readed[0]));
                    throw new IOException("No acknowledge response from the device on write");
                }
                while ((buffer = pull(ekomilkResponseSize())).length == 0 && !isLoadInBackgroundCanceled()) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.d(TAGLOG, "Reading is interrupted");
                        return null;
                    }
                }
            } catch (IOException e) {
                Log.e(TAGLOG, "Cannot communicate with the device", e);
                return null;
//            } finally {
//                try {
//                    close();
//                } catch (IOException e) {
//                    Log.e(TAGLOG, "Cannot close resources. Possible memory leaks.", e);
//                }
            }
            double[] values = parseEkomilk(buffer);

            if(values[8] == EKOMILK_EXEPTION){
                return null;
            }

            return convert(0, values, action);
        }

        private byte[] pull(int size) throws IOException {
            byte[] pullRequest = new PullRequest().toBytes();
            writeDevice(pullRequest);
            byte[] response = readDevice(size);
            if (isNullResponse(response)) {
                response = new byte[0];
            } else if (isErrorResponse(response)) {
                throw new IOException("Error occurs while reading from Ekomilk");
            }
            return response;
        }

        private MilkParam convert(int visitId, double[] params, String action) {
            MilkParam milkParam = MilkParam.builder()
                    .visitId(visitId)
                    .fat(params[0])
                    .snf(params[1])
                    .dencity(params[2])
                    .addedWater(params[3])
                    .fp(params[4])
                    .protein(params[5])
                    .conductivity(params[6])
                    .volume(params[7])
                    .build();

            writeMilkParamInCash(milkParam, action);

            return milkParam;
        }

        private void writeMilkParamInCash(MilkParam milkParam, String action) {

            SettingsServiceImpl settingsService = new SettingsServiceImpl(this.context);

            settingsService.saveData(MILK_FAT, String.valueOf(milkParam.getFat()));
            settingsService.saveData(MILK_SNF, String.valueOf(milkParam.getSnf()));
            settingsService.saveData(MILK_DENCITY, String.valueOf(milkParam.getDencity()));
            settingsService.saveData(MILK_ADDEDWATER, String.valueOf(milkParam.getAddedWater()));
            settingsService.saveData(MILK_FP, String.valueOf(milkParam.getFp()));
            settingsService.saveData(MILK_PROTEIN, String.valueOf(milkParam.getProtein()));
            settingsService.saveData(MILK_CONDUCTIVITY, String.valueOf(milkParam.getConductivity()));
            settingsService.saveData(MILK_VOLUME, String.valueOf(milkParam.getVolume()));
            settingsService.saveData(EKOMILK_DEVICE_TYPE, action == EKOMILK_LOAD_ACTION_FLOWMETER ?
                                                          DeviceType.FLOWMETER.name() :
                                                          DeviceType.ECOMILK.name());

            settingsService.saveData(ECOMILK_SYNC, String.valueOf(false));

        }

        private Boolean isUsingAutoStartEcomilk(){
            DataRepository dataRepository = new DataRepositoryImpl(context.getContentResolver());

            return Boolean.valueOf(dataRepository.getUserSetting(EKOMILK_AUTOMATIC_START));

        }
    }

}
