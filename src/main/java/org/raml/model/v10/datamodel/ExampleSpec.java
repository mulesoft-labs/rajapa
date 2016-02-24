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


public interface ExampleSpec extends RAMLLanguageElement
{

    /**
     * By default, examples are validated against any type declaration. Set this to false to allow examples that need not validate.
     **/
    Boolean strict();


    /**
     * Example identifier, if specified
     **/
    String name();


    /**
     * An alternate, human-friendly name for the example
     **/
    String displayName();


    /**
     * A longer, human-friendly description of the example
     **/
    MarkdownString description();


    /**
     * Most of RAML model elements may have attached annotations decribing additional meta data about this element
     **/
    List<AnnotationRef> annotations();


    /**
     * Returns object representation of example, if possible
     **/
    TypeInstance structuredContent();


    /**
     * String representation of example
     **/
    String content();

}