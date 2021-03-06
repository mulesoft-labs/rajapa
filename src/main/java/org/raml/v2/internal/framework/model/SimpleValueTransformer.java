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


import org.raml.v2.internal.framework.nodes.BooleanNode;
import org.raml.v2.internal.framework.nodes.IntegerNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.SimpleTypeNode;

public enum SimpleValueTransformer
{

    STRING
    {
        @Override
        public boolean accepts(Class<?> type)
        {
            return String.class.isAssignableFrom(type);
        }

        @Override
        public String adaptTo(Node valueNode, Class<?> returnType)
        {
            if (valueNode == null)
            {
                return null;
            }
            if (valueNode instanceof SimpleTypeNode)
            {
                return ((SimpleTypeNode) valueNode).getLiteralValue();
            }
            else
            {
                throw new RuntimeException("Invalid node type " + valueNode.getType() + " expecting a simple type.");
            }

        }
    },
    BOOLEAN
    {
        @Override
        public boolean accepts(Class<?> type)
        {
            return Boolean.class.isAssignableFrom(type) || Boolean.TYPE.equals(type);
        }

        @Override
        public Boolean adaptTo(Node valueNode, Class<?> returnType)
        {
            if (valueNode == null)
            {
                return false;
            }
            if (valueNode instanceof BooleanNode)
            {
                return ((BooleanNode) valueNode).getValue();
            }
            if (valueNode instanceof SimpleTypeNode)
            {
                return Boolean.parseBoolean(((SimpleTypeNode) valueNode).getLiteralValue());
            }
            else
            {
                throw new RuntimeException("Invalid node type " + valueNode.getType() + " expecting a simple type.");
            }

        }
    },
    INT
    {
        @Override
        public boolean accepts(Class<?> type)
        {
            return Integer.class.isAssignableFrom(type) || Integer.TYPE.equals(type);
        }

        @Override
        public Integer adaptTo(Node valueNode, Class<?> returnType)
        {
            if (valueNode == null)
            {
                return 0;
            }
            if (valueNode instanceof IntegerNode)
            {
                return ((IntegerNode) valueNode).getValue();
            }
            if (valueNode instanceof SimpleTypeNode)
            {
                return Integer.parseInt(((SimpleTypeNode) valueNode).getLiteralValue());
            }
            else
            {
                throw new RuntimeException("Invalid node type " + valueNode.getType() + " expecting a simple type.");
            }

        }
    },
    FLOAT
    {
        @Override
        public boolean accepts(Class<?> type)
        {
            return Float.class.isAssignableFrom(type) || Float.TYPE.equals(type);
        }

        @Override
        public Float adaptTo(Node valueNode, Class<?> returnType)
        {
            if (valueNode == null)
            {
                return 0.0f;
            }
            if (valueNode instanceof SimpleTypeNode)
            {
                return Float.parseFloat(((SimpleTypeNode) valueNode).getLiteralValue());
            }
            else
            {
                throw new RuntimeException("Invalid node type " + valueNode.getType() + " expecting a simple type.");
            }

        }
    },

    ENUM
    {
        @Override
        public boolean accepts(Class<?> type)
        {
            return type.isEnum();
        }

        @Override
        public Object adaptTo(Node valueNode, Class<?> returnType)
        {
            if (valueNode == null)
            {
                return null;
            }
            if (valueNode instanceof SimpleTypeNode)
            {
                final String literalValue = ((SimpleTypeNode) valueNode).getLiteralValue();
                return Enum.valueOf((Class<? extends Enum>) returnType, literalValue);
            }
            else
            {
                throw new RuntimeException("Invalid node type " + valueNode.getType() + " expecting a simple type.");
            }

        }
    };

    public abstract Object adaptTo(Node valueNode, Class<?> returnType);

    public abstract boolean accepts(Class<?> type);
}
