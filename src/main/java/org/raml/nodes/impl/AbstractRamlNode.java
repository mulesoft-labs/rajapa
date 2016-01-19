package org.raml.nodes.impl;

import org.raml.nodes.BaseNode;
import org.raml.nodes.Position;

public abstract class AbstractRamlNode extends BaseNode {

    @Override
    public Position getEndMark() {
        return getSource() != null? getSource().getEndMark() : null;
    }

    @Override
    public Position getStartMark() {
        return getSource() != null? getSource().getStartMark() : null;
    }
}
