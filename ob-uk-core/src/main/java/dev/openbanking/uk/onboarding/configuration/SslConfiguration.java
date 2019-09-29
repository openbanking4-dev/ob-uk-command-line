/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package dev.openbanking.uk.onboarding.configuration;

import dev.openbanking.uk.onboarding.exceptions.SslConfigurationFailure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@AllArgsConstructor
@Builder
public class SslConfiguration {

    private static final String JAVA_KEYSTORE = "jks";

    private KeyStore keyStore;
    private char[] keyStorePassword;
    private String keyAlias;
    private boolean checkHostname;

    public HttpComponentsClientHttpRequestFactory factory() throws SslConfigurationFailure {
        try {
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder()
                    .loadKeyMaterial(
                            keyStore,
                            keyStorePassword,
                            (aliases, socket) -> keyAlias.toLowerCase()
                    ).loadTrustMaterial(TrustAllStrategy.INSTANCE)

                    ;

            SSLContext sslContext = sslContextBuilder.build();
            SSLConnectionSocketFactory socketFactory;

            if (checkHostname) {
                socketFactory = new SSLConnectionSocketFactory(sslContext);
            } else {
                socketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            }

            HttpClient httpClient = HttpClients.custom()
                    .setRedirectStrategy(new DefaultRedirectStrategy() {
                        @Override
                        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
                            return false;
                        }
                    })
                    .setSSLSocketFactory(socketFactory).build();

            return new HttpComponentsClientHttpRequestFactory(httpClient);
        } catch (Exception e) {
            throw new SslConfigurationFailure(e);
        }
    }
}
