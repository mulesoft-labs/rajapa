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
package org.raml.model.v10.methodsAndResources;

import java.util.List;
import org.raml.model.v10.bodies.Response;
import org.raml.model.v10.datamodel.TypeDeclaration;
import org.raml.model.v10.declarations.AnnotationRef;
import org.raml.model.v10.systemTypes.MarkdownString;


public interface SecuritySchemePart extends MethodBase
{

    /**
     * Headers that allowed at this position
     **/
    List<TypeDeclaration> headers();


    /**
     * An APIs resources MAY be filtered (to return a subset of results) or altered (such as transforming a response body from JSON to XML format) by the use of query strings. If the resource or its method supports a query string, the query string MUST be defined by the queryParameters property
     **/
    List<TypeDeclaration> queryParameters();


    /**
     * Specifies the query string, used by the scheme in order to authorize the request. Mutually exclusive with queryParameters.
     **/
    TypeDeclaration queryString();


    /**
     * Optional array of responses, describing the possible responses that could be sent. See [[raml-10-spec-responses|Responses]] section.
     **/
    List<Response> responses();


    /**
     * Instantiation of applyed traits
     **/
    List<TraitRef> is();


    /**
     * securityScheme may also be applied to a resource by using the securedBy key, which is equivalent to applying the securityScheme to all methods that may be declared, explicitly or implicitly, by defining the resourceTypes or traits property for that resource.
     * To indicate that the method may be called without applying any securityScheme, the method may be annotated with the null securityScheme.
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


    /**
     * Annotations to be applied to this security scheme part. Annotations are any property whose key begins with "(" and ends with ")" and whose name (the part between the beginning and ending parentheses) is a declared annotation name. See [[raml-10-spec-annotations|the section on annotations]].
     **/
    List<AnnotationRef> annotations();

}