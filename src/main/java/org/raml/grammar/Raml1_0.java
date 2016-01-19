package org.raml.grammar;

import org.raml.nodes.impl.RamlDocumentNode;

import static org.raml.grammar.Rules.*;

public class Raml1_0 {

//    public Transformer raml(){
//        mapping()
//                .field(string("title"), string(),true)
//                .field(string("version"), string(),true)
//                .field(new ResourceRule(), true).then(ResourceFieldNode.class);
//
//    }
//      public Transformer resource(){
//           mapping()
//              .field(anyOf("get","post","put"...),string())
//              .field("description()", string())
//              .field(self())


    public static Rule raml() {
        return mapping(RamlDocumentNode.class)
                .addSubRule(field(string("title"), stringType()));
    }
}

