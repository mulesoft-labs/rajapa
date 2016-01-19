package org.raml.nodes.impl;

import org.raml.nodes.BaseRamlNode;
import org.raml.nodes.Position;

public abstract class AbstractRamlNode extends BaseRamlNode {

    @Override
    public Position getEndMark() {
        return getSource() != null? getSource().getEndMark() : null;
    }

    @Override
    public Position getStartMark() {
        return getSource() != null? getSource().getStartMark() : null;
    }
}
