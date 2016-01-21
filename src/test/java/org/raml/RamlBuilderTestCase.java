/*
 *
 */
package org.raml;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.text.IsEqualIgnoringWhiteSpace;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.raml.nodes.Node;
import org.raml.utils.TreeDumper;

@RunWith(Parameterized.class)
public class RamlBuilderTestCase
{

    private File input;
    private File expected;
    private String name;

    public RamlBuilderTestCase(File input, File expected, String name)
    {
        this.input = input;
        this.expected = expected;
        this.name = name;
    }

    @Test
    public void runTest() throws IOException
    {
        final RamlBuilder builder = new RamlBuilder();
        final Node raml = builder.build(input);
        assertThat(raml, notNullValue());
        String dump = new TreeDumper().dump(raml);
        String expected = IOUtils.toString(new FileInputStream(this.expected));
        System.out.println("dump = \n" + dump);
        Assert.assertThat(dump, IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(expected));

    }


    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> data() throws URISyntaxException
    {
        final URI baseFolder = RamlBuilderTestCase.class.getResource("").toURI();
        final File testFolder = new File(baseFolder);
        final File[] scenarios = testFolder.listFiles();
        List<Object[]> result = new ArrayList<>();
        for (File scenario : scenarios)
        {
            if (scenario.isDirectory())
            {
                result.add(new Object[] {
                                         new File(scenario, "input.raml"),
                                         new File(scenario, "output.txt"),
                                         scenario.getName()
                });
            }
        }
        return result;
    }


}
