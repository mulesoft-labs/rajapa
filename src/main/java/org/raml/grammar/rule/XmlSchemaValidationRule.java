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
package org.raml.grammar.rule;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.raml.nodes.Node;
import org.raml.nodes.snakeyaml.SYStringNode;
import org.raml.suggester.RamlParsingContext;
import org.raml.suggester.Suggestion;
import org.xml.sax.SAXException;

public class XmlSchemaValidationRule extends Rule
{

    private Schema schema;

    public XmlSchemaValidationRule(String schema)
    {
        try
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            this.schema = factory.newSchema(new StreamSource(new StringReader(schema)));
        }
        catch (SAXException e)
        {
            this.schema = null;
        }
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return Lists.newArrayList();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return false;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (schema == null)
        {
            return ErrorNodeFactory.createInvalidXmlExampleNode("Invalid XmlSchema");
        }
        SYStringNode source = (SYStringNode) node.getSource();
        if (source == null)
        {
            return ErrorNodeFactory.createInvalidXmlExampleNode("Source was null");
        }
        String value = source.getValue();
        try
        {
            schema.newValidator().validate(new StreamSource(new StringReader(value)));
        }
        catch (SAXException | IOException e)
        {
            node.replaceWith(ErrorNodeFactory.createInvalidXmlExampleNode(e.getMessage()));
        }
        return node;
    }

    @Override
    public String getDescription()
    {
        return null;
    }
}
