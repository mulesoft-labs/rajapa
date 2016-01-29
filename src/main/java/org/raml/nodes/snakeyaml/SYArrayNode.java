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

import org.raml.nodes.ArrayNode;
import org.raml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SYArrayNode extends SYBaseRamlNode implements ArrayNode
{

    public SYArrayNode(SYArrayNode node)
    {
        super(node);
    }

    public SYArrayNode(SequenceNode sequenceNode)
    {
        super(sequenceNode);
    }

    @Override
    public Node copy()
    {
        return new SYArrayNode(this);
    }
}
