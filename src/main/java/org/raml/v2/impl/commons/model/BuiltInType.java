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
package org.raml.v2.impl.commons.model;

public enum BuiltInType
{
    STRING("string"), NUMBER("number"), INTEGER("integer"), BOOLEAN("boolean"), DATE("date"), FILE("file");

    private final String type;

    BuiltInType(String type)
    {
        this.type = type;
    }

    public static boolean isBuiltInType(String type)
    {
        for (BuiltInType builtInType : values())
        {
            if (builtInType.type.equals(type))
            {
                return true;
            }
        }
        return false;
    }

    public String getType()
    {
        return this.type;
    }
}
