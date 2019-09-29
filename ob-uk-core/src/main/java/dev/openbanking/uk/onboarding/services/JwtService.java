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

package dev.openbanking.uk.onboarding.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyStore;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Builder
@AllArgsConstructor
public class JwtService {

    private KeyStore keystore;
    private String alias;
    private char[] password;

    public String signJwt(String issuerId, String kid, JWSAlgorithm algo, JWTClaimsSet jwtClaimsSet) throws Exception {
        JWK signingJwk = JWK.load(keystore, alias, password);
        jwtClaimsSet = new JWTClaimsSet.Builder(jwtClaimsSet)
                .issuer(issuerId)
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader jwsHeader = new JWSHeader
                .Builder(algo)
                .keyID(kid)
                .build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
        try {
            if (signingJwk instanceof ECKey) {
                signedJWT.sign(new ECDSASigner((ECKey) signingJwk));
            } else if (signingJwk instanceof RSAKey) {
                signedJWT.sign(new RSASSASigner((RSAKey) signingJwk));
            } else {
                throw new RuntimeException("Unknown algorithm '" + signingJwk.getClass()
                        + "' used for generate the key '" + signingJwk.getKeyID() + "'");
            }
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Couldn't load the key behind the kid '{}'", signingJwk.getKeyID(), e);
            throw new RuntimeException(e);
        }
    }

}
