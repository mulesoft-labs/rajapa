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
package org.raml.model.v10.datamodel;

import java.util.List;
import org.raml.model.v10.common.RAMLLanguageElement;
import org.raml.model.v10.declarations.AnnotationRef;
import org.raml.model.v10.systemTypes.MarkdownString;


public interface TypeDeclaration extends RAMLLanguageElement
{

    /**
     * name of the parameter
     **/
    String name();


    /**
     * When extending from a type you can define new facets (which can then be set to concrete values by subtypes).
     **/
    List<TypeDeclaration> facets();


    /**
     * Alias for the equivalent "type" property, for compatibility with RAML 0.8. Deprecated - API definitions should use the "type" property, as the "schema" alias for that property name may be removed in a future RAML version. The "type" property allows for XML and JSON schemas.
     **/
    String schema();


    /**
     * A base type which the current type extends, or more generally a type expression.
     **/
    List<String> type();


    /**
     * Location of the parameter (can not be edited by user)
     **/
    ModelLocation location();


    /**
     * Kind of location
     **/
    LocationKind locationKind();


    /**
     * Provides default value for a property
     **/
    Object default_();


    /**
     * An object containing named examples of instances of this type. This can be used, e.g., by documentation generators to generate sample values for an object of this type. Cannot be present if the examples property is present.
     **/
    List<ExampleSpec> examples();


    /**
     * The repeat attribute specifies that the parameter can be repeated. If the parameter can be used multiple times, the repeat parameter value MUST be set to 'true'. Otherwise, the default value is 'false' and the parameter may not be repeated.
     **/
    Boolean repeat();


    /**
     * Sets if property is optional or not
     **/
    Boolean required();


    /**
     * An alternate, human-friendly name for the type
     **/
    String displayName();


    /**
     * A longer, human-friendly description of the type
     **/
    MarkdownString description();


    /**
     * Most of RAML model elements may have attached annotations decribing additional meta data about this element
     **/
    List<AnnotationRef> annotations();


    /**
     * Returns facets fixed by the type. Value is an object with properties named after facets fixed. Value of each property is a value of the corresponding facet.
     **/
    TypeInstance fixedFacets();


    /**
     * Returns schema content for the cases when schema is inlined, when schema is included, and when schema is a reference.
     **/
    String schemaContent();


    /**
     * Returns object representation of example, if possible
     **/
    TypeInstance structuredExample();


    /**
     * An example of this type instance represented as string. This can be used, e.g., by documentation generators to generate sample values for an object of this type. Cannot be present if the examples property is present.
     **/
    String example();

}