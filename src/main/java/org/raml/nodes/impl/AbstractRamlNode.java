/*
 *
 */
package org.raml.nodes.impl;

import org.raml.nodes.BaseNode;
import org.raml.nodes.Position;

import javax.annotation.Nullable;

public abstract class AbstractRamlNode extends BaseNode
{

    @Nullable
    private Position endPosition;

    @Nullable
    private Position startPosition;

    public void setEndPosition(@Nullable Position endPosition)
    {
        this.endPosition = endPosition;
    }

    public void setStartPosition(@Nullable Position startPosition)
    {
        this.startPosition = startPosition;
    }

    @Override
    public Position getEndPosition()
    {
        return endPosition != null ? endPosition : getSource() != null ? getSource().getEndPosition() : null;
    }

    @Override
    public Position getStartPosition()
    {
        return startPosition != null ? startPosition : getSource() != null ? getSource().getStartPosition() : null;
    }
}
