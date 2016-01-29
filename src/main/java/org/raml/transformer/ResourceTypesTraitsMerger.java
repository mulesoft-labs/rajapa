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
/*
 *
 */
package org.raml.transformer;

import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.utils.NodeSelector;

public class ResourceTypesTraitsMerger
{

    static void merge(Node baseNode, Node copyNode)
    {
        if (baseNode == null || copyNode == null)
        {
            throw new IllegalArgumentException();
        }

        for (Node child : copyNode.getChildren())
        {
            if (child instanceof KeyValueNode)
            {
                if (shouldIgnoreKey((KeyValueNode) child))
                {
                    continue;
                }
                String key = ((KeyValueNode) child).getKey().toString();
                Node node = NodeSelector.selectFrom(key, baseNode);
                if (node == null)
                {
                    baseNode.addChild(child);
                }
                else
                {
                    merge(node, child);
                }
            }
        }
    }

    private static boolean shouldIgnoreKey(KeyValueNode child)
    {
        return "usage".equals(child.getKey().toString());
    }
}
