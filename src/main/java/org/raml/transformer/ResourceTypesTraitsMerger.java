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

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.SimpleTypeNode;
import org.raml.nodes.snakeyaml.SYArrayNode;
import org.raml.nodes.snakeyaml.SYObjectNode;
import org.raml.utils.NodeSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceTypesTraitsMerger
{

    private static final Logger logger = LoggerFactory.getLogger(ResourceTypesTraitsMerger.class);

    static void merge(Node baseNode, Node copyNode)
    {
        if (baseNode instanceof SYObjectNode && copyNode instanceof SYObjectNode)
        {
            merge((SYObjectNode) baseNode, (SYObjectNode) copyNode);
        }
        else if (baseNode instanceof SYArrayNode && copyNode instanceof SYArrayNode)
        {
            merge((SYArrayNode) baseNode, (SYArrayNode) copyNode);
        }
        else
        {
            throw new RuntimeException(String.format("Merging not supported for nodes of type %s and %s",
                    baseNode.getClass().getSimpleName(), copyNode.getClass().getSimpleName()));
        }
    }

    static void merge(SYArrayNode baseNode, SYArrayNode copyNode)
    {
        for (Node child : copyNode.getChildren())
        {
            baseNode.addChild(child);
        }
    }

    static void merge(SYObjectNode baseNode, SYObjectNode copyNode)
    {
        for (Node child : copyNode.getChildren())
        {
            if (!(child instanceof KeyValueNode))
            {
                throw new RuntimeException("only expecting KeyValueNode");
            }

            String key = ((KeyValueNode) child).getKey().toString();
            if (shouldIgnoreKey((KeyValueNode) child))
            {
                logger.info("Ignoring key '{}'", key);
                continue;
            }

            boolean optional = key.endsWith("?");
            if (optional)
            {
                key = key.substring(0, key.length() - 1);
            }
            Node node = NodeSelector.selectFrom(key, baseNode);
            if (node == null && optional)
            {
                logger.info("Ignoring optional key {}", key);
            }
            else if (node == null)
            {
                logger.info("Adding key '{}'", key);
                baseNode.addChild(child);
            }
            else if (((KeyValueNode) child).getValue() instanceof SimpleTypeNode)
            {
                logger.info("Scalar key already exists '{}'", key);
            }
            else
            {
                logger.info("Merging values '{}' and '{}'", node.getParent(), child);
                merge(node, ((KeyValueNode) child).getValue());
            }
        }
    }

    private static boolean shouldIgnoreKey(KeyValueNode child)
    {
        Set<String> ignoreSet = ImmutableSet.<String> builder()
                                            .add("usage")
                                            .add("type")
                                            .build();
        String key = child.getKey().toString();
        return ignoreSet.contains(key);
    }
}
