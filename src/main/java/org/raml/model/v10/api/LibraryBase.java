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
import org.raml.model.v10.common.RAMLLanguageElement;
import org.raml.model.v10.datamodel.TypeDeclaration;
import org.raml.model.v10.declarations.AnnotationTypeDeclaration;
import org.raml.model.v10.methodsAndResources.AbstractSecurityScheme;
import org.raml.model.v10.methodsAndResources.ResourceType;
import org.raml.model.v10.methodsAndResources.Trait;


public interface LibraryBase extends RAMLLanguageElement
{

    /**
     * Alias for the equivalent "types" property, for compatibility with RAML 0.8. Deprecated - API definitions should use the "types" property, as the "schemas" alias for that property name may be removed in a future RAML version. The "types" property allows for XML and JSON schemas.
     **/
    List<GlobalSchema> schemas();


    /**
     * Declarations of (data) types for use within this API
     **/
    List<TypeDeclaration> types();


    /**
     * Declarations of annotation types for use by annotations
     **/
    List<AnnotationTypeDeclaration> annotationTypes();


    /**
     * Declarations of security schemes for use within this API.
     **/
    List<AbstractSecurityScheme> securitySchemes();


    /**
     * Importing libraries
     **/
    List<Library> uses();


    /**
     * Declarations of traits for use within this API
     **/
    List<Trait> traits();


    /**
     * Declarations of resource types for use within this API
     **/
    List<ResourceType> resourceTypes();

}