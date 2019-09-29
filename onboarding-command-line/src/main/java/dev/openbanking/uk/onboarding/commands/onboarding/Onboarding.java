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
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Component
@CommandLine.Command(name = "on-board", mixinStandardHelpOptions = true, subcommands = {
        OnboardingRegister.class,
        OnboardingGet.class,
        OnboardingEdit.class,
        OnboardingUnregister.class
},
        exitCodeOnExecutionException = 44)
public class Onboarding implements Callable<Integer> {

    @CommandLine.Option(names = {"-p", "--keystore-password"}, required = true, arity = "0..1", interactive = true)
    private String password;
    @CommandLine.Option(names = {"-a", "--alias"}, required = true, arity = "0..1", interactive = true)
    private String alias;

    @CommandLine.Option(names = {"-w", "--well-known"}, required = true, arity = "0..1", interactive = true)
    private String wellKnownUrl;
    @CommandLine.Option(names = {"-k", "--kid"}, required = true, arity = "0..1", interactive = true)
    private String kid;

    @Override
    public Integer call() {
        System.out.printf("ob on-board");
        return 43;
    }
}