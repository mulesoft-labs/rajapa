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
package org.raml.model.v10.common;

import java.util.List;
import org.raml.model.v10.core.BasicNode;
import org.raml.model.v10.declarations.AnnotationRef;
import org.raml.model.v10.systemTypes.MarkdownString;


public interface RAMLLanguageElement extends BasicNode
{

    /**
     * The displayName attribute specifies the $self's display name. It is a friendly name used only for display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself).
     **/
    String displayName();


    /**
     * The description attribute describes the intended use or meaning of the $self. This value MAY be formatted using Markdown [MARKDOWN]
     **/
    MarkdownString description();


    /**
     * Most of RAML model elements may have attached annotations decribing additional meta data about this element
     **/
    List<AnnotationRef> annotations();

}