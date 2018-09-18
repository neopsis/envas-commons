/*
 * @(#)NvLicenseManager.java   07.07.2016
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.envas.commons.license;

import com.neopsis.envas.util.NvLog;

import com.tridium.sys.license.LicenseUtil;

import javax.baja.license.Feature;
import javax.baja.license.FeatureNotLicensedException;
import javax.baja.license.LicenseDatabaseException;
import javax.baja.license.LicenseManager;
import javax.baja.nre.util.SortUtil;

import java.security.GeneralSecurityException;

import java.util.HashMap;

/**
 * License Manager saves all licensed features from all vendors.
 * Licensed features are accessible via simple API
 *    <li> {@link NvLicenseManager#getFeature}
 *    <li> {@link NvLicenseManager#checkFeature}
 *    <li> {@link NvLicenseManager#getFeatures()}
 *    <p>
 * All features from validated licenses (license HostId,
 * signature and expiration are OK), but features itself can be
 * invalid (expired)
 *
 */
public abstract class NvLicenseManager implements LicenseManager {

    private HashMap<String, NvLicenseFeature> features;

    /**
     * License Manager uses factory method <b>make()</b>
     * to create a new instance
     */
    protected NvLicenseManager() {}

    /**
     * Returns all licenses form license store, implemented
     * by subclasses
     *
     * @return array of licenses
     */
    protected abstract NvLicense[] loadLicenses();

    /**
     * Reloads licenses from the license store
     *
     */
    protected void reload() {
        this.load();
    }

    /**
     * Loads all valid features into the fresh initialized
     * feature map
     */
    protected final void load() {

        features = new HashMap<String, NvLicenseFeature>();
        setFeatures(loadLicenses());
    }

    /**
     * Add all valid features into the feature map.
     *
     * @param licenses array with all licenses loaded from license store
     */
    protected final void setFeatures(final NvLicense[] licenses) {

        for (int i = 0; i < licenses.length; i++) {

            NvLicense lic = licenses[i];

            try {

                lic.check(publicKey);

                HashMap<String, NvLicenseFeature> map = lic.getFeatures();

                for (NvLicenseFeature entry : map.values()) {
                    features.put(LicenseUtil.toKey(entry.getVendorName(), entry.getFeatureName()), entry);
                }

            } catch (GeneralSecurityException e) {
                NvLog.error("License validation failed: " + e.getMessage());
            }
        }
    }

    /**
     * Returns a vendor feature or raise an exception if feature does not exist.
     * Does not make a validation check
     *
     * @param vendor owner of the feature
     * @param feature feature name
     */
    public Feature getFeature(final String vendor, final String feature)
            throws FeatureNotLicensedException, LicenseDatabaseException {

        final String  key        = LicenseUtil.toKey(vendor, feature);
        final Feature licFeature = (Feature) this.features.get(key);

        if (licFeature == null) {
            throw new FeatureNotLicensedException(key);
        }

        return licFeature;
    }

    /**
     * Returns a vendor feature or raise an exception if feature does not exist or
     * if the feature is not valid (expired).
     *
     * @param vendor owner of the feature
     * @param feature feature name
     */
    @Override
    public Feature checkFeature(String vendor, String feature) throws FeatureNotLicensedException, LicenseDatabaseException {

        Feature licFeature = getFeature(vendor, feature);

        licFeature.check();

        return licFeature;
    }

    /**
     * Returns all features as array
     *
     * @return array with all features registered in this License Manager
     */
    @Override
    public Feature[] getFeatures() throws LicenseDatabaseException {

        final NvLicenseFeature[] temp = this.features.values().toArray(new NvLicenseFeature[this.features.size()]);
        final String[]           keys = new String[temp.length];

        for (int i = 0; i < keys.length; ++i) {
            keys[i] = LicenseUtil.toKey(temp[i].getVendorName(), temp[i].getFeatureName());
        }

        SortUtil.sort((Object[]) keys, (Object[]) temp, true);

        return (Feature[]) temp;
    }

    public void setPublicKey(String pk) {
        publicKey = pk;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Fields
    ///////////////////////////////////////////////////////////////////////////////////////////
    private String publicKey = "";
}
