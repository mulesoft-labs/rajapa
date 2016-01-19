package org.raml.nodes.snakeyaml;

import org.raml.nodes.Position;
import org.yaml.snakeyaml.error.Mark;

public class SYPositionImpl implements Position {

    private Mark mark;

    public SYPositionImpl(Mark mark) {
        this.mark = mark;
    }

    @Override
    public int getIndex() {
        return mark.getIndex();
    }

    @Override
    public int getLine() {
        return mark.getLine();
    }

    @Override
    public int getColumn() {
        return mark.getColumn();
    }

    @Override
    public String getResource() {
        //Todo add the resource where this position belongs too
        return null;
    }
}
