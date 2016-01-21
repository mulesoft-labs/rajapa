package org.raml.nodes.impl;

import org.raml.nodes.BaseNode;
import org.raml.nodes.Position;

public abstract class AbstractRamlNode extends BaseNode
{

    @Override
    public Position getEndPosition()
    {
        return getSource() != null ? getSource().getEndPosition() : null;
    }

    @Override
    public Position getStartPosition()
    {
        return getSource() != null ? getSource().getStartPosition() : null;
    }
}
