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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import dev.openbanking.uk.onboarding.exceptions.SslConfigurationFailure;
import dev.openbanking.uk.onboarding.model.OIDCRegistrationRequest;
import dev.openbanking.uk.onboarding.model.OIDCRegistrationResponse;
import dev.openbanking.uk.onboarding.model.OIDCWellKnown;
import dev.openbanking.uk.onboarding.services.DynamicRegistrationService;
import dev.openbanking.uk.onboarding.services.JwtService;
import dev.openbanking.uk.onboarding.services.OnboardingService;
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
@CommandLine.Command(name = "register", mixinStandardHelpOptions = true,
        exitCodeOnExecutionException = 44)
@Slf4j
public class OnboardingRegister implements Callable<Integer> {

    @CommandLine.ParentCommand
    private Onboarding onboarding;

    @CommandLine.Option(names = {"-ssa", "--ssa"}, required = true)
    private String ssa;
    @CommandLine.Option(names = {"-sa", "--signing-alias"}, required = true)
    private String signingAlias;
    @CommandLine.Option(names = {"-ssi", "--software-statement-id"}, required = true)
    private String softwareStatementId;
    @CommandLine.Option(names = {"-a", "--alg"}, required = true)
    private String alg;

    private JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OnboardingService onboardingService;
    @Autowired
    private DynamicRegistrationService dynamicRegistrationService;

    @Override
    public Integer call() throws Exception {
        init();
        log.debug("ob on-board register");

        OIDCWellKnown wellKnown = onboarding.getWellKnown();
        String registrationRequest = generateRegistrationRequest(wellKnown);

        try {
            OIDCRegistrationResponse oidcRegistrationResponse = dynamicRegistrationService.register(
                    onboarding.getObCommand().getRestTemplate(),
                    wellKnown.getRegistrationEndpoint(),
                    registrationRequest
            );
            log.info("oidc registration response: {}", oidcRegistrationResponse);
        } catch (HttpClientErrorException e) {
            log.error("The ASPSP rejected the on-boarding request: {}", e.getResponseBodyAsString(), e);
        }
        return 43;
    }

    private String generateRegistrationRequest(OIDCWellKnown wellKnown) throws Exception {
        OIDCRegistrationRequest oidcRegistrationRequest = onboardingService.getDefaultRegistrationRequest();

        oidcRegistrationRequest.setSoftwareStatement(ssa);

        JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(objectMapper.writeValueAsString(oidcRegistrationRequest));
        JWSAlgorithm algorithm = JWSAlgorithm.parse(alg);

        return jwtService.signJwt(softwareStatementId, onboarding.getObCommand().getKid(), algorithm, jwtClaimsSet);
    }

    public void init() throws URISyntaxException, SslConfigurationFailure, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        onboarding.init();
        jwtService = JwtService.builder()
                .alias(signingAlias)
                .keystore(onboarding.getObCommand().getKeystore())
                .password(onboarding.getObCommand().getPassword())
                .build();
    }
}