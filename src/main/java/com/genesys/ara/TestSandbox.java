package com.genesys.ara;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.*;
import java.security.cert.CertificateException;

public class TestSandbox {
    public static void main(String [] args) {
        try {
            System.out.println("Trusted certificate...");
            trustedRequest();
            System.out.println("Untrusted certificate...");
            untrustedRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trustedRequest() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.sandbox-m2.ll9k.p1.openshiftapps.com:6443/apis/project.openshift.io/v1/projects")
                .method("GET", null)
                .addHeader("Authorization", "Bearer sha256~UpE1bfN9ySsV4X80cQvR8aJxbf4RGtgvL5AuJGVdV38")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    public static void untrustedRequest() throws Exception {
        // Create Trust Manager that does not validate cert chains
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        // Install Trust Manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        // Create SSL Socket Factory with Trust Manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory,(X509TrustManager)trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        OkHttpClient okHttpClient = builder.build();
        Request request = new Request.Builder()
                .url("https://api.crc.testing:6443/apis/project.openshift.io/v1/projects")
                .method("GET", null)
                .addHeader("Authorization", "Bearer sha256~kvYxXXGIBifi6PMLt8wQI0PLgiM-ALuPUv0J0d9FLyA")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        System.out.println(response.body().string());
    }

}
