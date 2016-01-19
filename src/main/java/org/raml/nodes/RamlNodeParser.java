package org.raml.nodes;

import org.raml.nodes.snakeyaml.SYModelWrapper;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class RamlNodeParser {

    @Nullable
    public static RamlNode parse(InputStream inputStream) {
        try {
            return parse(new InputStreamReader(inputStream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return parse(new InputStreamReader(inputStream));
        }
    }


    @Nullable
    public static RamlNode parse(Reader reader) {
        Yaml yamlParser = new Yaml();
        Node composedNode = yamlParser.compose(reader);
        if (composedNode == null) {
            return null;
        } else {
            return new SYModelWrapper().wrap(composedNode);
        }
    }
}
