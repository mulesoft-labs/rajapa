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
package org.raml.nodes.snakeyaml;

import org.raml.nodes.Node;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.*;

public class RamlNodeParser
{

    @Nullable
    public static Node parse(InputStream inputStream)
    {
        try
        {
            return parse(new InputStreamReader(inputStream, "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            return parse(new InputStreamReader(inputStream));
        }
    }


    @Nullable
    public static Node parse(Reader reader)
    {
        Yaml yamlParser = new Yaml();
        org.yaml.snakeyaml.nodes.Node composedNode = yamlParser.compose(reader);
        if (composedNode == null)
        {
            return null;
        }
        else
        {
            return new SYModelWrapper().wrap(composedNode);
        }
    }

    @Nullable
    public static Node parse(String content)
    {
        Yaml yamlParser = new Yaml();
        org.yaml.snakeyaml.nodes.Node composedNode = yamlParser.compose(new StringReader(content));
        if (composedNode == null)
        {
            return null;
        }
        else
        {
            return new SYModelWrapper().wrap(composedNode);
        }
    }
}
