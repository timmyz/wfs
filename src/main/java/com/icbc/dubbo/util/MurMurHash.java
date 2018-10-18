package com.icbc.dubbo.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MurMurHash {

    public static String hashHex(String key) {
        return null == key ? ""
                : String.format("%16s", Long.toString(hash(key), 16).substring(1)).replace(' ',
                        '0');
    }

    public static long hash(String key) {
        try {
            return hash(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException:" + key);
        }
    }

    public static long hash(byte[] key) {
        long hashValue = hash0(key);
        return hashValue < 0L ? hashValue : hashValue + Long.MIN_VALUE;
    }

    public static boolean withInRange(String[] groups, long hashValue, String routeFlag,
            boolean flag) {
        int l = groups.length;
        char groupFlag = groups[--l].charAt(0);
        if (null != routeFlag && flag ^ -1 < routeFlag.indexOf(groupFlag)) {
            return false;
        }
        while (1 < l) {
            if (withInRange(hashValue, groups[--l], groups[--l])) {
                return true;
            }
        }
        return false;
    }

    public static boolean withInRange(String group, long hashValue, String routeFlag,
            boolean flag) {
        return withInRange(group.split("-"), hashValue, routeFlag, flag);
    }

    public static boolean withInRange(long hashValue, String begin, String end) {
        return hashValue >= nParseLong(begin) && hashValue < nParseLong(end);
    }

    public static boolean withInRange(String beginA, String endA, String beginB, String endB) {
        long bA = nParseLong(beginA);
        long bB = nParseLong(beginB);
        long eA = nParseLong(endA);
        long eB = nParseLong(endB);
        return (bA < bB ? bB : bA) < (eA < eB ? eA : eB);
    }

    private static long hash0(byte[] key) {

        ByteBuffer buf = ByteBuffer.wrap(key);
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, do this first:
            // finish.position(8-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }

    public static long nParseLong(String s) throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }
        long result = 0L;
        int i = 0, max = s.length();
        int digit;
        if (max > 0) {
            while (i < 16) {
                result <<= 4;
                if (i < max) {
                    digit = Character.digit(s.charAt(i), 16);
                    if (digit < 0) {
                        throw new NumberFormatException(s);
                    }
                    result -= digit;
                }
                i++;
            }
        }
        return result;
    }

}
