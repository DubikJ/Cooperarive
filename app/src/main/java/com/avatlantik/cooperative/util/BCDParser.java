package com.avatlantik.cooperative.util;

import android.util.Log;

import static com.avatlantik.cooperative.common.Consts.TAGLOG;

public class BCDParser {

    public static void main(String[] args) {
        System.out.println(String.format("%8s", Integer.toBinaryString(toBcd(12) & 0xFF)).replace(' ', '0'));
    }

    public static double[] decode(byte[] bytes) {
        double[] result = new double[bytes.length / 2];
        for (int i = 0; i < bytes.length; i += 2) {
            Log.d(TAGLOG, "Byte1 = " + String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0')
                    + ", Byte2 = " + String.format("%8s", Integer.toBinaryString(bytes[i + 1] & 0xFF)).replace(' ', '0'));

            int firstNumber = fromBcd(bytes[i]);
            int secondNumber = fromBcd(bytes[i + 1]);
            Log.d(TAGLOG, "First = " + firstNumber + ", Second = " + secondNumber);
            result[i / 2] = (double) firstNumber + (double) secondNumber / 100d;
        }
        return result;
    }

    public static byte[] encode(double[] numbers) {
        byte[] result = new byte[numbers.length * 2];
        for (int i = 0; i < numbers.length; i++) {
            double number = numbers[i];
            int firstNumber = ((int) number) % 100;
            int secondNumber = ((int) (number * 100)) % 100;
            Log.d(TAGLOG, "First = " + firstNumber + ", Second = " + secondNumber);


            result[i * 2] = toBcd(firstNumber);
            result[i * 2 + 1] = toBcd(secondNumber);
            Log.d(TAGLOG, "Byte1 = " + String.format("%8s", Integer.toBinaryString(result[i * 2] & 0xFF)).replace(' ', '0')
                    + ", Byte2 = " + String.format("%8s", Integer.toBinaryString(result[i * 2 + 1] & 0xFF)).replace(' ', '0'));
        }
        return result;
    }

    public static byte[] encodeInt(int[] numbers) {
        byte[] result = new byte[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            int number = numbers[i];
            Log.d(TAGLOG, "number = " + number);
            result[i] = toBcd(number);
            Log.d(TAGLOG, "Byte = " + String.format("%8s", Integer.toBinaryString(result[i] & 0xFF)).replace(' ', '0'));
        }
        return result;
    }

    private static int fromBcd(byte bcd) {
        return ((bcd >> 4) & 0x0F) * 10 + (bcd & 0x0F);
    }

    private static byte toBcd(int number) {
        return (byte) (((byte) ((number / 10) << 4)) + ((byte) (number % 10)));
    }
}
