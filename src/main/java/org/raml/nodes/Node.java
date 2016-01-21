package org.raml.nodes;

import javax.annotation.Nullable;
import java.util.List;

public interface Node
{

    @Nullable
    Position getStartPosition();

    @Nullable
    Position getEndPosition();

    Node getRootNode();

    @Nullable
    Node getParent();

    List<Node> getChildren();

    void addChild(Node node);

    void setParent(Node parent);

    void setSource(Node source);

    <T extends Node> List<T> findChildrenWith(Class<T> nodeType);

    @Nullable
    Node getSource();

    void replaceWith(Node newNode);

    void setChild(int idx, Node newNode);
}
