/*
 * @(#)Tests.java   13.12.2017
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.envas.test;

import com.neopsis.envas.commons.license.NvLicense;
import com.neopsis.envas.commons.license.NvLicenseFeature;
import com.neopsis.envas.commons.license.util.ByteHex;
import com.neopsis.envas.commons.license.util.LicenseUtils;
import org.testng.annotations.Test;

import javax.baja.license.Feature;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test cases
 *
 *
 * @version        1.0.0, 13.12.2017
 * @author         Robert Carnecky
 */
public class Tests {

    public static String PRIVATE_KEY =
        "3082014C0201003082012C06072A8648CE3804013082011F02818100FD7F53811D75122952DF4A9C2EECE4E7F611B7523CEF4400C31E3F80B6512669455D402251FB593D8D58FABFC5F5BA30F6CB9B556CD7813B801D346FF26660B76B9950A5A49F9FE8047B1022C24FBBA9D7FEB7C61BF83B57E7C6A8A6150F04FB83F6D3C51EC3023554135A169132F675F3AE2B61D72AEFF22203199DD14801C70215009760508F15230BCCB292B982A2EB840BF0581CF502818100F7E1A085D69B3DDECBBCAB5C36B857B97994AFBBFA3AEA82F9574C0B3D0782675159578EBAD4594FE67107108180B449167123E84C281613B7CF09328CC8A6E13C167A8B547C8D28E0A3AE1E2BB3A675916EA37F0BFA213562F1FB627A01243BCCA4F1BEA8519089A883DFE15AE59F06928B665E807B552564014C3BFECF492A04170215008DD7783E59BFD923A4BCB79DADEBEFF119BB55C3";
    public static String PUBLIC_KEY =
        "308201B73082012C06072A8648CE3804013082011F02818100FD7F53811D75122952DF4A9C2EECE4E7F611B7523CEF4400C31E3F80B6512669455D402251FB593D8D58FABFC5F5BA30F6CB9B556CD7813B801D346FF26660B76B9950A5A49F9FE8047B1022C24FBBA9D7FEB7C61BF83B57E7C6A8A6150F04FB83F6D3C51EC3023554135A169132F675F3AE2B61D72AEFF22203199DD14801C70215009760508F15230BCCB292B982A2EB840BF0581CF502818100F7E1A085D69B3DDECBBCAB5C36B857B97994AFBBFA3AEA82F9574C0B3D0782675159578EBAD4594FE67107108180B449167123E84C281613B7CF09328CC8A6E13C167A8B547C8D28E0A3AE1E2BB3A675916EA37F0BFA213562F1FB627A01243BCCA4F1BEA8519089A883DFE15AE59F06928B665E807B552564014C3BFECF492A038184000281806BAD0894CACDF97BF7653C13527D20F6DC30B18A5570AF9FEA7B0D3A6673B68EF710F56FE4E6921DC7B5BAA3C7103E5387BD9C7A8AD412322584D684E9A8BB7CC0384673B9D39218DC933081C247D056D835D557E61DB55B6A49299C0C7C40A8D1779AB434E662986F0CFD935A530528F4B07B32847C43DC8F8537EC1F92CEAB";
    public static String GEN_PRIVATE_KEY;
    public static String GEN_PUBLIC_KEY;
    public static Date   sysdate   = new Date();
    public static String VENDOR    = "ACME";
    public static Date   GENERATED = new Date(sysdate.getYear(), sysdate.getMonth(), sysdate.getDate());
    public static Date   EXPIRED   = new Date(sysdate.getYear() + 1, sysdate.getMonth(), sysdate.getDate());

    // feature 1
    public static String FEATURE1       = "reports";
    public static String OPTION_KEY11   = "pdf";
    public static String OPTION_KEY12   = "doc";
    public static String OPTION_KEY13   = "html";
    public static String OPTION_VALUE11 = "true";
    public static String OPTION_VALUE12 = "false";
    public static String OPTION_VALUE13 = "true";

    // feature 2
    public static String FEATURE2       = "rdbms";
    public static String OPTION_KEY21   = "type";
    public static String OPTION_KEY22   = "max_tables";
    public static String OPTION_VALUE21 = "mysql";
    public static int    OPTION_VALUE22 = 200;
    public static String OPTION23       = "html";

    //
    public static String HOST_ID   = "Win-0000-1234-ABCD-FFFF";
    public static String VERSION   = "1.0";
    public static String FILE_NAME = HOST_ID + ".l4e";

    /**
     * Generates the key pair for the test
     */
    @Test
    public void generateKeyPair() {

        try {

            KeyPair    keyPair    = LicenseUtils.getKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey  publicKey  = keyPair.getPublic();

            GEN_PRIVATE_KEY = ByteHex.convert(privateKey.getEncoded());
            GEN_PUBLIC_KEY  = ByteHex.convert(publicKey.getEncoded());
            System.out.println("-----------------  KEY PAIR  ------------------");
            System.out.println("PRIVATE: " + GEN_PRIVATE_KEY);
            System.out.println("PUBLIC : " + GEN_PUBLIC_KEY);
            System.out.println();

        } catch (Exception e) {
            fail("Cannot generate a key pair");
        }
    }

    @Test
    public void licensorTest() {

        System.out.println("Licensor test");

        NvLicense lic = new NvLicense();

        lic.setVendor(VENDOR);
        lic.setExpiration(Long.MAX_VALUE);
        lic.setGenerated(GENERATED.getTime());
        lic.setHostId(HOST_ID);
        lic.setVersion(VERSION);

        NvLicenseFeature feature1 = new NvLicenseFeature();

        feature1.setVendorName(VENDOR);
        feature1.setFeatureName(FEATURE1);
        feature1.setExpiration(EXPIRED.getTime());
        feature1.addOption(OPTION_KEY11, OPTION_VALUE11);
        feature1.addOption(OPTION_KEY12, OPTION_VALUE12);
        feature1.addOption(OPTION_KEY13, OPTION_VALUE13);
        lic.addFeature(feature1);

        NvLicenseFeature feature2 = new NvLicenseFeature();

        feature2.setVendorName(VENDOR);
        feature2.setFeatureName(FEATURE2);
        feature2.setExpiration(Long.MAX_VALUE);
        feature2.addOption(OPTION_KEY21, OPTION_VALUE21);
        feature2.addOption(OPTION_KEY22, OPTION_VALUE22);
        lic.addFeature(feature2);

        try {
            LicenseUtils.sign(lic, PRIVATE_KEY);
        } catch (GeneralSecurityException e) {
            fail("cannot sign the license");
        }

        assertNotNull(lic.getSignature());

        Feature f = lic.getFeature(FEATURE1);

        assertNotNull("Cannot get license feature", f);
        assertTrue(f.getExpiration() == EXPIRED.getTime());
        assertTrue("Feature vendor missing", f.getVendorName().equalsIgnoreCase(VENDOR));

        // TEST: write the license to a file
        try {
            LicenseUtils.writeLicense(lic, FILE_NAME);
        } catch (Exception e) {
            fail("Cannot wrote the license to the file " + FILE_NAME + "(" + e.getMessage() + ")");
        }
    }

    @Test
    public void licenseeTest() {

        System.out.println("Licensee test");

        NvLicense lic     = null;
        boolean   isValid = false;

        // TEST: read the license from the file
        try {
            lic = LicenseUtils.readLicense(FILE_NAME, NvLicense.class);
        } catch (IOException e) {
            fail("IOException when reading file (" + e.getMessage() + ")");
        } catch (GeneralSecurityException e) {
            fail("GeneralSecurityException when reading file (" + e.getMessage() + ")");
        } catch (InstantiationException e) {
            fail("InstantiationException when reading file (" + e.getMessage() + ")");
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException when reading file (" + e.getMessage() + ")");
        }

        try {
            isValid = lic.verify(PUBLIC_KEY);
        } catch (GeneralSecurityException e) {
            fail("GeneralSecurityException when verifying license: " + e.getMessage() );
        }

        assertTrue("Valid license failed the verification", isValid);
    }
}
