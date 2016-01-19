package org.raml.nodes;

public class RamlResourceNode extends RamlKeyValueNode
{

    public RamlResourceNode(RamlKeyValueNode node)
    {
        super(node);
    }

    public String getRelativePath()
    {
        return getKeyNodeValue();
    }

    public String getFullPath()
    {
        StringBuilder builder = new StringBuilder(getRelativePath());
        RamlNode current = this;
        while(current.getParent() != null)
        {
            current = current.getParent();
            if (current instanceof RamlResourceNode)
            {
                builder.insert(0, ((RamlResourceNode) current).getRelativePath());
            }
        }
        return builder.toString();
    }
}
