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

package dev.openbanking.uk.onboarding.commands.onboarding;

import dev.openbanking.uk.onboarding.exceptions.SslConfigurationFailure;
import dev.openbanking.uk.onboarding.model.OIDCWellKnown;
import dev.openbanking.uk.onboarding.services.DynamicRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.Callable;

@Component
@Slf4j
@CommandLine.Command(name = "unregister", mixinStandardHelpOptions = true,
        exitCodeOnExecutionException = 44)
public class OnboardingUnregister implements Callable<Integer> {

    @CommandLine.ParentCommand
    private Onboarding onboarding;

    @CommandLine.Option(names = {"-o", "--oidc-client-id"}, required = true)
    private String oidcClientId;
    @CommandLine.Option(names = {"-t", "--registration-access-token"}, required = true)
    private String registrationAccessToken;

    @Autowired
    private DynamicRegistrationService dynamicRegistrationService;

    @Override
    public Integer call() throws URISyntaxException, KeyStoreException, CertificateException, NoSuchAlgorithmException, SslConfigurationFailure, IOException {
        init();
        log.debug("ob on-board delete");
        OIDCWellKnown wellKnown = onboarding.getWellKnown();
        try {
            dynamicRegistrationService.unregister(
                    onboarding.getObCommand().getRestTemplate(),
                    wellKnown.getRegistrationEndpoint(),
                    oidcClientId,
                    registrationAccessToken
            );
            log.info("OIDC client unregistered");
        } catch (HttpClientErrorException e) {
            log.error("The ASPSP rejected the un-registration request: {}", e.getResponseBodyAsString(), e);
        }
        return 43;
    }

    public void init() throws URISyntaxException, SslConfigurationFailure, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        onboarding.init();
    }
}