/*
 *
 */
package org.raml.transformer;

import org.raml.nodes.Node;
import org.raml.phase.Phase;

import java.util.Arrays;
import java.util.List;

/**
 * Applies a list of Transformers in a pipeline way
 */
public class TransformationPhase implements Phase
{

    private List<Transformer> transformers;

    public TransformationPhase(Transformer... transformers)
    {
        this.transformers = Arrays.asList(transformers);
    }

    @Override
    public Node apply(Node tree)
    {
        // first pass may replace child nodes
        Node result = tree;
        for (Transformer transformer : transformers)
        {
            if (transformer.matches(result))
            {
                result = transformer.transform(result);
            }
        }
        if (tree != result && tree.getParent() != null)
        {
            tree.replaceWith(result);
        }
        for (Node node : result.getChildren())
        {
            apply(node);
        }
        return result;
    }
}
