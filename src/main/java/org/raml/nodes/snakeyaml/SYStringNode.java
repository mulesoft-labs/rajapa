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

import org.raml.nodes.Node;
import org.raml.nodes.StringNode;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYStringNode extends SYBaseRamlNode implements StringNode
{

    public SYStringNode(SYStringNode node)
    {
        super(node);
    }

    public SYStringNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public String getValue()
    {
        return ((ScalarNode) getYamlNode()).getValue();
    }

    @Override
    public Node copy()
    {
        return new SYStringNode(this);
    }

    @Override
    public String toString()
    {
        return getValue();
    }
}
