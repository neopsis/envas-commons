/*
 * @(#)NvLicenseLocalManager.java   11.07.2016
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.envas.commons.license;

import com.neopsis.envas.commons.license.util.LicenseUtils;
import com.neopsis.envas.commons.util.NvLog;
import niagara.sys.Sys;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

/**
 * License Manager implementation for licenses saved in a local file store
 *
 */
public class NvLicenseLocalManager extends NvLicenseManager {

    private NvLicenseLocalManager() {

        // private constructor, use factory method make() to create INSTANCEs
    }

    public static NvLicenseLocalManager make(String publicKey) {

        NvLicenseLocalManager lm = new NvLicenseLocalManager();

        lm.setPublicKey(publicKey);
        lm.reload();

        return lm;
    }

    /**
     * License Local Manager loads the licenses from local file store.
     * This method returns array of all licenses found in all license files.
     * The license file must have the extension <i>*.l4e</>.
     */
    @Override
    protected NvLicense[] loadLicenses() {

        final ArrayList<NvLicense> licenses = new ArrayList<NvLicense>();
        final File                 dir      = Sys.getNiagaraSharedUserHome();
        final File[]               files    = dir.listFiles();

        for (int i = 0; (files != null) && (i < files.length); ++i) {

            if (files[i].getName().toLowerCase().endsWith(".l4e")) {

                try {

                    NvLicense lic = loadLicense(files[i]);

                    if (lic != null) {
                        licenses.add(lic);
                    }

                } catch (Exception e) {
                    NvLog.error("Error reading license file " + e.getMessage());
                }
            }
        }

        return licenses.toArray(new NvLicense[licenses.size()]);
    }

    private NvLicense loadLicense(File file)
            throws IOException, IllegalAccessException, GeneralSecurityException, InstantiationException {
        return LicenseUtils.readLicense(file.getPath(), NvLicense.class);
    }
}
