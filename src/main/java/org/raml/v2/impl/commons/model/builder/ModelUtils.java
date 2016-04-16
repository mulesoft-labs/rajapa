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
package org.raml.v2.impl.commons.model.builder;

import org.raml.v2.impl.commons.model.StringType;
import org.raml.v2.nodes.Node;
import org.raml.v2.utils.NodeSelector;

public class ModelUtils
{

    public static <T> T getSimpleValue(String key, Node node)
    {
        return NodeSelector.selectType(key, node, null);
    }

    public static String getStringValue(String key, Node node)
    {
        return getSimpleValue(key, node);
    }

    public static StringType getStringTypeValue(String key, Node node)
    {
        String value = getSimpleValue(key, node);
        if (value != null)
        {
            return new StringType(value);
        }
        return null;
    }
}
