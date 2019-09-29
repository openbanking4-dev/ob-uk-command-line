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

package dev.openbanking.uk.onboarding.commands;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import dev.openbanking.uk.onboarding.commands.onboarding.Onboarding;
import dev.openbanking.uk.onboarding.configuration.SslConfiguration;
import dev.openbanking.uk.onboarding.exceptions.SslConfigurationFailure;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.Callable;

@Component
@Command(name = "ob", mixinStandardHelpOptions = true, subcommands = {Onboarding.class})
@Getter
@Slf4j
public class OBCommand implements Callable<Integer> {

    // Prevent "Unknown option" error when users use
    // the Spring Boot parameter 'spring.config.location' to specify
    // an alternative location for the application.properties file.
    @CommandLine.Option(names = "--spring.config.location", hidden = true)
    private String springConfigLocation;

    @CommandLine.Option(names = "--verbose")
    private boolean verbose;

    @CommandLine.Option(names = {"-p", "--keystore-password"}, required = true, interactive = true)
    private char[] password;
    @CommandLine.Option(names = {"-t", "--transport-alias"}, required = true)
    private String transportAlias;
    @CommandLine.Option(names = {"-k", "--kid"}, required = false)
    private String kid;

    private RestTemplate restTemplate;

    @Value("${ob.ssl.key-store}")
    private String keyStorePath;

    @Value("${ob.ssl.check-hostname}")
    private boolean checkHostname;

    @Override
    public Integer call() {
        log.debug("ob");
        return 23;
    }

    public void init() throws SslConfigurationFailure, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("dev.openbanking");
        if (verbose) {
            rootLogger.setLevel(Level.DEBUG);
        } else {
            rootLogger.setLevel(Level.INFO);
        }

        log.debug("Setup the SSL configuration, using keystore {}, check hostname {} using the transport certificate alias {}",
                keyStorePath, checkHostname, transportAlias);
        SslConfiguration sslConfiguration = SslConfiguration.builder()
                .keyStore(getKeystore())
                .keyStorePassword(password)
                .checkHostname(checkHostname)
                .keyAlias(transportAlias)
                .build();

        restTemplate = new RestTemplate(sslConfiguration.factory());
    }

    public KeyStore getKeystore() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        return getStore(keyStorePath, password);
    }

    private KeyStore getStore(final String url, final char[] password) throws
            KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(url), password);
        return ks;
    }
}