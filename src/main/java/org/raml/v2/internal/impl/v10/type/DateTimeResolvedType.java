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
package org.raml.v2.internal.impl.v10.type;

import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import static org.raml.v2.internal.utils.NodeSelector.selectStringValue;

public class DateTimeResolvedType extends XmlFacetsCapableType
{

    private String format;

    public DateTimeResolvedType(TypeDeclarationNode declarationNode, XmlFacets xmlFacets, String format)
    {
        super(declarationNode, xmlFacets);
        this.format = format;
    }

    public DateTimeResolvedType(TypeDeclarationNode from)
    {
        super(from);
    }

    protected DateTimeResolvedType copy()
    {
        return new DateTimeResolvedType(getTypeDeclarationNode(), getXmlFacets().copy(), format);
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final DateTimeResolvedType result = copy();
        result.setFormat(selectStringValue("format", from));
        return overwriteFacets(result, from);
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        final DateTimeResolvedType result = copy();
        if (with instanceof DateTimeResolvedType)
        {
            result.setFormat(((DateTimeResolvedType) with).getFormat());
        }
        return mergeFacets(result, with);
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitDateTime(this);
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        if (format != null)
        {
            this.format = format;
        }
    }
}
