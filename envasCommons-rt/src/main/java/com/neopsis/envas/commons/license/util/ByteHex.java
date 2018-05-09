/*
 * @(#)ByteHex.java   18.08.2016
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.envas.commons.license.util;

import java.io.ByteArrayOutputStream;

/**
 * Utility class for converting byte arrays to and from a Hex string
 *
 *  Robert Carnecky, Neopsis
 */
public final class ByteHex {

    private static final char[] hexs = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Convert a byte array into a printable format containing a
     * String of hexadecimal digit characters (two per byte).
     *
     * @param bytes Byte array representation
     * @return Hex string
     */
    public static String convert(byte bytes[]) {

        StringBuffer sb = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {

            sb.append(hexs[(bytes[i] >> 4) & 0x0f]);
            sb.append(hexs[bytes[i] & 0x0f]);
        }

        return (sb.toString());
    }

    /**
     * Convert a Hex String into the corresponding byte array by encoding
     * each two hexadecimal digits as a byte.
     *
     * @param hex Hexadecimal digits representation
     * @return byte Byte array
     */
    public static byte[] convert(String hex) throws IllegalArgumentException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (int i = 0; i < hex.length(); i += 2) {

            char c1 = hex.charAt(i);

            if ((i + 1) >= hex.length()) {
                throw new IllegalArgumentException();
            }

            char c2 = hex.charAt(i + 1);
            byte b  = 0;

            if ((c1 >= '0') && (c1 <= '9')) {
                b += ((c1 - '0') * 16);
            } else if ((c1 >= 'a') && (c1 <= 'f')) {
                b += ((c1 - 'a' + 10) * 16);
            } else if ((c1 >= 'A') && (c1 <= 'F')) {
                b += ((c1 - 'A' + 10) * 16);
            } else {
                throw new IllegalArgumentException();
            }

            if ((c2 >= '0') && (c2 <= '9')) {
                b += (c2 - '0');
            } else if ((c2 >= 'a') && (c2 <= 'f')) {
                b += (c2 - 'a' + 10);
            } else if ((c2 >= 'A') && (c2 <= 'F')) {
                b += (c2 - 'A' + 10);
            } else {
                throw new IllegalArgumentException();
            }

            baos.write(b);
        }

        return (baos.toByteArray());
    }
}
