package com.jg.gopractical.utils;

import inet.ipaddr.ipv4.IPv4Address;

public class IPUtils {

    public static IPv4Address fromString(final String value) {
        return new IPv4Address(getIpNumbers(value));
    }

    public static boolean isInRange(final IPv4Address value, final IPv4Address lower, final IPv4Address upper) {
        return getIpNumbers(value.toString()) >= getIpNumbers(lower.toString())
                && getIpNumbers(value.toString()) <= getIpNumbers(upper.toString());
    }

    public static int getDifference(final IPv4Address upper, final IPv4Address lower) {
        return getIpNumbers(upper.toString()) - getIpNumbers(lower.toString());
    }

    public static int getIpNumbers(final String value) {
        int[] ip = new int[4];
        String[] parts = value.split("\\.");

        for (int i = 0; i < 4; i++) {
            ip[i] = Integer.parseInt(parts[i]);
        }


        int ipNumbers = 0;
        for (int i = 0; i < 4; i++) {
            ipNumbers += ip[i] << (24 - (8 * i));
        }
        return ipNumbers;
    }
}
