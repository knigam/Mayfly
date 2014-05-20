package com.keonasoft.mayfly.helper;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.MyRuntimeException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by atul on 5/11/14.
 */
public class HttpsCertAuth {

    private static HttpsCertAuth ourInstance = new HttpsCertAuth();
    private TrustManagerFactory tmf;

    private HttpsCertAuth(){
    }

    public static HttpsCertAuth getInstance() {
        return ourInstance;
    }

    private Certificate loadCAFromInputStream(String filename){
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new MyRuntimeException(e);
        }

        InputStream caInput = null;
        try {
            caInput = new BufferedInputStream(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new MyRuntimeException(e);
        }

        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca="+((X509Certificate) ca).getSubjectDN());
        } catch (CertificateException e) {
            throw new MyRuntimeException(e);
        } finally {
            try {
                caInput.close();
            } catch (IOException e) {
                throw new MyRuntimeException(e);
            }
        }
        return ca;
    }


    private KeyStore createKeystore(Certificate ca){
        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
        } catch (KeyStoreException e) {
            throw new MyRuntimeException(e);
        }
        try {
            keyStore.load(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        try {
            keyStore.setCertificateEntry("ca", ca);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return keyStore;

    }

    private TrustManagerFactory getTrustManagerFactory(){
        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new MyRuntimeException(e);
        }
        return tmf;
    }

    public TrustManagerFactory initTrustManager(){

        tmf = getTrustManagerFactory();
        String filename = "tmp"; //TODO change this
        Certificate  ca = loadCAFromInputStream(filename);
        KeyStore     keyStore = createKeystore(ca);

        try {
            tmf.init(keyStore);
        } catch (KeyStoreException e) {
            throw new MyRuntimeException(e);
        }

        return tmf;

    }

    public HttpsURLConnection getHttpsURLConnection(URL url){


        // Create an SSLContext that uses our TrustManager
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        TrustManagerFactory tmf = getTrustManagerFactory();

        try {
            context.init(null, tmf.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            throw new MyRuntimeException(e);
        }

        // Tell the URLConnection to use a SocketFactory from our SSLContext
        // URL url = new URL("https://certs.cac.washington.edu/CAtest/");

        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection)url.openConnection();
        } catch (IOException e) {
            throw new MyRuntimeException(e);
        }

        urlConnection.setSSLSocketFactory(context.getSocketFactory());

        return urlConnection;

        /*
        InputStream in = null;
        try {
            in = urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        copyInputStreamToOutputStream(in, System.out);
        */

    }



}
