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
package org.raml.model.v10.resources;

import org.raml.model.v10.datamodel.TypeInstance;


public interface ResourceType extends ResourceBase
{

    /**
     * The displayName attribute specifies the resource type display name. It is a friendly name used only for  display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself).
     **/
    String displayName();


    /**
     * Name of the resource type
     **/
    String name();


    /**
     * Instructions on how and when the resource type should be used.
     **/
    String usage();


    /**
     * Returns object representation of parametrized properties of the resource type
     **/
    TypeInstance parametrizedProperties();

}