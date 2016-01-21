/*
 *
 */
package org.raml.nodes;

import java.math.BigDecimal;

public interface FloatingNode extends Node
{
    BigDecimal getValue();
}
