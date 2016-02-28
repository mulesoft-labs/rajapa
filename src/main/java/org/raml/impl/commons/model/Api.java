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
package org.raml.impl.commons.model;

import static org.raml.impl.commons.model.builder.ModelUtils.getStringTypeValue;
import static org.raml.impl.commons.model.builder.ModelUtils.getStringValue;

import java.util.ArrayList;
import java.util.List;

import org.raml.impl.commons.nodes.RamlDocumentNode;
import org.raml.impl.commons.nodes.TraitNode;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.utils.NodeSelector;

public class Api
{

    private RamlDocumentNode node;

    public Api(RamlDocumentNode delegateNode)
    {
        node = delegateNode;
    }

    public String title()
    {
        return getStringValue("title", node);
    }

    public StringType mediaType()
    {
        return getStringTypeValue("mediaType", node);
    }

    public List<Trait> traits()
    {
        ArrayList<Trait> traitList = new ArrayList<>();
        Node traits = NodeSelector.selectFrom("traits", node);
        for (Node trait : traits.getChildren())
        {
            traitList.add(new Trait(((TraitNode) trait).getValue()));
        }
        return traitList;
    }

    public List<Resource> resources()
    {
        ArrayList<Resource> resultList = new ArrayList<>();
        for (Node item : node.getChildren())
        {
            if (item instanceof KeyValueNode && ((KeyValueNode) item).getKey().toString().startsWith("/"))
            {
                resultList.add(new Resource(((KeyValueNode) item).getValue()));
            }
        }
        return resultList;
    }

}
