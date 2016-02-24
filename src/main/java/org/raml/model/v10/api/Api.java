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
package org.raml.model.v10.api;

import java.util.List;
import org.raml.model.v10.bodies.MimeType;
import org.raml.model.v10.datamodel.TypeDeclaration;
import org.raml.model.v10.declarations.AnnotationRef;
import org.raml.model.v10.methodsAndResources.Resource;
import org.raml.model.v10.methodsAndResources.SecuritySchemeRef;
import org.raml.model.v10.systemTypes.FullUriTemplateString;
import org.raml.model.v10.systemTypes.MarkdownString;


public interface Api extends LibraryBase
{

    /**
     * Short plain-text label for the API
     **/
    String title();


    /**
     * The version of the API, e.g. "v1"
     **/
    String version();


    /**
     * A URI that's to be used as the base of all the resources' URIs. Often used as the base of the URL of each resource, containing the location of the API. Can be a template URI.
     **/
    FullUriTemplateString baseUri();


    /**
     * The default media type to use for request and response bodies (payloads), e.g. "application&#47;json"
     **/
    MimeType mediaType();


    /**
     * The security schemes that apply to every resource and method in the API
     **/
    List<SecuritySchemeRef> securedBy();


    /**
     * The resources of the API, identified as relative URIs that begin with a slash (&#47;). Every property whose key begins with a slash (&#47;), and is either at the root of the API definition or is the child property of a resource property, is a resource property, e.g.: &#47;users, &#47;{groupId}, etc
     **/
    List<Resource> resources();


    /**
     * Additional overall documentation for the API
     **/
    List<DocumentationItem> documentation();


    /**
     * The displayName attribute specifies the $self's display name. It is a friendly name used only for display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself).
     **/
    String displayName();


    /**
     * A longer, human-friendly description of the API
     **/
    MarkdownString description();


    /**
     * Most of RAML model elements may have attached annotations decribing additional meta data about this element
     **/
    List<AnnotationRef> annotations();


    /**
     * Returns RAML version. "RAML10" string is returned for RAML 1.0. "RAML08" string is returned for RAML 0.8.
     **/
    String RAMLVersion();


    /**
     * Named parameters used in the baseUri (template)
     **/
    List<TypeDeclaration> baseUriParameters();


    /**
     * The protocols supported by the API
     **/
    List<String> protocols();

}
