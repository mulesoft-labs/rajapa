package org.raml.nodes;

import java.util.Collection;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;

public interface RamlNode
{

    Node getYamlNode();
    Mark getStartMark();
    Mark getEndMark();

    RamlNode getParent();
    Collection<RamlNode> getChildren();

    void addChild(RamlNode node);

    void replaceChildWith(RamlNode oldNode, RamlNode newNode);

    void replaceWith(RamlNode newNode);

    void setParent(RamlNode parent);

    void setSource(RamlNode source);

    RamlNode getSource();

    void setAsSourceOf(RamlNode ramlNode);
}
