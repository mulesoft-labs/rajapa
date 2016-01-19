package org.raml.phase;

import org.raml.nodes.RamlNode;

import java.util.Arrays;
import java.util.List;

/**
 * Applies a list of Transformers in a pipeline way
 */
public class TransformationPhase implements Phase {

    private List<Transformer> transformers;

    public TransformationPhase(Transformer... transformers) {
        this.transformers = Arrays.asList(transformers);
    }

    @Override
    public RamlNode apply(RamlNode tree) {
        //first pass may replace child nodes
        RamlNode result = tree;
        for (Transformer transformer : transformers) {
            if (transformer.matches(result)) {
                result = transformer.transform(result);
            }
        }
        if (tree != result && tree.getParent() != null) {
            tree.getParent().replaceChildWith(tree, result);
        }
        for (RamlNode ramlNode : result.getChildren()) {
            apply(ramlNode);
        }
        return result;
    }
}
