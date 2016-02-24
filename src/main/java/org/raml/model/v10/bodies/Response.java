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
package org.raml.model.v10.bodies;

import java.util.List;
import org.raml.model.v10.common.RAMLLanguageElement;
import org.raml.model.v10.datamodel.TypeDeclaration;
import org.raml.model.v10.declarations.AnnotationRef;
import org.raml.model.v10.systemTypes.MarkdownString;
import org.raml.model.v10.systemTypes.StatusCodeString;


public interface Response extends RAMLLanguageElement
{

    /**
     * Responses MUST be a map of one or more HTTP status codes, where each status code itself is a map that describes that status code.
     **/
    StatusCodeString code();


    /**
     * Detailed information about any response headers returned by this method
     **/
    List<TypeDeclaration> headers();


    /**
     * The body of the response: a body declaration
     **/
    List<TypeDeclaration> body();


    /**
     * An alternate, human-friendly name for the response
     **/
    String displayName();


    /**
     * A longer, human-friendly description of the response
     **/
    MarkdownString description();


    /**
     * Most of RAML model elements may have attached annotations decribing additional meta data about this element
     **/
    List<AnnotationRef> annotations();

}