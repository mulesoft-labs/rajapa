/*
 *
 */
package org.raml.nodes;

import java.math.BigInteger;

public interface IntegerNode extends SimpleTypeNode<BigInteger>
{
    BigInteger getValue();
}
