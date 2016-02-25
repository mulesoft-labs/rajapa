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

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.raml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.ArrayList;
import java.util.List;

public class SYIncludeNode extends SYStringNode
{

    public SYIncludeNode(SYIncludeNode node)
    {
        super(node);
    }

    public SYIncludeNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public String getIncludePath()
    {
        Node current = this;
        while (current != null)
        {
            Node possibleSource = current.getSource();
            if (possibleSource instanceof SYIncludeNode)
            {
                String basePath = ((SYIncludeNode) possibleSource).getIncludePath();
                List<String> segments = Lists.newArrayList(basePath.split("/"));
                segments.remove(segments.size() - 1);
                return StringUtils.join(segments, "/") + "/" + getValue();
            }
            current = current.getParent();
        }

        return getValue();
    }

    @Override
    public Node copy()
    {
        return new SYIncludeNode(this);
    }
}
