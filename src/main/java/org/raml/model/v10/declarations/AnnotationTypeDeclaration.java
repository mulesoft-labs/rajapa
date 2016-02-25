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
package org.raml.model.v10.declarations;

import java.util.List;
import org.raml.model.v10.datamodel.TypeDeclaration;


public interface AnnotationTypeDeclaration extends TypeDeclaration
{

    /**
     * Whether multiple instances of annotations of this type may be applied simultaneously at the same location
     **/
    Boolean allowMultiple();


    /**
     * Restrictions on where annotations of this type can be applied. If this property is specified, annotations of this type may only be applied on a property corresponding to one of the target names specified as the value of this property.
     **/
    List<AnnotationTarget> allowedTargets();


    /**
     * Instructions on how and when to use this annotation in a RAML spec.
     **/
    String usage();

}