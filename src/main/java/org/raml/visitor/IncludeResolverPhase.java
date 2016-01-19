package org.raml.visitor;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.raml.SnakeYamlModelWrapper;
import org.raml.loader.ResourceLoader;
import org.raml.nodes.RamlErrorNode;
import org.raml.nodes.RamlIncludeNode;
import org.raml.nodes.RamlNode;
import org.raml.utils.StreamUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class IncludeResolverPhase implements Phase
{

    private final ResourceLoader resourceLoader;
    private final String resourceLocation;

    public IncludeResolverPhase(ResourceLoader resourceLoader, String resourceLocation)
    {
        this.resourceLoader = resourceLoader;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public RamlNode apply(RamlNode node)
    {
        if (!(node instanceof RamlIncludeNode))
        {
            return node;
        }
        RamlIncludeNode includeNode = (RamlIncludeNode) node;
        String resourcePath = resolvePath(includeNode.getIncludePath());
        InputStream inputStream = resourceLoader.fetchResource(resourcePath);
        Node composedNode = null;
        RamlNode result;

        if (inputStream == null)
        {
            String msg = "Include cannot be resolved: " + resourcePath;
            result = new RamlErrorNode(msg);
            node.replaceWith(result);
        }
        else if (resourcePath.endsWith(".raml") || resourcePath.endsWith(".yaml") || resourcePath.endsWith(".yml"))
        {
            Yaml yamlParser = new Yaml();
            composedNode = yamlParser.compose(new InputStreamReader(inputStream));
        }
        else //scalar value
        {
            String newValue = StreamUtils.toString(inputStream);
            composedNode = new ScalarNode(org.yaml.snakeyaml.nodes.Tag.STR, newValue, node.getStartMark(), node.getEndMark(), null);
        }
        if (composedNode == null)
        {
            String msg = "Include file is empty: " + resourcePath;
            result = new RamlErrorNode(msg);
            node.replaceWith(result);
        }
        else
        {
            result = new SnakeYamlModelWrapper().wrap(composedNode);
            node.replaceWith(result);
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
