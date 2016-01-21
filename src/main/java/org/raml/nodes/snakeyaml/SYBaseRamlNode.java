package org.raml.nodes.snakeyaml;

import org.raml.nodes.BaseNode;
import org.raml.nodes.Position;
import org.yaml.snakeyaml.nodes.Node;

public class SYBaseRamlNode extends BaseNode
{

    private Node yamlNode;

    public SYBaseRamlNode(Node yamlNode)
    {
        this.yamlNode = yamlNode;
    }

    protected Node getYamlNode()
    {
        return yamlNode;
    }

    @Override
    public Position getStartPosition()
    {
        return new SYPosition(yamlNode.getStartMark());
    }

    @Override
    public Position getEndPosition()
    {
        return new SYPosition(yamlNode.getEndMark());
    }

}
