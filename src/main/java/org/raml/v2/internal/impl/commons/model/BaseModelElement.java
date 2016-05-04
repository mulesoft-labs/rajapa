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
package org.raml.v2.internal.impl.commons.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.raml.v2.internal.impl.commons.model.builder.ModelUtils;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.utils.NodeSelector;

public abstract class BaseModelElement
{

    protected abstract Node getNode();

    protected String getStringValue(String key)
    {
        return ModelUtils.getStringValue(key, getNode());
    }

    protected StringType getStringTypeValue(String key)
    {
        return ModelUtils.getStringTypeValue(key, getNode());
    }

    protected <T> List<T> getList(String key, Class<T> clazz)
    {
        ArrayList<T> resultList = new ArrayList<>();
        Node parent = NodeSelector.selectFrom(key, getNode());
        if (parent != null)
        {
            List<Node> nodes = new ArrayList<>();
            if (parent.getChildren().isEmpty())
            {
                // case when using syntactic sugar for single element
                // that does not require to be in a sequence
                nodes.add(parent);
            }
            else
            {
                nodes = parent.getChildren();
            }
            for (Node child : nodes)
            {
                try
                {
                    Constructor<T> constructor = clazz.getConstructor(Node.class);
                    resultList.add(constructor.newInstance(child));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return resultList;
    }

}
