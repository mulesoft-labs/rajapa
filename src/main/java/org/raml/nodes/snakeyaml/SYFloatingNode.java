/*
 *
 */
package org.raml.nodes.snakeyaml;

import org.raml.nodes.FloatingNode;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.math.BigDecimal;

public class SYFloatingNode extends SYBaseRamlNode implements FloatingNode
{
    public SYFloatingNode(ScalarNode yamlNode)
    {
        super(yamlNode);
    }

    @Override
    public BigDecimal getValue()
    {
        final String value = ((ScalarNode) getYamlNode()).getValue();
        return new BigDecimal(value);
    }
}
