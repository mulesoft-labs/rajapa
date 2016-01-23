/*
 *
 */
package org.raml.nodes.snakeyaml;

import org.raml.nodes.NullNode;
import org.yaml.snakeyaml.nodes.Node;

public class SYNullNode extends SYBaseRamlNode implements NullNode
{
    public SYNullNode(Node yamlNode)
    {
        super(yamlNode);
    }

    @Override
    public Object getValue()
    {
        return null;
    }
}
