package com.edgescope.config

import com.google.common.base.CaseFormat
import org.codehaus.groovy.runtime.InvokerHelper

class EnvConfigLoader {
    static <T> T overrideFromEnvironment(T config, String environmentPrefix) {

        Map<String, String> envOverridesMap = getenv().findAll { it.key.startsWith(environmentPrefix) }
        Map<String, String> camelOverridesMap = envOverridesMap.collectEntries { Map.Entry<String, String> entry ->
            String camelPropertyName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, entry.key.drop(environmentPrefix.length() + 1).toString())
            return [(camelPropertyName): entry.value]
        } as Map<String, String>

        T overriddenConfig = config.clone() as T

        camelOverridesMap.each { entry ->
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
}
