package com.avatlantik.cooperative.ekomilk;

import com.avatlantik.cooperative.model.db.Milk;
import com.avatlantik.cooperative.util.BCDParser;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

class PrintRequest extends BaseRequest implements Serializable {

    private static final String PRINTER_CHARSET = "windows-1251";
    private static final int nameLengthMax = 45;
    private static final String nameEndPoint = ".";

    private String name;
    private Milk milk;

    PrintRequest(String name, Milk milk) {
        this.name = name.trim();
        if (this.name.length() > 50) {
            this.name = name.substring(0, 49);
        }
        this.milk = milk;
    }

    @Override
    protected byte[] toBytes(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.put(1, (byte) 0x02);
        byte[] nameBytes = name.getBytes(Charset.forName(PRINTER_CHARSET));
        for (int i = 0; i < nameBytes.length; i++) {
            if(i == nameLengthMax-1){
                byte[] nameEndPointBytes = nameEndPoint.getBytes(Charset.forName(PRINTER_CHARSET));
                byteBuffer.put(i + 2, nameEndPointBytes[0]);
            }
            byteBuffer.put(i + 2, nameBytes[i]);
        }

        byte[] dateByte = BCDParser.encodeInt(getArrayDate(LocalDate.now()));
        for (int i = 0; i < dateByte.length; i++) {
            byteBuffer.put(i + 47, dateByte[i]);
        }

        byte[] milkParams = BCDParser.encode(milk.toArray());
        for (int i = 0; i < milkParams.length; i++) {
            byteBuffer.put(i + 52, milkParams[i]);
        }
        return byteBuffer.array();
    }

    private int[] getArrayDate(LocalDate localDate){
        int[] arrayDate = new int[5];
        arrayDate[0] = localDate.getDayOfMonth();
        arrayDate[1] = localDate.getMonthOfYear();
        arrayDate[2] = localDate.getYear()-2000;
        arrayDate[3] = localDate.toDateTimeAtCurrentTime().getHourOfDay();
        arrayDate[4] = localDate.toDateTimeAtCurrentTime().getMinuteOfHour();

        return arrayDate;
    }
}
