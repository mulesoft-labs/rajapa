/*
 *
 */
package org.raml.loader;

import java.io.File;
import java.io.InputStream;

public class DefaultResourceLoader implements ResourceLoader
{

    private ResourceLoader resourceLoader;

    public DefaultResourceLoader()
    {
        resourceLoader = new CompositeResourceLoader(
                new UrlResourceLoader(), new ClassPathResourceLoader(), new FileResourceLoader("."), new FileResourceLoader((File) null));
    }

    @Override
    public InputStream fetchResource(String resourceName)
    {
        return resourceLoader.fetchResource(resourceName);
    }
}
