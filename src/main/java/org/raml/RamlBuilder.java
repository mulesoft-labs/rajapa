package org.raml;

import org.raml.loader.*;
import org.raml.nodes.RamlNode;
import org.raml.nodes.RamlNodeParser;
import org.raml.phase.TransformationPhase;
import org.raml.phase.transformer.IncludeResolver;
import org.raml.utils.StreamUtils;

import java.io.*;

public class RamlBuilder {

    private ResourceLoader resourceLoader;

    public RamlBuilder() {
        this.resourceLoader = new DefaultResourceLoader();
    }

    public RamlNode build(File ramlFile) throws FileNotFoundException {
        resourceLoader = new CompositeResourceLoader(
                new UrlResourceLoader(), new ClassPathResourceLoader(), new FileResourceLoader("."), new FileResourceLoader(ramlFile.getParent()));
        return build(new FileReader(ramlFile), "");
    }

    public RamlNode build(String resourceLocation) {
        InputStream inputStream = resourceLoader.fetchResource(resourceLocation);
        return build(StreamUtils.reader(inputStream), resourceLocation);
    }

    public RamlNode build(Reader content, String resourceLocation) {
        RamlNode rootNode = RamlNodeParser.parse(content);
        //The first phase expands the includes.
        final TransformationPhase firstPhase = new TransformationPhase(new IncludeResolver(resourceLoader, resourceLocation));
        rootNode = firstPhase.apply(rootNode);

        //Runs grammar. Applies the Raml rules and changes each node for a more specific.

        //Detect references and mark invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        //Normalize resources and detects duplicated ones and more than one use of url parameters.

        //Applies resourceTypes and Traits Library



        return rootNode;
    }


}
