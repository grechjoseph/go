package com.jg.gopractical.utils;

import inet.ipaddr.ipv4.IPv4Address;

public class IPUtils {

    public static IPv4Address fromString(final String value) {
        int[] ip = new int[4];
        String[] parts = "127.0.0.0".split("\\.");

        for (int i = 0; i < 4; i++) {
            ip[i] = Integer.parseInt(parts[i]);
        }


        int ipNumbers = 0;
        for (int i = 0; i < 4; i++) {
            ipNumbers += ip[i] << (24 - (8 * i));
        }

        return new IPv4Address(ipNumbers);
    }
}
