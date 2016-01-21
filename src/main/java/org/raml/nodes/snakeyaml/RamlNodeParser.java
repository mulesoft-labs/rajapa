/*
 *
 */
package org.raml.nodes.snakeyaml;

import org.raml.nodes.Node;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

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
}
