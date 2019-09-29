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
package dev.openbanking.uk.onboarding.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nimbusds.jose.jwk.JWKSet;
import dev.openbanking.uk.onboarding.serialiser.JWKSetDeserializer;
import dev.openbanking.uk.onboarding.serialiser.JWKSetSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OIDCRegistrationRequest {

    @JsonProperty("scopes")
    private List<String> scopes;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("redirect_uris")
    private List<String> redirectUris;
    @JsonProperty("response_types")
    private List<String> responseTypes;
    @JsonProperty("grant_types")
    private List<String> grantTypes;
    @JsonProperty("application_type")
    private String applicationType;
    @JsonProperty("contacts")
    private List<String> contacts;
    @JsonProperty("client_name")
    private String clientName;
    @JsonProperty("logo_uri")
    private String logoUri;
    @JsonProperty("client_uri")
    private String clientUri;
    @JsonProperty("policy_uri")
    private String policyUri;
    @JsonProperty("tos_uri")
    private String tosUri;
    @JsonProperty("jwks_uri")
    private String jwks_uri;

    @JsonIgnore
    private String jwksSerialised;

    @JsonProperty("sector_identifier_uri")
    private String sectorIdentifierUri;
    @JsonProperty("subject_type")
    private String subjectType;

    @JsonProperty("id_token_signed_response_alg")
    private String idTokenSignedResponseAlg;
    @JsonProperty("id_token_encrypted_response_alg")
    private String idTokenEncryptedResponseAlg;
    @JsonProperty("id_token_encrypted_response_enc")
    private String idTokenEncryptedResponseEnc;

    @JsonProperty("userinfo_signed_response_alg")
    private String userinfoSignedResponseAlg;
    @JsonProperty("userinfo_encrypted_response_alg")
    private String userinfoEncryptedResponseAlg;
    @JsonProperty("userinfo_encrypted_response_enc")
    private String userinfoEncryptedResponseEnc;

    @JsonProperty("request_object_signing_alg")
    private String requestObjectSigningAlg;
    @JsonProperty("request_object_encryption_alg")
    private String requestObjectEncryptionAlg;
    @JsonProperty("request_object_encryption_enc")
    private String requestObjectEncryptionEnc;

    @JsonProperty("token_endpoint_auth_method")
    private String tokenEndpointAuthMethod;
    @JsonProperty("token_endpoint_auth_signing_alg")
    private String tokenEndpointAuthSigningAlg;

    @JsonProperty("default_max_age")
    private String defaultMaxAge;
    @JsonProperty("require_auth_time")
    private String requireAuthTime;
    @JsonProperty("default_acr_values")
    private String defaultAcrValues;
    @JsonProperty("initiate_login_uri")
    private String initiateLoginUri;
    @JsonProperty("request_uris")
    private List<String> requestUris;

    @JsonProperty("software_statement")
    private String softwareStatement;
    @JsonProperty("software_id")
    private String softwareId;
    @JsonProperty("tls_client_auth_subject_dn")
    private String tlsClientAuthSubjectDn;

    @JsonSerialize(using = JWKSetSerializer.class)
    public JWKSet getJwks() {
        if (jwksSerialised == null || "".equals(jwksSerialised)) {
            return null;
        }
        try {
            return JWKSet.parse(jwksSerialised);
        } catch (ParseException e) {
            throw new RuntimeException("Serialized JWKs set '" + jwksSerialised + "' doesn't seems to be a JWKs set");
        }
    }

    @JsonDeserialize(using = JWKSetDeserializer.class)
    public void setJwks(JWKSet jwks) {
        if (jwks != null) {
            this.jwksSerialised = jwks.toString();
        }
    }
}
