package org.raml.phase.transformer;

import org.raml.loader.ResourceLoader;
import org.raml.nodes.RamlErrorNode;
import org.raml.nodes.RamlNode;
import org.raml.nodes.impl.RamlStringNodeImpl;
import org.raml.nodes.snakeyaml.SYIncludeNode;
import org.raml.nodes.RamlNodeParser;
import org.raml.phase.Transformer;
import org.raml.utils.StreamUtils;

import java.io.InputStream;


public class IncludeResolver implements Transformer
{

    private final ResourceLoader resourceLoader;
    private final String resourceLocation;

    public IncludeResolver(ResourceLoader resourceLoader, String resourceLocation)
    {
        this.resourceLoader = resourceLoader;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public boolean matches(RamlNode tree) {
        return tree instanceof SYIncludeNode;
    }

    @Override
    public RamlNode transform(RamlNode tree)
    {

        SYIncludeNode includeNode = (SYIncludeNode) tree;
        String resourcePath = resolvePath(includeNode.getIncludePath());
        InputStream inputStream = resourceLoader.fetchResource(resourcePath);

        RamlNode result;
        if (inputStream == null)
        {
            String msg = "Include cannot be resolved: " + resourcePath;
            result = new RamlErrorNode(msg);
        }
        else if (resourcePath.endsWith(".raml") || resourcePath.endsWith(".yaml") || resourcePath.endsWith(".yml"))
        {
            result = RamlNodeParser.parse(inputStream);
        }
        else //scalar value
        {
            String newValue = StreamUtils.toString(inputStream);
            result = new RamlStringNodeImpl(newValue);
        }

        if (result == null)
        {
            String msg = "Include file is empty: " + resourcePath;
            result = new RamlErrorNode(msg);
        }

        return result;
    }

    private String resolvePath(String includePath)
    {
        //TODO works for relative only for now
        int lastSlash = resourceLocation.lastIndexOf("/");
        if (lastSlash != -1)
        {
            return resourceLocation.substring(0, lastSlash + 1) + includePath;
        }
        return includePath;
    }
}
