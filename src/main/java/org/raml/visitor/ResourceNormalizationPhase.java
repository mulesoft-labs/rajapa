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
        //if (!maybeResource(node))
        //{
        //    return;
        //}

    return node;
    }

    //private boolean isResource(RamlNode node)
    //{
    //    RamlNode parent = node.getParent();
    //    if (parent != null && (parent instanceof RamlRootNode || parent instanceof RamlResourceNode))
    //    {
    //        if (!(node instanceof RamlKeyValueNode))
    //        {
    //            throw new IllegalStateException();
    //        }
    //        if (((RamlKeyValueNode) node).getKeyNodeValue().startsWith("/"))
    //        {
    //            node.replaceWith(new RamlResourceNode((RamlKeyValueNode) node));
    //        }
    //    }
    //    return false;
    //}
    //
    //private boolean maybeResource(RamlNode node)
    //{
    //    RamlNode parent = node.getParent();
    //    return parent != null && (parent instanceof RamlRootNode || parent instanceof RamlResourceNode);
    //}

}
