/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.v2.model.v10.security;

import java.util.List;
import org.raml.v2.model.v10.system.types.FixedUriString;


public interface OAuth1SecuritySchemeSettings extends SecuritySchemeSettings
{

    /**
     * The URI of the Temporary Credential Request endpoint as defined in RFC5849 Section 2.1
     **/
    FixedUriString requestTokenUri();


    /**
     * The URI of the Resource Owner Authorization endpoint as defined in RFC5849 Section 2.2
     **/
    FixedUriString authorizationUri();


    /**
     * The URI of the Token Request endpoint as defined in RFC5849 Section 2.3
     **/
    FixedUriString tokenCredentialsUri();


    /**
     * List of the signature methods used by the server. Available methods: HMAC-SHA1, RSA-SHA1, PLAINTEXT
     **/
    List<String> signatures();

}