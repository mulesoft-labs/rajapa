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
package org.raml.impl.commons;

public enum RamlVersion
{
    RAML_08("0.8"),
    RAML_10("1.0");

    public final String number;

    public static RamlVersion parse(String version)
    {
        for (RamlVersion item : values())
        {
            if (item.number.equals(version))
            {
                return item;
            }
        }
        throw new IllegalArgumentException("Invalid version " + version);
    }

    RamlVersion(String number)
    {
        this.number = number;
    }

}