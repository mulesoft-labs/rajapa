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
import org.raml.model.v10.datamodel.TypeDeclaration;
import org.raml.model.v10.declarations.AnnotationRef;
import org.raml.model.v10.systemTypes.MarkdownString;


public interface Method extends MethodBase
{

    /**
     * Method that can be called
     **/
    String method();


    /**
     * An alternate, human-friendly name for the method (in the resource's context).
     **/
    String displayName();


    /**
     * A longer, human-friendly description of the method (in the resource's context)
     **/
    MarkdownString description();


    /**
     * Specifies the query string needed by this method. Mutually exclusive with queryParameters.
     **/
    TypeDeclaration queryString();


    /**
     * Detailed information about any query parameters needed by this method. Mutually exclusive with queryString.
     **/
    List<TypeDeclaration> queryParameters();


    /**
     * Detailed information about any request headers needed by this method.
     **/
    List<TypeDeclaration> headers();


    /**
     * Some methods admit request bodies, which are described by this property.
     **/
    List<TypeDeclaration> body();


    /**
     * A list of the traits to apply to this method. See [[raml-10-spec-applying-resource-types-and-traits|Applying Resource Types and Traits]] section.
     **/
    List<TraitRef> is();


    /**
     * Most of RAML model elements may have attached annotations decribing additional meta data about this element
     **/
    List<AnnotationRef> annotations();


    /**
     * The security schemes that apply to this method
     **/
    List<SecuritySchemeRef> securedBy();

}