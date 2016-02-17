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
package org.raml.types.builtin;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.impl.AbstractRamlNode;
import org.raml.nodes.snakeyaml.SYArrayNode;
import org.raml.nodes.snakeyaml.SYStringNode;
import org.raml.utils.NodeSelector;

public class FileTypeNode extends AbstractRamlNode implements ObjectNode
{

    public FileTypeNode()
    {
    }

    private FileTypeNode(FileTypeNode node)
    {
        super(node);
    }

    public Number getMinLength()
    {
        return NodeSelector.selectIntValue("minLength", getSource());
    }

    public Number getMaxLength()
    {
        return NodeSelector.selectIntValue("maxLength", getSource());
    }

    public List<String> getFileTypes()
    {
        ArrayList<String> fileTypes = Lists.newArrayList();
        Node fileTypesNode = NodeSelector.selectFrom("fileTypes", getSource());
        if (fileTypesNode != null)
        {
            if (fileTypesNode instanceof SYStringNode)
            {
                fileTypes.add(((SYStringNode) fileTypesNode).getValue());
            }
            else if (fileTypesNode instanceof SYArrayNode)
            {
                for (Node node : fileTypesNode.getChildren())
                {
                    if (node instanceof SYStringNode)
                    {
                        fileTypes.add(((SYStringNode) node).getValue());
                    }
                }
            }
        }
        return fileTypes;
    }

    @Override
    public Node copy()
    {
        return new FileTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }
}
