package com.ppsoclab.ppsoc3;

/**
 * Created by heiruwu on 5/8/15.
 */
public class ByteParse {
    public static String byteToString(byte b){
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    public static int sIN16FromByte(byte input){
        String temp = byteToString(input);
        return Integer.parseInt(temp,2);
    }

    public static int sIN16From2Byte(byte L,byte H){
        String temp = byteToString(H);
        temp += byteToString(L);
        if(temp.substring(0,1).equals("1")){
            return (-1*(65536 - Integer.parseInt(temp,2)));
        } else {
            return Integer.parseInt(temp,2);
        }
    }
}
