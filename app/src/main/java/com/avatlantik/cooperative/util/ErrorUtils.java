package com.avatlantik.cooperative.util;

import android.content.Context;
import android.content.res.Resources;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.model.APIError;

import static com.avatlantik.cooperative.common.Consts.STATUS_ERROR_SYNC;

public class ErrorUtils {

    private static Context context;

    public ErrorUtils(Context context) {
        this.context = context;
    }

    public static APIError parseErrorMessage(Exception exception) {
        APIError error = new APIError();

        Resources resources = context.getResources();

        String textMessage = resources.getString(R.string.sync_error);
        if ("canceled".equalsIgnoreCase(exception.getMessage())) {

            error.setStatusCode("canceled");
            error.setMessage(textMessage + " " + resources.getString(R.string.sync_cancel));

        }else if ("timeout".equalsIgnoreCase(exception.getMessage())) {

            error.setStatusCode("timeout");
            error.setMessage(textMessage + " " + resources.getString(R.string.sync_timeout));

        }else if ("failed to connect to localhost/127.0.0.1:80".equalsIgnoreCase(exception.getMessage())) {

            error.setStatusCode("failedconnect");
            error.setMessage(textMessage + " " + resources.getString(R.string.error_connection_server));

        }else {

            error.setStatusCode(String.valueOf(STATUS_ERROR_SYNC));
            error.setMessage(textMessage + " " + exception.toString());

        }

        return error;
    }

    public static APIError parseErrorCode(int codeExeption) {
        APIError error = new APIError();

        Resources resources = context.getResources();

        String textMessage = resources.getString(R.string.sync_error);
        error.setStatusCode(String.valueOf(codeExeption));
        switch (codeExeption){
            case 401:
                error.setMessage(textMessage + " " + resources.getString(R.string.auth_error));
                return error;
            case 404:
                error.setMessage(textMessage + " " + resources.getString(R.string.error_data_not_found));
                return error;
            case 500:
                error.setMessage(textMessage + " " + resources.getString(R.string.error_data_server));
                return error;

        }

        error.setMessage(textMessage + " " + resources.getString(R.string.error_retrieving_data));
        return error;
    }
}
