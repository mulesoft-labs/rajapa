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
package org.raml.model.v08.methodsAndResources;

import java.util.List;
import org.raml.model.v08.bodies.BodyLike;
import org.raml.model.v08.bodies.Response;
import org.raml.model.v08.parameters.HasNormalParameters;


public interface MethodBase extends HasNormalParameters
{

    /**
     * Resource methods MAY have one or more responses. Responses MAY be described using the description property, and MAY include example attributes or schema properties.
     **/
    List<Response> responses();


    /**
     * Some method verbs expect the resource to be sent as a request body. For example, to create a resource, the request must include the details of the resource to create.
     * Resources CAN have alternate representations. For example, an API might support both JSON and XML representations.
     * A method's body is defined in the body property as a hashmap, in which the key MUST be a valid media type.
     **/
    List<BodyLike> body();


    /**
     * A method can override an API's protocols value for that single method by setting a different value for the fields.
     **/
    List<String> protocols();


    /**
     * Instantiation of applyed traits
     **/
    List<TraitRef> is();


    /**
     * A list of the security schemas to apply, these must be defined in the securitySchemes declaration.
     * To indicate that the method may be called without applying any securityScheme, the method may be annotated with the null securityScheme.
     * Security schemas may also be applied to a resource with securedBy, which is equivalent to applying the security schemas to all methods that may be declared, explicitly or implicitly, by defining the resourceTypes or traits property for that resource.
     **/
    List<SecuritySchemeRef> securedBy();

}