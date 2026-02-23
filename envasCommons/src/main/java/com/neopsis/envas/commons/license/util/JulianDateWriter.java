package com.neopsis.envas.commons.license.util;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * JulianDateWriter is a custom implementation of the ObjectWriter interface that
 * formats and writes objects as JSON strings. It specifically handles long values
 * interpreted as dates, formatting them according to a specified date format.
 * <p>
 * If the input is the maximum value of long (`Long.MAX_VALUE`), it writes the string
 * "never". If the input is another long value, it formats the value as a date.
 * For unsupported data types, it writes an error message including the class name
 * and the field name.
 */
public class JulianDateWriter implements ObjectWriter<Object> {

    public static final JulianDateWriter INSTANCE = new JulianDateWriter();
    static final        DateFormat       df       = new SimpleDateFormat(LicenseUtils.DATE_FORMAT);

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {

        if (fieldType.getTypeName().equalsIgnoreCase("long")) {
            Long value = (Long) object;
            if (value == Long.MAX_VALUE) {
                jsonWriter.writeString("never");
            } else {
                jsonWriter.writeString(df.format(new Date(value)));
            }
        } else {
            jsonWriter.writeString("JulianDateWriter: unable to write " + object.getClass().getSimpleName() + "." + fieldName.toString());
        }
    }
}
