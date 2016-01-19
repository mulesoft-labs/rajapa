package org.raml.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;

public class RamlAbstractNode implements RamlNode
{

    private Node yamlNode;
    private RamlNode source;
    private RamlNode parent;
    private List<RamlNode> children = new ArrayList<>();

    public RamlAbstractNode(Node yamlNode)
    {
        this.yamlNode = yamlNode;
    }

    @Override
    public Node getYamlNode()
    {
        //TODO isolate snakeyaml node access to this class
        return yamlNode;
    }

    @Override
    public Mark getStartMark()
    {
        return yamlNode.getStartMark();
    }

    @Override
    public Mark getEndMark()
    {
        return yamlNode.getEndMark();
    }

    @Override
    public RamlNode getParent()
    {
        return parent;
    }

    @Override
    public Collection<RamlNode> getChildren()
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
        oldNode.setAsSourceOf(newNode);
    }

    @Override
    public void replaceWith(RamlNode newNode)
    {
        parent.replaceChildWith(this, newNode);
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

    @Override
    public void setAsSourceOf(RamlNode ramlNode)
    {
        ramlNode.setSource(this);
        for (RamlNode child : children)
        {
            ramlNode.addChild(child);
        }
        this.children.clear();
        this.parent = null;
    }

}
