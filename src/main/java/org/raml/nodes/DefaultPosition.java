/*
 *
 */
package org.raml.nodes;

public class DefaultPosition implements Position
{
    private int index;
    private int line;
    private int column;
    private String resource;

    public DefaultPosition(int index, int line, int column, String resource)
    {
        this.index = index;
        this.line = line;
        this.column = column;
        this.resource = resource;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public int getLine()
    {
        return line;
    }

    @Override
    public int getColumn()
    {
        return column;
    }

    @Override
    public String getResource()
    {
        return resource;
    }

    @Override
    public Position rightShift(int offset)
    {
        return new DefaultPosition(index + offset, line, column + offset, resource);
    }
}
