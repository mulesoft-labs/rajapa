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
package org.raml.v2.impl.commons.phase;

import com.fasterxml.jackson.core.JsonParseException;

import java.util.List;

import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.SchemaNodeImpl;
import org.raml.v2.internal.framework.phase.Phase;
import org.raml.v2.internal.utils.SchemaGenerator;

public class SchemaValidationPhase implements Phase
{

    SchemaGenerator schemaGenerator;

    public SchemaValidationPhase(ResourceLoader resourceLoader)
    {
        this.schemaGenerator = new SchemaGenerator(resourceLoader);
    }

    @Override
    public Node apply(Node tree)
    {
        List<SchemaNodeImpl> schemas = tree.findDescendantsWith(SchemaNodeImpl.class);
        for (SchemaNodeImpl schema : schemas)
        {
            try
            {
                if (SchemaGenerator.isXmlSchemaNode(schema))
                {
                    this.schemaGenerator.generateXmlSchema(schema);
                }
                else if (SchemaGenerator.isJsonSchemaNode(schema))
                {
                    this.schemaGenerator.generateJsonSchema(schema);
                }
            }
            catch (JsonParseException ex)
            {
                schema.replaceWith(ErrorNodeFactory.createInvalidSchemaNode(ex.getOriginalMessage()));
            }
            catch (Exception e)
            {
                schema.replaceWith(ErrorNodeFactory.createInvalidSchemaNode(e.getMessage()));
            }
        }
        return tree;
    }
}
