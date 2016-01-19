package org.raml.phase;

import org.raml.nodes.RamlNode;

public class GrammarPhase implements Phase {
    @Override
    public RamlNode apply(RamlNode tree) {

        return tree;
    }
}
