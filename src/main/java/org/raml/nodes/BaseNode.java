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
    public void replaceChildWith(Node oldNode, Node newNode)
    {
        int idx = children.indexOf(oldNode);
        newNode.setParent(this);
        children.set(idx, newNode);
        newNode.setSource(oldNode);
        oldNode.setAsSourceOf(newNode);
    }

    @Override
    public void replaceWith(Node newNode)
    {
        if (this != newNode)
        {
            if (parent != null)
            {
                parent.replaceChildWith(this, newNode);
            }
            else
            {
                this.setAsSourceOf(newNode);
            }
        }
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

    @Override
    public void setAsSourceOf(Node node)
    {
        node.setSource(this);
        for (Node child : children)
        {
            node.addChild(child);
        }
        this.children.clear();
        this.parent = null;
    }


}
