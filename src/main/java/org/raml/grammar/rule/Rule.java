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
/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Rule
{

    @Nullable
    private NodeFactory factory;

    protected Rule()
    {

    }

    public abstract boolean matches(@Nonnull Node node);

    public abstract Node transform(@Nonnull Node node);

    public abstract String getDescription();

    @Nullable
    public NodeFactory getFactory()
    {
        return factory;
    }

    public Rule then(Class<? extends Node> clazz)
    {
        this.factory = new ClassNodeFactory(clazz);
        return this;
    }

    public Rule then(NodeFactory factory)
    {
        this.factory = factory;
        return this;
    }


}
