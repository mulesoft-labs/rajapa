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

import java.io.IOException;
import java.util.StringTokenizer;

import org.raml.impl.v10.RamlFragment;

public class RamlHeader
{

    public static final String RAML_10_VERSION = "1.0";
    public static final String RAML_08_VERSION = "0.8";
    public static final String RAML_HEADER_PREFIX = "#%RAML";

    private String version;
    private RamlFragment fragment;

    public RamlHeader(String version, RamlFragment fragment)
    {
        this.version = version;
        this.fragment = fragment;
    }

    public RamlHeader(String version)
    {
        this(version, null);
    }

    public static RamlHeader parse(String stringContent) throws InvalidHeaderException, IOException
    {
        final StringTokenizer lines = new StringTokenizer(stringContent, "\n");
        if (lines.hasMoreElements())
        {
            final String header = lines.nextToken().trim();
            final StringTokenizer headerParts = new StringTokenizer(header);
            if (headerParts.hasMoreTokens())
            {
                final String raml = headerParts.nextToken();
                if (RAML_HEADER_PREFIX.equals(raml))
                {
                    if (headerParts.hasMoreTokens())
                    {
                        final String version = headerParts.nextToken();
                        if (RAML_10_VERSION.equals(version))
                        {
                            final String fragmentText = headerParts.hasMoreTokens() ? headerParts.nextToken() : "";

                            RamlFragment fragment = RamlFragment.byName(fragmentText);
                            if (fragment == null)
                            {
                                throw new InvalidHeaderFragmentException(fragmentText);
                            }

                            return new RamlHeader(RAML_10_VERSION, fragment);
                        }
                        else if (RAML_08_VERSION.equals(version))
                        {
                            return new RamlHeader(RAML_08_VERSION);
                        }
                        else
                        {
                            throw new InvalidHeaderVersionException(version);
                        }
                    }
                }
            }
            throw new InvalidHeaderException(header);
        }
        else
        {
            throw new MissingHeaderException();
        }
    }

    public String getVersion()
    {
        return version;
    }

    public RamlFragment getFragment()
    {
        return fragment;
    }


    public static class InvalidHeaderException extends Exception
    {

        public InvalidHeaderException()
        {
        }

        public InvalidHeaderException(String message)
        {
            super(message);
        }
    }

    public static class InvalidHeaderFragmentException extends InvalidHeaderException
    {

        public InvalidHeaderFragmentException(String fragmentText)
        {
            super(fragmentText);
        }
    }

    public static class InvalidHeaderVersionException extends InvalidHeaderException
    {

        public InvalidHeaderVersionException(String version)
        {
            super(version);
        }
    }

    public static class MissingHeaderException extends InvalidHeaderException
    {

    }
}
