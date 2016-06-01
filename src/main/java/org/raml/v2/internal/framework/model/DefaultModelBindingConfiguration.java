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
package org.raml.v2.internal.framework.model;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class DefaultModelBindingConfiguration implements ModelBindingConfiguration
{

    private List<ModelBinding> bindings;
    private NodeModelFactory defaultBinding;

    public DefaultModelBindingConfiguration()
    {
        this.bindings = new ArrayList<>();
    }

    @Nonnull
    @Override
    public NodeModelFactory bindingOf(Class<?> className)
    {
        for (ModelBinding binding : bindings)
        {
            final NodeModelFactory result = binding.binding(className);
            if (result != null)
            {
                return result;
            }
        }
        if (defaultBinding != null)
        {
            return defaultBinding;
        }
        else
        {
            throw new RuntimeException("No binding found for.");
        }
    }

    @Override
    public Class<?> reverseBindingOf(NodeModel model)
    {
        return null;
    }

    public DefaultModelBindingConfiguration bindPackage(String basePackageName)
    {
        this.bindings.add(new PackageModelBinding(basePackageName));
        return this;
    }

    public DefaultModelBindingConfiguration bindSimpleName(Class<? extends NodeModel> modelClass, String... names)
    {
        this.bindings.add(new SimpleClassNameBinding(new HashSet<>(Arrays.asList(names)), modelClass));
        return this;
    }

    public DefaultModelBindingConfiguration bindInterfaceTo(Class<?> anInterface, Class<? extends NodeModel> model)
    {
        this.bindings.add(new ImplementsInterfaceBinding(anInterface, model));
        return this;
    }

    public DefaultModelBindingConfiguration defaultTo(Class<? extends NodeModel> defaultClass)
    {
        this.defaultBinding = new ClassNodeModelFactory(defaultClass, false);
        return this;
    }
}
