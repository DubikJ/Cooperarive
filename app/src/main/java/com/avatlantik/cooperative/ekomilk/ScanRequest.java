package com.avatlantik.cooperative.ekomilk;

import static com.avatlantik.cooperative.common.Consts.EKOMILK_START_AUTO_ANALYSE;
import static com.avatlantik.cooperative.common.Consts.FLOWMETER_START_ANALYSE;

public class ScanRequest extends BaseRequest {

    private String typeCommand;

    public ScanRequest(String typeCommand) {
        this.typeCommand = typeCommand;
    }

    public byte[] toBytes(byte[] buffer) {
        switch (typeCommand){
            case EKOMILK_START_AUTO_ANALYSE :
                buffer[1] = 0x06;
                break;
            case FLOWMETER_START_ANALYSE :
                buffer[1] = 0x03;
                break;
            default:
                buffer[1] = 0x01;
        }
        return buffer;
    }
}
