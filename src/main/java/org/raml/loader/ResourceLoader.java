/*
 *
 */
package org.raml.loader;

import java.io.InputStream;

public interface ResourceLoader
{

    InputStream fetchResource(String resourceName);

}
