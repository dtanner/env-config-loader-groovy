package com.edgescope.config

import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EnvConfigLoader {

    static final Logger log = LoggerFactory.getLogger(EnvConfigLoader)

    /**
     * Given a class, returns a new instance of that class with overridden properties from environment variables with the given prefix.
     *
     * @param config an object, presumably used for configuration.
     * @param environmentPrefix the prefix used to name the overriding values. e.g. MY_APP
     * @return an instance of the same type, with overridden property values.
     */
    static <T> T overrideFromEnvironment(T config, String environmentPrefix) {

        Map<String, String> envOverridesMap = getenv().findAll { it.key.startsWith(environmentPrefix) }
        Map<String, String> camelOverridesMap = envOverridesMap.collectEntries { Map.Entry<String, String> entry ->
            String camelPropertyName = toLowerCamelCase(entry.key.drop(environmentPrefix.length() + 1).toString())
            return [(camelPropertyName): entry.value]
        } as Map<String, String>

        T overriddenConfig = config.clone() as T

        camelOverridesMap.each { entry ->
            if (!overriddenConfig.hasProperty(entry.key)) {
                log.warn "Environment override for key ${entry.key} found, but no matching property exists."
                return
            }

            if (overriddenConfig[entry.key] instanceof Integer) {
                InvokerHelper.setProperty(overriddenConfig, entry.key, entry.value.toInteger())
            } else if (overriddenConfig[entry.key] instanceof BigDecimal) {
                InvokerHelper.setProperty(overriddenConfig, entry.key, entry.value.toBigDecimal())
            } else {
                InvokerHelper.setProperty(overriddenConfig, entry.key, entry.value)
            }
        }

        return overriddenConfig
    }

    // thin wrapper for easier testing
    protected static Map<String, String> getenv() {
        System.getenv()
    }

    /**
     * Given e.g. FOO or FOO_BAR, results in foo or fooBar
     * @param upperUnderscoreString
     * @return the string in lowerCamelCase
     */
    protected static String toLowerCamelCase(String upperUnderscoreString) {
        String lowerInput = upperUnderscoreString.toLowerCase()

        StringBuilder stringBuilder = new StringBuilder(lowerInput.size())
        char underscore = '_'
        for (int index = 0; index < lowerInput.length(); index++) {
            char c = lowerInput.charAt(index)
            if (c == underscore && index > 0) {
                index++
                stringBuilder << lowerInput.charAt(index).toUpperCase()
            } else {
                stringBuilder << c
            }

        }

        return stringBuilder.toString()
    }
}
