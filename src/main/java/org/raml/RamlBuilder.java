package org.raml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;

import org.raml.loader.ClassPathResourceLoader;
import org.raml.loader.CompositeResourceLoader;
import org.raml.loader.DefaultResourceLoader;
import org.raml.loader.FileResourceLoader;
import org.raml.loader.ResourceLoader;
import org.raml.loader.UrlResourceLoader;
import org.raml.nodes.RamlMappingNode;
import org.raml.nodes.RamlNode;
import org.raml.nodes.RamlRootNode;
import org.raml.utils.StreamUtils;
import org.raml.visitor.IncludeResolverPhase;
import org.raml.visitor.Phase;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

public class RamlBuilder
{

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    public RamlNode build(File ramlFile) throws FileNotFoundException
    {
        resourceLoader = new CompositeResourceLoader(
                new UrlResourceLoader(), new ClassPathResourceLoader(), new FileResourceLoader("."), new FileResourceLoader(ramlFile.getParent()));
        return build(new FileReader(ramlFile), "");
    }

    public RamlNode build(String resourceLocation)
    {
        InputStream inputStream = resourceLoader.fetchResource(resourceLocation);
        return build(StreamUtils.reader(inputStream), resourceLocation);
    }

    public RamlNode build(Reader content, String resourceLocation)
    {
        Yaml yamlParser = new Yaml();
        Node yamlRootNode = yamlParser.compose(content);
        //if (yamlRootNode.getNodeId() != mapping)
        //{
        //    throw new IllegalStateException("Invalid root node");
        //}
        RamlNode rootNode = new RamlRootNode((RamlMappingNode) new SnakeYamlModelWrapper().wrap(yamlRootNode));
        rootNode = resolveIncludes(rootNode, resourceLocation);
        rootNode = resourceNormalization(rootNode);
        return rootNode;
    }

    private RamlNode resourceNormalization(RamlNode rootNode)
    {
        return rootNode;
    }

    private RamlNode resolveIncludes(RamlNode rootNode, String resourceLocation)
    {
        Phase includeResolver = new IncludeResolverPhase(resourceLoader, resourceLocation);
        return apply(rootNode, includeResolver);
    }

    private RamlNode apply(RamlNode rootNode, Phase phase)
    {
        //first pass may replace child nodes
        RamlNode result = phase.apply(rootNode);
        for (RamlNode ramlNode : result.getChildren())
        {
            apply(ramlNode, phase);
        }
        return result;
    }
}
