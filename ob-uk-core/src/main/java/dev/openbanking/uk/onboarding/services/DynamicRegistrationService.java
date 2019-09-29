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

import dev.openbanking.uk.onboarding.model.OIDCRegistrationRequest;
import dev.openbanking.uk.onboarding.model.OIDCRegistrationResponse;
import dev.openbanking.uk.onboarding.model.OIDCWellKnown;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
public class DynamicRegistrationService {

    public OIDCRegistrationResponse register(
            RestTemplate restTemplate,
            String dynamicRegistrationEndpoint,
            String oidcRegistrationRequestAsJWT

    ) throws URISyntaxException {
        log.info("register by calling the dynamic registration uri {} with payload {}", dynamicRegistrationEndpoint, oidcRegistrationRequestAsJWT);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/jwt");

        return restTemplate.exchange(new URI(dynamicRegistrationEndpoint), HttpMethod.POST, new HttpEntity<>(oidcRegistrationRequestAsJWT, headers),
                new ParameterizedTypeReference<OIDCRegistrationResponse>() {
                }).getBody();
    }

    public void unregister(
            RestTemplate restTemplate,
            String dynamicRegistrationEndpoint,
            String oidcClientId,
            String accessToken
    ) throws URISyntaxException {
        log.info("unregister  OIDC Client ID {} by calling the dynamic registration uri {}", oidcClientId, dynamicRegistrationEndpoint);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Content-Type", "application/jwt");

        restTemplate.exchange(new URI(dynamicRegistrationEndpoint + "/" + oidcClientId), HttpMethod.DELETE, new HttpEntity<>(headers),
                new ParameterizedTypeReference<OIDCRegistrationResponse>() {
                }).getBody();
    }

    public OIDCRegistrationResponse edit(
            RestTemplate restTemplate,
            String dynamicRegistrationEndpoint,
            String oidcClientId,
            String accessToken,
            String oidcRegistrationRequestAsJWT
    ) throws URISyntaxException {
        log.info("Edit OIDC Client ID {} by calling the dynamic registration uri {}", oidcClientId, dynamicRegistrationEndpoint);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Content-Type", "application/jwt");

        return restTemplate.exchange(new URI(dynamicRegistrationEndpoint + "/" + oidcClientId), HttpMethod.PUT, new HttpEntity<>(oidcRegistrationRequestAsJWT, headers),
                new ParameterizedTypeReference<OIDCRegistrationResponse>() {
                }).getBody();
    }

    public OIDCRegistrationResponse read(
            RestTemplate restTemplate,
            String dynamicRegistrationEndpoint,
            String oidcClientId,
            String accessToken) throws URISyntaxException {
        log.info("Read  OIDC Client ID {} by calling the dynamic registration uri {}", oidcClientId, dynamicRegistrationEndpoint);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Content-Type", "application/jwt");

        return restTemplate.exchange(new URI(dynamicRegistrationEndpoint + "/" + oidcClientId), HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<OIDCRegistrationResponse>() {
                }).getBody();
    }

}
