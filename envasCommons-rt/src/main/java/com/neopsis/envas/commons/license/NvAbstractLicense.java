/*
 * @(#)NvAbstractLicense.java   06.10.2017
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.envas.commons.license;

import com.alibaba.fastjson.annotation.JSONField;

import com.neopsis.envas.commons.license.util.LicenseUtils;

import java.security.GeneralSecurityException;

/**
 * Subclasses should add license specific fields.
 * All licenses have a signature, it's here.
 *
 */
public abstract class NvAbstractLicense {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Fields
    ///////////////////////////////////////////////////////////////////////////////////////////
    @JSONField(name = "signature")
    protected String signature;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////////////////////
    public NvAbstractLicense() {}

    public NvAbstractLicense(String signature) {
        this.signature = signature;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getters/Setters
    ///////////////////////////////////////////////////////////////////////////////////////////
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * Verify the license signature using a public key
     *
     * @param PUBLIC_KEY public key as string
     * @throws GeneralSecurityException security exception with error message
     */
    public abstract boolean verify(String PUBLIC_KEY) throws GeneralSecurityException;

    /**
     * Parsing callback called after the license object was created and before
     * the object will be filled with parsed data. Default - do nothing
     */
    public void beforeDeserialize() {

        //
    }

    /**
     * Parsing callback called after parsing was finished. Default - do nothing
     */
    public void afterDeserialize() {

        //
    }

    @Override
    public String toString() {
        return LicenseUtils.toJson(this);
    }
}
