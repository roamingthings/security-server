package de.roamingthings.net;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/06/04
 */
public class UrlQueryParser {
    public static Map<String, List<String>> extractParametersFromUrl(URL url) {
        final String query = url.getQuery();

        return extractParametersFromFormEncodedParameterList(query);
    }

    public static Map<String, List<String>> extractParametersFromFormEncodedParameterList(String parameterQuery) {
        if (parameterQuery == null || parameterQuery.isEmpty()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(parameterQuery.split("&"))
                .map(UrlQueryParser::splitQueryParameter)
                .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    public static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }
}
