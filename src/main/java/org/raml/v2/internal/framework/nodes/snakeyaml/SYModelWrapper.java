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
package org.raml.v2.internal.framework.nodes.snakeyaml;

import static org.yaml.snakeyaml.nodes.NodeId.mapping;
import static org.yaml.snakeyaml.nodes.NodeId.scalar;
import static org.yaml.snakeyaml.nodes.NodeId.sequence;

import org.raml.v2.internal.framework.nodes.KeyValueNodeImpl;
import org.raml.v2.internal.framework.nodes.Node;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class SYModelWrapper
{

    private String resourcePath;
    private boolean supportLibraries;
    private int depth;

    public SYModelWrapper(String resourcePath, boolean supportLibraries)
    {
        this.resourcePath = resourcePath;
        this.supportLibraries = supportLibraries;
    }

    private static class MappingNodeMerger extends SafeConstructor
    {
        void merge(MappingNode mappingNode)
        {
            flattenMapping(mappingNode);
        }
    }

    public static final Tag INCLUDE_TAG = new Tag("!include");

    public Node wrap(org.yaml.snakeyaml.nodes.Node node)
    {
        if (node.getNodeId() == mapping)
        {
            return wrap((MappingNode) node);
        }
        if (node.getNodeId() == sequence)
        {
            return wrap((SequenceNode) node);
        }
        if (node.getNodeId() == scalar)
        {
            return wrap((ScalarNode) node);
        }
        else
        {
            throw new IllegalStateException("Invalid node type");
        }
    }


    private SYObjectNode wrap(MappingNode mappingNode)
    {
        if (mappingNode.isMerged())
        {
            new MappingNodeMerger().merge(mappingNode);
        }
        SYObjectNode mapping = new SYObjectNode(mappingNode, resourcePath);
        depth++;
        for (NodeTuple nodeTuple : mappingNode.getValue())
        {
            checkForUsesKey(nodeTuple);
            Node key = wrap(nodeTuple.getKeyNode());
            Node value = wrap(nodeTuple.getValueNode());
            KeyValueNodeImpl keyValue = new KeyValueNodeImpl(key, value);
            mapping.addChild(keyValue);
        }
        depth--;
        return mapping;
    }

    /*
     * check if tuple is a 'uses' definition at root level and inject !include tags for each library reference
     */
    private void checkForUsesKey(NodeTuple nodeTuple)
    {
        if (!supportLibraries)
        {
            return;
        }
        if (depth > 1)
        {
            return;
        }
        if (!(nodeTuple.getKeyNode() instanceof ScalarNode) ||
            !"uses".equals(((ScalarNode) nodeTuple.getKeyNode()).getValue()))
        {
            return;
        }
        if (nodeTuple.getValueNode() instanceof MappingNode)
        {
            for (NodeTuple libTuple : ((MappingNode) nodeTuple.getValueNode()).getValue())
            {
                if (libTuple.getValueNode() instanceof ScalarNode)
                {
                    libTuple.getValueNode().setTag(INCLUDE_TAG);
                }
            }
        }
    }

    private Node wrap(ScalarNode scalarNode)
    {
        final Tag tag = scalarNode.getTag();
        if (INCLUDE_TAG.equals(tag))
        {
            return new SYIncludeNode(scalarNode, resourcePath);
        }
        else if (Tag.NULL.equals(tag))
        {
            return new SYNullNode(scalarNode, resourcePath);
        }
        else if (Tag.FLOAT.equals(tag))
        {
            return new SYFloatingNode(scalarNode, resourcePath);
        }
        else if (Tag.INT.equals(tag))
        {
            SYIntegerNode syIntegerNode = new SYIntegerNode(scalarNode, resourcePath);
            try
            {
                syIntegerNode.getValue();
                return syIntegerNode;
            }
            catch (NumberFormatException e)
            {
                // wrap with string node if number is invalid e.g: 12:30:00
                return new SYStringNode(scalarNode, resourcePath);
            }
        }
        else
        {
            final String value = scalarNode.getValue();
            // We only use true or false as boolean possibilities for yaml 1.2 and not yes no.
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
            {
                return new SYBooleanNode(scalarNode, resourcePath);
            }
            else
            {
                return new SYStringNode(scalarNode, resourcePath);
            }
        }
    }

    private SYArrayNode wrap(SequenceNode sequenceNode)
    {
        SYArrayNode sequence = new SYArrayNode(sequenceNode, resourcePath);
        for (org.yaml.snakeyaml.nodes.Node node : sequenceNode.getValue())
        {
            sequence.addChild(wrap(node));
        }
        return sequence;
    }
}
