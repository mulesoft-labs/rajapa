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
package org.raml.v2.utils;

import org.raml.v2.nodes.Node;

import javax.annotation.Nullable;

public class NodeUtils
{

    @Nullable
    public static Node getGranParent(Node node)
    {
        return getAncestor(node, 2);
    }

    @Nullable
    public static Node getAncestor(Node node, int level)
    {
        int i = 1;
        Node parent = node.getParent();
        while (i < level && parent != null)
        {
            parent = parent.getParent();
            i++;
        }
        return parent;
    }
}
