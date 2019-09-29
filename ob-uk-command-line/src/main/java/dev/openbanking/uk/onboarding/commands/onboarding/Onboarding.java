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

import dev.openbanking.uk.onboarding.commands.OBCommand;
import dev.openbanking.uk.onboarding.exceptions.SslConfigurationFailure;
import dev.openbanking.uk.onboarding.model.OIDCWellKnown;
import dev.openbanking.uk.onboarding.services.WellKnownService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.Callable;

@Component
@Getter
@Slf4j
@CommandLine.Command(name = "on-board", mixinStandardHelpOptions = true, subcommands = {
        OnboardingRegister.class,
        OnboardingGet.class,
        OnboardingEdit.class,
        OnboardingUnregister.class
},
        exitCodeOnExecutionException = 44)
public class Onboarding implements Callable<Integer> {

    @CommandLine.ParentCommand
    private OBCommand obCommand;

    @CommandLine.Option(names = {"-w", "--well-known"}, required = true)
    private String wellKnownUrl;

    @Autowired
    private WellKnownService wellKnownService;

    private OIDCWellKnown wellKnown;

    @Override
    public Integer call() {
        log.debug("ob on-board");
        return 43;
    }

    public void init() throws URISyntaxException, SslConfigurationFailure, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        obCommand.init();
        log.debug("Call the well-known {}", wellKnownUrl);
        wellKnown = wellKnownService.getWellKnown(obCommand.getRestTemplate(), new URI(wellKnownUrl));
        log.debug("Well-known result {}", wellKnown);
    }
}