package com.neopsis.envas.commons.license.util;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.neopsis.envas.commons.util.NvLog;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JulianDateReader implements ObjectReader<Object> {

    public static final JulianDateReader INSTANCE = new JulianDateReader();
    static final        DateFormat       df       = new SimpleDateFormat(LicenseUtils.DATE_FORMAT);

    @Override
    public Object readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {

        Long retVal = null;
        try {
            String value = jsonReader.readString();
            if (value.equals("never")) {
                retVal = Long.MAX_VALUE;
            } else {
                Date dat = null;
                try {
                    dat = df.parse(value);
                } catch (ParseException e) {
                    return null;
                }
                retVal = dat.getTime();
            }
        } catch (NumberFormatException e) {
            NvLog.error("JulianDateReader: error parsing " + fieldName.toString() + " (" + fieldType.getTypeName() + ") - " + e.getMessage());
        }

        return retVal;
    }
}
