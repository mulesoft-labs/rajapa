/*
 *
 */
package org.raml.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseNode implements Node
{

    private Node source;
    private Node parent;
    private List<Node> children = new ArrayList<>();

    @Override
    public Node getParent()
    {
        return parent;
    }

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
    public Node getRootNode()
    {
        return getParent() == null ? this : getParent().getRootNode();
    }

    @Override
    public <T extends Node> List<T> findChildrenWith(Class<T> nodeType)
    {
        final List<T> result = new ArrayList<>();
        final List<Node> children = getChildren();
        for (Node child : children)
        {
            if (nodeType.isAssignableFrom(child.getClass()))
            {
                result.add(nodeType.cast(child));
            }
            result.addAll(child.findChildrenWith(nodeType));
        }
        return result;
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
            this.children.clear();
        }
    }

    @Override
    public void setChild(int idx, Node newNode)
    {
        children.set(idx, newNode);
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
    }

    @Override
    public Node getSource()
    {
        return source;
    }


}
