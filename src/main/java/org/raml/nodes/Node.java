package org.raml.nodes;

import javax.annotation.Nullable;
import java.util.List;

public interface Node
{

    @Nullable
    Position getStartMark();

    @Nullable
    Position getEndMark();

    @Nullable
    Node getParent();

    List<Node> getChildren();

    void addChild(Node node);

    void replaceChildWith(Node oldNode, Node newNode);

    void replaceWith(Node newNode);

    void setParent(Node parent);

    void setSource(Node source);

    @Nullable
    Node getSource();

    void setAsSourceOf(Node node);
}
