/*
 * @(#)NvLicenseFeature.java   06.10.2017
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */


package com.neopsis.envas.commons.license;

import com.alibaba.fastjson2.annotation.JSONField;
import com.neopsis.envas.commons.license.util.JulianDateReader;
import com.neopsis.envas.commons.license.util.JulianDateWriter;
import com.tridium.sys.license.LicenseUtil;
import niagara.license.Feature;
import niagara.license.FeatureLicenseExpiredException;
import niagara.license.FeatureNotLicensedException;
import niagara.sys.Clock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Tridium license feature interface implementation.
 *
 */
public class NvLicenseFeature implements Feature, Comparable<NvLicenseFeature> {

    @JSONField(name = "vendor")
    String                  vendorName  = null;
    @JSONField(name = "feature")
    String                  featureName = null;
    @JSONField(
            name = "expiration",
            serializeUsing   = JulianDateWriter.class,
            deserializeUsing = JulianDateReader.class
    )
    long                    expiration;
    @JSONField(name = "options")
    HashMap<String, Object> options     = null;

    /**
     * Default constructor for reflection tools
     */
    public NvLicenseFeature() {
        options = new HashMap<>();
    }

    /**
     * Constructor without expiration and options
     *
     * @param vendorName  vendorName name
     * @param featureName feature name
     */
    public NvLicenseFeature(final String vendorName, final String featureName) {

        this.vendorName  = vendorName;
        this.featureName = featureName;
    }

    /**
     * Constructor without options
     *
     * @param vendorName  vendorName name
     * @param featureName feature name
     * @param expDate     expiration date
     */
    public NvLicenseFeature(final String vendorName, final String featureName, final long expDate) {

        this.vendorName  = vendorName;
        this.featureName = featureName;
        this.expiration  = expDate;
    }

    /**
     * Constructor with options
     *
     * @param vendorName  vendorName name
     * @param featureName feature name
     * @param expDate     expiration date
     * @param options     options map
     */
    public NvLicenseFeature(final String vendorName, final String featureName, final long expDate,
                            HashMap<String, Object> options) {

        this.vendorName  = vendorName;
        this.featureName = featureName;
        this.expiration  = expDate;
        this.options     = options;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getters and Setters

    /// ////////////////////////////////////////////////////////////////////////////////////////
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setExpiration(long expDate) {
        this.expiration = expDate;
    }

    @JSONField(serialize = false, deserialize = false)
    public void setExpirationDate(Date expDate) {
        this.expiration = expDate.getTime();
    }

    @JSONField(serialize = false, deserialize = false)
    public Date getExpirationDate() {
        return new Date(expiration);
    }

    public HashMap<String, Object> getOptions() {
        return options;
    }

    public void setOptions(HashMap<String, Object> options) {
        this.options = options;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isExpired(long expDate) {
        return this.expiration < expDate;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Options interface
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Add a featureName option
     *
     * @param key   option key
     * @param value option value
     */
    public void addOption(String key, Object value) {
        addOption(key, value, null);
    }

    /**
     * Add a featureName option
     *
     * @param key   option key
     * @param value option value
     */
    public void addOption(String key, Object value, Object defaultValue) {

        if ((value == null) && (defaultValue == null)) {
            return;
        }

        if (options == null) {
            options = new HashMap<>();
        }

        if (value == null) {
            options.put(key, defaultValue);
        } else {
            options.put(key, value);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Tridium interface implementation
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This belongs to getters but is defined in the Tridium interface, that's why it's here
     *
     * @return feature name
     */
    public String getFeatureName() {
        return featureName;
    }

    /**
     * This belongs to getters but is defined in the Tridium interface, that's why it's here
     *
     * @return vendor name
     */
    @Override
    public String getVendorName() {

        // compatibility with 1.0
        if (vendorName == null) {
            return "neopsis";
        }

        return vendorName;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public int compareTo(NvLicenseFeature f) {
        return this.featureName.compareTo(f.getFeatureName());
    }

    @JSONField(serialize = false, deserialize = false)
    @Override
    public boolean isExpired() {
        return this.expiration < Clock.millis();
    }

    /**
     * Check the featureName validity.
     * Invalid featureName throws {@link FeatureNotLicensedException}
     *
     * @throws FeatureNotLicensedException thrown if featureName is not licensed
     */
    @Override
    public void check() throws FeatureNotLicensedException {

        if (this.isExpired()) {
            throw new FeatureLicenseExpiredException(this.toString());
        }
    }

    /**
     * Returns a list of option names as String array.
     *
     * @return names of all options in a String array
     */
    @Override
    public String[] list() {

        ArrayList<String> lst = new ArrayList<>();

        for (String key : options.keySet()) {
            lst.add(key);
        }

        lst.trimToSize();

        return lst.toArray(new String[lst.size()]);
    }

    /**
     * Returns a string option
     *
     * @param option option key
     */
    @Override
    public String get(String option) {

        Object oval = options.get(option);

        if (oval instanceof String) {
            return (String) oval;
        }

        return oval.toString();
    }

    /**
     * Returns a string option or a default value if option
     * not available
     *
     * @param option option key
     * @param def    default option value
     */
    @Override
    public String get(String option, String def) {

        String value = get(option);

        if (value == null) {
            return def;
        }

        return value;
    }

    /**
     * Returns a boolean option or a default value if option
     * not available
     *
     * @param option option key
     * @param def    default option value
     */
    @Override
    public boolean getb(String option, boolean def) {

        String value = get(option);

        if (value == null) {
            return def;
        }

        value = value.toLowerCase();

        if (value.equals("true")) {
            return true;
        }

        if (value.equals("false")) {
            return false;
        }

        if (value.equals("0")) {
            return false;
        }

        if (value.equals("1")) {
            return true;
        }

        throw new IllegalStateException("Invalid boolean " + value);
    }

    /**
     * Returns an integer option or a default value if option
     * not available
     *
     * @param option option key
     * @param def    default option value
     */
    @Override
    public int geti(String option, int def) {

        final String value = this.get(option);

        if (value == null) {
            return def;
        }

        return Integer.parseInt(value);
    }

    @Override
    public String toString() {

        String exp;

        if (this.expiration == Long.MAX_VALUE) {
            exp = "never";
        } else {
            exp = LicenseUtil.formatDate(this.expiration);
        }

        if (this.isExpired()) {
            return featureName + " [expired: " + exp + "]";
        }

        return featureName + " [expires: " + exp + "]";
    }
}
