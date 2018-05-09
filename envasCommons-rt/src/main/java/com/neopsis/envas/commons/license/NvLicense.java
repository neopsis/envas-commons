/*
 * @(#)NvLicense.java   06.10.2017
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.envas.commons.license;

import com.alibaba.fastjson.annotation.JSONField;
import com.neopsis.envas.commons.license.util.JulianDateCodec;
import com.neopsis.envas.commons.license.util.LicenseUtils;
import com.tridium.sys.Nre;

import javax.baja.license.Feature;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;

/**
 * Tridium license reference implementation
 *
 */
public class NvLicense extends NvAbstractLicense {

    @JSONField(name = "vendor")
    private String                            vendor;
    @JSONField(name = "hostId")
    private String                            hostId;
    @JSONField(
        name             = "expiration",
        serializeUsing   = JulianDateCodec.class,
        deserializeUsing = JulianDateCodec.class
    )
    private long                              expiration;
    @JSONField(
        name             = "generated",
        serializeUsing   = JulianDateCodec.class,
        deserializeUsing = JulianDateCodec.class
    )
    private long                              generated;
    @JSONField(name = "version")
    private String                            version;
    @JSONField(name = "features")
    private HashMap<String, NvLicenseFeature> features;

    /**
     * Default constructor - reflection needs it
     */
    public NvLicense() {

        //
    }

    /**
     * Constructor creates new license object without features
     *
     * @param vendor  vendorName
     * @param hostId  host ID
     * @param expDate expiration date
     * @param genDate generated date
     * @param version version
     *
     */
    public NvLicense(String vendor, String hostId, long expDate, long genDate, String version) {

        this.vendor  = vendor;
        this.hostId  = hostId;
        expiration   = expDate;
        generated    = genDate;
        this.version = version;
    }

    /**
     * Constructor creates new license object including features
     *
     * @param vendor  vendorName
     * @param hostId  host ID
     * @param expDate expiration date
     * @param genDate generated date
     * @param version version
     *
     * @param ftr features
     *
     */
    public NvLicense(String vendor, String hostId, long expDate, long genDate, String version,
                     HashMap<String, NvLicenseFeature> ftr) {

        this.vendor  = vendor;
        this.hostId  = hostId;
        expiration   = expDate;
        generated    = genDate;
        this.version = version;
        features     = ftr;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Setters and Getters
    ///////////////////////////////////////////////////////////////////////////////////////////
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getHostId() {
        return hostId;
    }

    public NvLicense setHostId(String hostId) {

        this.hostId = hostId;

        return this;
    }

    public void setExpiration(long expDate) {
        this.expiration = expDate;
    }

    @JSONField(serialize = false, deserialize = false)
    public void setExpirationDate(Date expDate) {
        this.expiration = expDate.getTime();
    }

    public void setGenerated(long genDate) {
        this.generated = genDate;
    }

    @JSONField(serialize = false, deserialize = false)
    public void setGeneratedDate(Date genDate) {
        this.generated = genDate.getTime();
    }

    public long getExpiration() {
        return expiration;
    }

    public long getGenerated() {
        return generated;
    }

    @JSONField(serialize = false, deserialize = false)
    public Date getExpirationDate() {
        return new Date(expiration);
    }

    @JSONField(serialize = false, deserialize = false)
    public Date getGeneratedDate() {
        return new Date(generated);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HashMap<String, NvLicenseFeature> getFeatures() {
        return features;
    }

    public void setFeatures(HashMap<String, NvLicenseFeature> features) {

        this.features = features;
        this.features.forEach(
            (key, value) -> {
                value.setVendorName(this.getVendor());
            });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Features utils
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     *  There is no updateFeature() or deleteFeature() methods. We assume a license will
     *  be always created from scratch using an entry form or a database as the data source.
     *
     */

    /**
     * Adds a license feature to the license. This is a facade for the features.add() method
     *
     * @param feature license feature object
     */
    public void addFeature(NvLicenseFeature feature) {

        if (feature == null) {
            return;
        }

        if (features == null) {
            features = new HashMap<>();
        }

        feature.setVendorName(getVendor());
        features.put(feature.getFeatureName().toLowerCase(), feature);
    }

    @JSONField(serialize = false, deserialize = false)
    public Feature getFeature(String feature) {

        if ((feature == null) || (features == null)) {
            return null;
        }

        return features.get(feature.toLowerCase());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Generate/Validate Utilities
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Check if the license is valid
     *
     */
    public void check(String publicKey) throws GeneralSecurityException {

        checkExpiration();
        validateHostId(Nre.getHostId().toLowerCase());
        verify(publicKey);
    }

    /**
     * Check license current expiration
     *
     * @throws GeneralSecurityException security exception with error message
     */
    public void checkExpiration(long checkDate) throws GeneralSecurityException {
        checkExpiration(new Date(checkDate));
    }

    /**
     * Check license current expiration
     *
     * @throws GeneralSecurityException security exception with error message
     */
    public void checkExpiration() throws GeneralSecurityException {
        checkExpiration(new Date());
    }

    /**
     * Check license expiration at some given date
     *
     * @param checkDate  date to check expiration
     * @throws GeneralSecurityException security exception with error message
     */
    public void checkExpiration(Date checkDate) throws GeneralSecurityException {

        if (checkDate == null) {
            throw new GeneralSecurityException("Missing license expiration");
        }

        if (checkDate.getTime() > expiration) {
            throw new GeneralSecurityException("License expired");
        }
    }

    /**
     * Check the Niagara HostId.
     *
     * @throws GeneralSecurityException security exception with error message
     */
    public void validateHostId(String checkHostId) throws GeneralSecurityException {

        if (checkHostId == null) {
            throw new GeneralSecurityException("License HostId check failed, checked HostId is null");
        }

        if (!hostId.equalsIgnoreCase(checkHostId)) {
            throw new GeneralSecurityException("Invalid Host ID");
        }
    }

    /**
     * Verify the license signature using a public key. This is a shortcut for
     * LicenseUtils.verify(this, publicKey);
     *
     * @param  PUBLIC_KEY public key as string
     * @throws GeneralSecurityException security exception with error message
     */
    public boolean verify(String PUBLIC_KEY) throws GeneralSecurityException {
        return LicenseUtils.verify(this, PUBLIC_KEY);
    }

    /**
     * When deserialization finishes, we must update all feature vendors from the license.
     *
     */
    @Override
    public void afterDeserialize() {

        this.features.forEach(
            (key, value) -> {
                value.setVendorName(this.getVendor());
            });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Fields
    ///////////////////////////////////////////////////////////////////////////////////////////
}
