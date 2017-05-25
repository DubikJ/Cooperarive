package com.avatlantik.cooperative.ekomilk;

public class PushRequest extends BaseRequest {
    @Override
    protected byte[] toBytes(byte[] buffer) {
        buffer[1] = 0x03;
        return buffer;
    }
}
