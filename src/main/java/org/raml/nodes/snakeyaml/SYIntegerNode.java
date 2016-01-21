/*
 *
 */
package org.raml.nodes.snakeyaml;

import org.raml.nodes.IntegerNode;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.math.BigInteger;

public class SYIntegerNode extends SYBaseRamlNode implements IntegerNode
{

    public SYIntegerNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public BigInteger getValue()
    {
        final String value = ((ScalarNode) getYamlNode()).getValue();
        return new BigInteger(value);
    }
}
