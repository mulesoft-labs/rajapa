/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.nodes.snakeyaml;

import org.raml.nodes.IntegerNode;
import org.raml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.math.BigInteger;

public class SYIntegerNode extends SYBaseRamlNode implements IntegerNode
{

    public SYIntegerNode(SYIntegerNode node)
    {
        super(node);
    }

    public SYIntegerNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public BigInteger getValue()
    {
        final String value = ((ScalarNode) getYamlNode()).getValue();
        return new BigInteger(value);
    }

    @Override
    public Node copy()
    {
        return new SYIntegerNode(this);
    }
}
