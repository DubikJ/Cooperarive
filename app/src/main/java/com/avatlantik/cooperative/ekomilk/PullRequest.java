package com.avatlantik.cooperative.ekomilk;

public class PullRequest extends BaseRequest {
    @Override
    protected byte[] toBytes(byte[] buffer) {
        buffer[1] = 0x05;
        return buffer;
    }
}
