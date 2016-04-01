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
package org.raml.utils;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import org.raml.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.nodes.Node;

public class TestTreeDumper extends TreeDumper
{

    public String dump(Node node)
    {
        printIndent();
        super.dumpNode(node);
        dump.append(" (");
        if (node.getStartPosition() != null)
        {
            dump.append("Start: ").append(node.getStartPosition().getIndex());
        }

        if (node.getEndPosition() != null)
        {
            dump.append(" , End: ").append(node.getEndPosition().getIndex());
        }
        if (node.getSource() != null)
        {
            dump.append(", Source: ");
            dump.append(node.getSource().getClass().getSimpleName());
        }
        dump.append(")");
        dump.append("\n");
        indent();
        Collection<Node> children = node.getChildren();
        if (node instanceof ObjectTypeNode)
        {
            List<Node> merged = Lists.newArrayList();
            merged.addAll(children);
            merged.addAll(((ObjectTypeNode) node).getInheritedProperties());
            children = merged;
        }
        for (Node child : children)
        {
            dump(child);
        }
        dedent();
        return dump.toString();
    }
}
