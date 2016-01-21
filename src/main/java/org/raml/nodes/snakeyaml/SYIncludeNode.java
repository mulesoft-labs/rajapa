/*
 *
 */
package org.raml.nodes.snakeyaml;

import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYIncludeNode extends SYStringNode
{

    public SYIncludeNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public String getIncludePath()
    {
        // TODO go up tree to get the whole path if there are other includes
        return getValue();
    }
}
