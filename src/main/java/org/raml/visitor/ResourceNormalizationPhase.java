package org.raml.visitor;

import org.raml.nodes.RamlKeyValueNode;
import org.raml.nodes.RamlNode;
import org.raml.nodes.RamlResourceNode;
import org.raml.nodes.RamlRootNode;

public class ResourceNormalizationPhase implements Phase
{

    @Override
    public RamlNode apply(RamlNode node)
    {
        if (isResource(node))
        {
            node.replaceWith(new RamlResourceNode((RamlKeyValueNode) node));
        }

        return node;
    }

    /*
     * being a resource:
     *  - node is KeyValue
     *  - parent is Root or grampa is Resource
     *  - key starts with slash
     */
    private boolean isResource(RamlNode node)
    {
        if (!(node instanceof RamlKeyValueNode))
        {
            return false;
        }

        RamlNode parent = node.getParent();
        if (parent == null)
        {
            return false;
        }
        if (!(parent instanceof RamlRootNode))
        {
            RamlNode grampa = parent.getParent();
            if (!(grampa instanceof RamlResourceNode))
            {
                return false;
            }
        }

        String keyNodeValue = ((RamlKeyValueNode) node).getKeyNodeValue();
        return keyNodeValue != null && keyNodeValue.startsWith("/");
    }

}
