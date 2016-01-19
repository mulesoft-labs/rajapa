package org.raml.nodes.snakeyaml;

import org.raml.nodes.BaseRamlNode;
import org.raml.nodes.Position;
import org.yaml.snakeyaml.nodes.Node;

public class SYBaseRamlNode extends BaseRamlNode {

    private Node yamlNode;

    public SYBaseRamlNode(Node yamlNode) {
        this.yamlNode = yamlNode;
    }

    protected Node getYamlNode() {
        return yamlNode;
    }

    @Override
    public Position getStartMark()
    {
        return new SYPositionImpl(yamlNode.getStartMark());
    }

    @Override
    public Position getEndMark()
    {
        return new SYPositionImpl(yamlNode.getEndMark());
    }

}
