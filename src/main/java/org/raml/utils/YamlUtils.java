package org.raml.utils;

public class YamlUtils {
    public static boolean isYamlSignificantChar(char character) {
        return character == ':' || character== '-' || character== '\n' || character == ',';
    }
}
