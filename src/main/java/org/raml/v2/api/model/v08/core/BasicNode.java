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
package org.raml.v2.api.model.v08.core;

import java.util.List;

public interface BasicNode extends AbstractWrapperNode
{

    /***
     * @return Direct ancestor in RAML hierarchy
     **/
    BasicNode parent();

    Object toJSON();

    /**
     * @return For siblings of traits or resource types returns a list of optional properties names.
     **/
    List<String> optionalProperties();

    /***
     * @return Whether the element is an optional sibling of trait or resource type
     **/
    boolean optional();

    /***
     * @return Metadata of the node itself and its scalara properties
     **/
    NodeMetadata meta();
}
