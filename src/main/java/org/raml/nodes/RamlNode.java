package org.raml.nodes;

import javax.annotation.Nullable;
import java.util.List;

public interface RamlNode
{

    @Nullable
    Position getStartMark();

    @Nullable
    Position getEndMark();

    @Nullable
    RamlNode getParent();

    List<RamlNode> getChildren();

    void addChild(RamlNode node);

    void replaceChildWith(RamlNode oldNode, RamlNode newNode);

    void setParent(RamlNode parent);

    void setSource(RamlNode source);

    @Nullable
    RamlNode getSource();
}
