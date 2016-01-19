package org.raml.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseRamlNode implements RamlNode {

    private RamlNode source;
    private RamlNode parent;
    private List<RamlNode> children = new ArrayList<>();

    @Override
    public RamlNode getParent()
    {
        return parent;
    }

    @Override
    public List<RamlNode> getChildren()
    {
        return Collections.unmodifiableList(new ArrayList<>(children));
    }

    @Override
    public void addChild(RamlNode node)
    {
        node.setParent(this);
        children.add(node);
    }

    @Override
    public void replaceChildWith(RamlNode oldNode, RamlNode newNode)
    {
        int idx = children.indexOf(oldNode);
        newNode.setParent(this);
        children.set(idx, newNode);
        oldNode.setParent(null);
        newNode.setSource(oldNode);
        for (RamlNode child : oldNode.getChildren()) {
            newNode.addChild(child);
        }
    }

    @Override
    public void setParent(RamlNode parent)
    {
        this.parent = parent;
    }

    @Override
    public void setSource(RamlNode source)
    {
        this.source = source;
    }

    @Override
    public RamlNode getSource()
    {
        return source;
    }
}
