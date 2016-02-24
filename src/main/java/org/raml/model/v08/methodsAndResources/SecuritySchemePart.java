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
import org.raml.model.v08.bodies.Response;
import org.raml.model.v08.parameters.Parameter;
import org.raml.model.v08.systemTypes.MarkdownString;


public interface SecuritySchemePart extends MethodBase
{

    /**
     * Headers that allowed at this position
     **/
    List<Parameter> headers();


    /**
     * An APIs resources MAY be filtered (to return a subset of results) or altered (such as transforming a response body from JSON to XML format) by the use of query strings. If the resource or its method supports a query string, the query string MUST be defined by the queryParameters property
     **/
    List<Parameter> queryParameters();


    /**
     * Optional array of responses, describing the possible responses that could be sent.
     **/
    List<Response> responses();


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


    /**
     * An alternate, human-friendly name for the security scheme part
     **/
    String displayName();


    /**
     * A longer, human-friendly description of the security scheme part
     **/
    MarkdownString description();

}