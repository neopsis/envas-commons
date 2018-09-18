/*
 * @(#)JulianDateCodec.java   10.10.2017
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.envas.commons.license.util;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;

import java.lang.reflect.Type;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Serializes and deserializes the long (julian) date.
 *
 */
public class JulianDateCodec implements ObjectSerializer, ObjectDeserializer {

    DateFormat df = new SimpleDateFormat(LicenseUtils.DATE_FORMAT);

    /**
     * Generates the long value from the date string. Sets Long.MAX_VALUE
     * for dooms day.
     *
     * @param parser      JSON parser
     * @param type        type (Class) of the object
     * @param fieldName   field name in case of beans
     * @param <T>         returning type (long)
     *
     * @return  deserialized object
     */
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {

        Long       longObject;
        DateFormat df    = new SimpleDateFormat(LicenseUtils.DATE_FORMAT);
        String     value = parser.parseObject(String.class);

        if (value.equals("never")) {
            longObject = Long.MAX_VALUE;
        } else {

            Date dat = null;

            try {
                dat = df.parse(value);
            } catch (ParseException e) {
                return null;
            }

            longObject = dat.getTime();
        }

        return (T) longObject;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

    /**
     * Write the long (julian) date as string
     *
     * @param serializer    JSON serializer
     * @param object        serialized object
     * @param fieldName     field name in case of beans
     * @param fieldType     field type (class)
     * @param features      optinal features
     *
     * @throws IOException
     */
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {

        SerializeWriter out   = serializer.out;
        long            value = (long) object;

        if (value == Long.MAX_VALUE) {
            out.writeString("never");
        } else {
            out.writeString(df.format(new Date(value)));
        }
    }
}
