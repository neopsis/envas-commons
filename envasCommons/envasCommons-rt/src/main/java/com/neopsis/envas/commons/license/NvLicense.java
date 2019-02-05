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
import com.tridium.sys.Nre;

import javax.baja.license.Feature;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tridium license reference implementation
 *
 */
public class NvLicense extends NvAbstractLicense {

    @JSONField(name = "vendor")
    private String                 vendor;
    @JSONField(name = "hostId")
    private String                 hostId;
    @JSONField(
        name             = "expiration",
        serializeUsing   = JulianDateCodec.class,
        deserializeUsing = JulianDateCodec.class
    )
    public long                    expiration;
    @JSONField(
        name             = "generated",
        serializeUsing   = JulianDateCodec.class,
        deserializeUsing = JulianDateCodec.class
    )
    private long                   generated;
    @JSONField(name = "version")
    private String                 version;
    @JSONField(name = "features")
    private List<NvLicenseFeature> features;

    /**
     * Default constructor - reflection needs it
     */
    public NvLicense() {
        generated = new Date().getTime();
    }

    /**
     * Constructor creates new license object without features
     *
     * @param vendor  vendor
     * @param hostId  host ID
     * @param expDate expiration date
     * @param version version
     *
     */
    public NvLicense(String vendor, String hostId, long expDate, String version) {

        this();
        this.vendor  = vendor;
        this.hostId  = hostId;
        expiration   = expDate;
        this.version = version;
    }

    /**
     * Constructor creates new license object including features
     *
     * @param vendor  vendor
     * @param hostId  host ID
     * @param expDate expiration date
     * @param version version
     *
     * @param ftr features
     *
     */
    public NvLicense(String vendor, String hostId, long expDate, String version, List<NvLicenseFeature> ftr) {

        this();
        this.vendor  = vendor;
        this.hostId  = hostId;
        expiration   = expDate;
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

    public List<NvLicenseFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<NvLicenseFeature> features) {
        this.features = features;
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
            features = new ArrayList<>();
        }

        features.add(feature);
    }

    @JSONField(serialize = false, deserialize = false)
    public Feature getFeature(String featureName) {

        if (features == null) {
            return null;
        }

        for (NvLicenseFeature feature : features) {

            if (feature.getFeatureName().equals(featureName)) {
                return feature;
            }
        }

        return null;
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
        if( !verify(publicKey)) {
            throw new GeneralSecurityException("Envas license is not valid or has been tampered with since it was created!");
        };
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


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Fields
    ///////////////////////////////////////////////////////////////////////////////////////////
}
