/*
 * @(#)LicenseUtils.java   06.10.2017
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.envas.commons.license.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.neopsis.envas.commons.license.NvAbstractLicense;

import javax.crypto.KeyGenerator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Utilities supporting licensing framework
 *
 */
public class LicenseUtils {

    /**
     * Default values
     */
    public static final String KPG_ALGORITHM = "DSA";
    public static final String PROVIDER      = "SUN";
    public static final String RNG_ALGORITHM = "SHA1PRNG";
    public static final String SIG_ALGORITHM = "SHA1withDSA";
    public static final int    KEYLEN        = 1024;
    public static final String DATE_FORMAT   = "yyyy-MM-dd";

    /**
     *
     *
     */
    public LicenseUtils() {

        //
    }

    /**
     * Test if unlimited strength policy is supported. Java Cryptography Extension (JCE)
     * Unlimited Strength Jurisdiction Policy Files are required to use PGP encryption
     * and may be required by some connectors.
     *
     * @return true if unlimited strength policy is supported
     */
    public static boolean isUnlimitedSupported() {

        boolean isSupported = false;

        try {

            KeyGenerator kgen = KeyGenerator.getInstance("AES", "SunJCE");

            kgen.init(256);
            isSupported = true;

        } catch (NoSuchAlgorithmException e) {
            isSupported = false;
        } catch (NoSuchProviderException e) {
            isSupported = false;
        }

        return isSupported;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private/Public Key generator
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the public key from the encoded byte array using default arguments
     *
     * <ul>
     *     <li>key pair algorithm = DSA</li>
     *     <li>provider = SUN</li>
     * </ul>
     *
     * @param encodedKey encoded byte array
     * @return public key
     *
     * @throws NoSuchAlgorithmException wrong algorithm (should not occur)
     * @throws NoSuchProviderException  wrong provider (should not occur)
     * @throws InvalidKeySpecException  invalid key (should not occur)
     */
    public static PublicKey getPublic(byte[] encodedKey)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        return getPublic(encodedKey, KPG_ALGORITHM, PROVIDER);
    }

    /**
     * Returns the public key from the encoded byte array.
     * The bytes can be recovered from a Hex string saved in a file etc.
     *
     * @param encodedKey   the encoded public key in bytes.
     * @param kpgAlgorithm key pair algorithm
     * @param provider     provider
     *
     * @throws NoSuchAlgorithmException wrong algorithm
     * @throws NoSuchProviderException  wrong provider
     * @throws InvalidKeySpecException  invalid key (should not occur)
     */
    public static PublicKey getPublic(byte[] encodedKey, String kpgAlgorithm, String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

        KeyFactory kf = KeyFactory.getInstance(kpgAlgorithm, provider);

        return kf.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    /**
     * Returns the private key for default arguments from the encoded byte array
     *
     * <ul>
     *     <li>key pair algorithm = DSA</li>
     *     <li>provider = SUN</li>
     * </ul>
     *
     * @param encodedKey encoded byte array
     *
     * @return private key
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivate(byte[] encodedKey)
            throws InvalidKeySpecException, NoSuchProviderException, NoSuchAlgorithmException {
        return getPrivate(encodedKey, KPG_ALGORITHM, PROVIDER);
    }

    /**
     * This method gets the private key from the encoded byte.
     * The bytes can be recovered from a Hex string saved in a file etc.
     * @param encodedKey the encoded private key in bytes.
     */
    public static PrivateKey getPrivate(byte[] encodedKey, String kpgAlgorithm, String provider)
            throws InvalidKeySpecException, NoSuchProviderException, NoSuchAlgorithmException {

        KeyFactory kf = KeyFactory.getInstance(kpgAlgorithm, provider);

        return kf.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    /**
     * Generates key pair for default arguments
     * <ul>
     *     <li>key length = 1024</li>
     *     <li>key pair algorithm = DSA</li>
     *     <li>provider = SUN</li>
     *     <li>random generator algorithm = SHA1PRNG</li>
     * </ul>
     *
     * @return a new key pair
     *
     * @throws NoSuchAlgorithmException invalid algorithm
     * @throws NoSuchProviderException  invalid provider
     */
    public static KeyPair getKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        return getKeyPair(KEYLEN, KPG_ALGORITHM, PROVIDER, RNG_ALGORITHM);
    }

    /**
     * Generates the key pair with arguments.
     *
     * @param   keyLen         key length
     * @param   kpgAlgorithm   algorithm for key pair generator (DSA, RSA, ...)
     * @param   provider       algorithm provider (SUN, ...)
     * @param   rngAlgorithm   algorithm for random generator (SHA1PRNG, ...)
     * @return                 a new key pair
     *
     * @throws NoSuchAlgorithmException invalid algorithm
     * @throws NoSuchProviderException  invalid provider
     *
     */
    public static KeyPair getKeyPair(int keyLen, String kpgAlgorithm, String provider, String rngAlgorithm)
            throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator kpg;

        kpg = KeyPairGenerator.getInstance(kpgAlgorithm, provider);
        kpg.initialize(keyLen, SecureRandom.getInstance(rngAlgorithm, provider));

        return kpg.generateKeyPair();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Signature handling
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Validates the license using the signature from the license using the default arguments
     *
     * <ul>
     *     <li>signature algorithm = SHA1withDSA</li>
     *     <li>provider = SUN</li>
     * </ul>
     *
     * @param lic            the license.
     * @param publicKey      public key as a hex string
     * @return a boolean whether the license data is valid.
     * @throws GeneralSecurityException if any exception was thrown
     */
    public static boolean verify(NvAbstractLicense lic, final String publicKey) throws GeneralSecurityException {
        return verify(lic, publicKey, SIG_ALGORITHM, PROVIDER);
    }

    /**
     * Validates the license using the signature from the license. Algorithm and provider
     * are passed a arguments
     *
     * @param lic            the license.
     * @param publicKey      public key as a hex string
     * @param sigAlgorithm   algorithm for the signature (SHA1withDSA, ...)
     * @param provider       algorithm provider (SUN, ...)
     * @return a boolean whether the license data is valid.
     * @throws GeneralSecurityException if any exception was thrown
     */
    public static boolean verify(NvAbstractLicense lic, final String publicKey, String sigAlgorithm, String provider)
            throws GeneralSecurityException {

        String errMsg    = "License verification failed: ";
        String signature = lic.getSignature();

        if ((signature == null) || (signature.trim().length() == 0)) {
            throw new GeneralSecurityException(errMsg + "no signature");
        }

        lic.setSignature(null);

        try {

            String    json    = toJson(lic, false);
            byte[]    sigArr  = ByteHex.convert(signature);
            boolean   isValid = false;
            Signature sig     = Signature.getInstance(sigAlgorithm, provider);
            PublicKey key     = getPublic(ByteHex.convert(publicKey));

            sig.initVerify(key);
            sig.update(json.getBytes());
            isValid = sig.verify(sigArr);
            lic.setSignature(signature);

            return isValid;

        } catch (NoSuchAlgorithmException e) {
            errMsg += "no such algorithm (" + e.getMessage() + ")";
        } catch (NoSuchProviderException e) {
            errMsg += "no such provider (" + e.getMessage() + ")";
        } catch (InvalidKeySpecException e) {
            errMsg += "invalid key specification (" + e.getMessage() + ")";
        } catch (InvalidKeyException e) {
            errMsg += "invalid key (" + e.getMessage() + ")";
        } catch (SignatureException e) {
            errMsg += "signature exception (" + e.getMessage() + ")";
        } catch (IllegalArgumentException e) {
            errMsg += "signature not a hex string";
        }

        lic.setSignature(signature);

        throw new GeneralSecurityException(errMsg);
    }

    /**
     * Signing the license data based on the private key using the default arguments
     *
     * <ul>
     *     <li>signature algorithm = SHA1withDSA</li>
     *     <li>provider = SUN</li>
     * </ul>
     *
     * @param lic            the license.
     * @param privateKey     private key as a hex string
     * @return a boolean whether the license data is valid.
     * @throws GeneralSecurityException if any exception was thrown
     */
    public static void sign(NvAbstractLicense lic, final String privateKey) throws GeneralSecurityException {
        sign(lic, privateKey, SIG_ALGORITHM, PROVIDER);
    }

    /**
     * Signes the license data based on the private key. Algorithm and provider are passed as arguments.
     *
     * @param lic            the license.
     * @param privateKey     private key as a hex string
     * @param sigAlgorithm   algorithm for the signature (SHA1withDSA, ...)
     * @param provider       algorithm provider (SUN, ...)
     *
     * @param lic the license.
     * @throws GeneralSecurityException if any exception was thrown
     */
    public static void sign(NvAbstractLicense lic, final String privateKey, String sigAlgorithm, String provider)
            throws GeneralSecurityException {

        String errMsg = "License sign failed: ";

        try {

            Signature  sig  = Signature.getInstance(sigAlgorithm, provider);
            PrivateKey key  = getPrivate(ByteHex.convert(privateKey));
            String     json = toJson(lic, false);

            sig.initSign(key);
            sig.update(json.getBytes());

            byte[] result = sig.sign();

            lic.setSignature(ByteHex.convert(result));

        } catch (NoSuchAlgorithmException e) {
            throw new GeneralSecurityException(errMsg + "no such algorithm (" + e.getMessage() + ")");
        } catch (NoSuchProviderException e) {
            throw new GeneralSecurityException(errMsg + "no such provider (" + e.getMessage() + ")");
        } catch (InvalidKeySpecException e) {
            throw new GeneralSecurityException(errMsg + "invalid key specification (" + e.getMessage() + ")");
        } catch (InvalidKeyException e) {
            throw new GeneralSecurityException(errMsg + "invalid key (" + e.getMessage() + ")");
        } catch (SignatureException e) {
            throw new GeneralSecurityException(errMsg + "signature exception (" + e.getMessage() + ")");
        } catch (IllegalArgumentException e) {
            throw new GeneralSecurityException(errMsg + "signature not a hex string");
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Converting tools
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts a license Java object to the JSON string with PrettyFormat feature
     *
     * @param license  the license as Java object
     * @return the license as JSON string
     */
    public static String toJson(NvAbstractLicense license) {
        return toJson(license, true);
    }

    /**
     * Converts a license Java object to the Json string. Using PrettyFormat
     * generates better human readable Json string.
     *
     * @param license  the license as Java object
     * @param prettyFormat true if output should be pretty formatted
     * @return the license as Json string
     */
    public static String toJson(NvAbstractLicense license, boolean prettyFormat) {

        SerializeWriter out    = new SerializeWriter();
        SerializeConfig config = new SerializeConfig();

        try {

            JSONSerializer serializer = new JSONSerializer(out, config);

            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            serializer.config(SerializerFeature.PrettyFormat, prettyFormat);
            serializer.setDateFormat(DATE_FORMAT);

            // license.configureSerializer(config);
            serializer.write(license);

            return out.toString();

        } finally {
            out.close();
        }

        // return JSON.toJSONStringWithDateFormat(license, "YYYY-MM-dd", features);
    }

    /**
     * Converts a JSON string to the Java object. The JSON
     * must be a serialization of {@link NvAbstractLicense} subclass
     *
     * @param json a license as JSON object
     * @return  license as Java object
     */
    public static <T extends NvAbstractLicense> T toObject(String json, Class<T> clazz)
            throws IllegalAccessException, InstantiationException {
        return toObject(json, clazz, ParserConfig.getGlobalInstance());
    }

    /**
     * Converts a JSON string to the Java object. The JSON
     * must be a serialization of {@link NvAbstractLicense} subclass
     *
     * @param json a license as JSON object
     * @return  license as Java object
     */
    public static <T extends NvAbstractLicense> T toObject(String json, Class<T> clazz, ParserConfig config)
            throws IllegalAccessException, InstantiationException {

        T lic = JSON.parseObject(json, clazz);

        /**
         *  Currently a bug in FastJSON - DefaultJSONParser.parseObject() does not
         *  accept field annotation @JSONField nd does not install custom
         *  deserializer.
         *
         *  ! -> lic.beforeDeserialize is not called !
         *
        T lic = clazz.newInstance();

        // lic.configureDeserializer(ParserConfig.getGlobalInstance());
        lic.beforeDeserialize();

        DefaultJSONParser parser = new DefaultJSONParser(json, config, DEFAULT_PARSER_FEATURE);
        parser.parseObject(lic);
        parser.close();

        */

        lic.afterDeserialize();

        return lic;
    }

    /**
     * Writes the license into a file
     *
     * @param license      license object
     * @param fileName     name of the license file
     * @throws IOException error on file write occurred
     */
    public static void writeLicense(NvAbstractLicense license, String fileName) throws IOException, GeneralSecurityException {

        if ((license.getSignature() == null) || license.getSignature().isEmpty()) {
            throw new GeneralSecurityException("Cannot save a license without signature");
        }

        FileOutputStream file = new FileOutputStream(fileName);

        file.write(LicenseUtils.toJson(license).getBytes());
        file.close();
    }

    /**
     * Reads the license from a file
     *
     * @param fileName     name of the license file
     * @throws IOException error on file write occurred
     */
    public static <T extends NvAbstractLicense> T readLicense(String fileName, Class<T> clazz)
            throws IOException, GeneralSecurityException, InstantiationException, IllegalAccessException {

        String content = new String(Files.readAllBytes(Paths.get(fileName)));

        return toObject(content, clazz);
    }
}
