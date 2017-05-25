package com.avatlantik.cooperative.ekomilk;

abstract class BaseRequest {

    protected abstract byte[] toBytes(byte[] buffer);

    byte[] toBytes() {
        byte[] buffer = new byte[71];
        buffer[0] = 0x08;
        buffer[70] = 0x09;
        return toBytes(buffer);
    }

}
