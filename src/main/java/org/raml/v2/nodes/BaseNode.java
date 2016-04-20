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
package org.raml.v2.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.utils.NodeSelector;

public abstract class BaseNode implements Node
{

    private Node source;
    private Node parent;
    private List<Node> children = new ArrayList<>();

    public BaseNode()
    {
    }

    public BaseNode(BaseNode node)
    {
        this.source = node;
        checkSourceLoop();
        for (Node child : node.children)
        {
            addChild(child.copy());
        }
    }

    @Override
    public Node getParent()
    {
        return parent;
    }

    @Nonnull
    @Override
    public List<Node> getChildren()
    {
        return Collections.unmodifiableList(new ArrayList<>(children));
    }

    @Override
    public void addChild(Node node)
    {
        node.setParent(this);
        children.add(node);
    }

    @Override
    public void removeChild(Node node)
    {
        node.setParent(null);
        children.remove(node);
    }

    @Override
    public Node getRootNode()
    {
        return getParent() == null ? this : getParent().getRootNode();
    }

    @Nonnull
    @Override
    public <T extends Node> List<T> findDescendantsWith(Class<T> nodeType)
    {
        final List<T> result = new ArrayList<>();
        final List<Node> children = getChildren();
        for (Node child : children)
        {
            if (nodeType.isAssignableFrom(child.getClass()))
            {
                result.add(nodeType.cast(child));
            }
            result.addAll(child.findDescendantsWith(nodeType));
        }
        return result;
    }

    @Nullable
    @Override
    public <T extends Node> T findAncestorWith(Class<T> nodeType)
    {
        Node parent = getParent();
        while (parent != null)
        {
            if (nodeType.isAssignableFrom(parent.getClass()))
            {
                return nodeType.cast(parent);
            }
            parent = parent.getParent();
        }
        return null;
    }

    @Override
    public void replaceWith(Node newNode)
    {
        if (this != newNode)
        {
            if (getParent() != null)
            {
                // If it has a parent replace it and the same idx
                int idx = getParent().getChildren().indexOf(this);
                getParent().setChild(idx, newNode);
            }
            newNode.setSource(this);
            for (Node child : getChildren())
            {
                newNode.addChild(child);
            }
        }
    }

    @Override
    public void setChild(int idx, Node newNode)
    {
        children.set(idx, newNode);
        newNode.setParent(this);
    }

    @Override
    public void addChild(int idx, Node newNode)
    {
        children.add(idx, newNode);
        newNode.setParent(this);
    }

    @Override
    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    @Override
    public void setSource(Node source)
    {
        this.source = source;
        checkSourceLoop();
    }

    private void checkSourceLoop()
    {
        Map<Node, Node> visited = new IdentityHashMap<>();
        Node source = this;
        while (source.getSource() != null)
        {
            if (visited.containsKey(source))
            {
                throw new RuntimeException("loop detected");
            }
            visited.put(source, source);
            source = source.getSource();
        }
    }

    @Override
    public Node getSource()
    {
        return source;
    }

    @Override
    public Node get(String selector)
    {
        return NodeSelector.selectFrom(selector, this);
    }
}
