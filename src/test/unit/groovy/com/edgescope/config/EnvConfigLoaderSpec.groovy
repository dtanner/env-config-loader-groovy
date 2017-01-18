package com.edgescope.config

import spock.lang.Specification
import spock.lang.Unroll

class EnvConfigLoaderSpec extends Specification {

    def "non-overridden values remain"() {
        given:
        TestAppConfig appConfig = new TestAppConfig()
        GroovySpy(EnvConfigLoader, global: true)

        when:
        appConfig = EnvConfigLoader.overrideFromEnvironment(appConfig, 'TEST')

        then:
        1 * EnvConfigLoader.getenv() >> [TEST_STRING_VALUE: "b"]
        appConfig.untouchedValue == "untouched"
    }

    @Unroll
    def "#propertyName type override"() {
        given:
        TestAppConfig appConfig = new TestAppConfig()
        GroovySpy(EnvConfigLoader, global: true)

        when:
        appConfig = EnvConfigLoader.overrideFromEnvironment(appConfig, 'TEST')

        then:
        1 * EnvConfigLoader.getenv() >> environmentMap
        appConfig.getProperty(propertyName) == expectedValue

        where:
        propertyName      | environmentMap                  | expectedValue
        'stringValue'     | [TEST_STRING_VALUE: "b"]        | 'b'
        'intValue'        | [TEST_INT_VALUE: "2"]           | 2
        'bigDecimalValue' | [TEST_BIG_DECIMAL_VALUE: "2.0"] | 2.0
    }

    def "extra environment values don't cause an error (e.g. when phasing out properties from the code)"() {
        given:
        TestAppConfig appConfig = new TestAppConfig()
        GroovySpy(EnvConfigLoader, global: true)

        when:
        appConfig = EnvConfigLoader.overrideFromEnvironment(appConfig, 'TEST')

        then:
        1 * EnvConfigLoader.getenv() >> [TEST_UNKNOWN_VALUE: "a"]
        appConfig.untouchedValue == "untouched"
        noExceptionThrown()
    }

    @Unroll
    def "toLowerCamelCase for #input should output #output"() {
        expect:
        EnvConfigLoader.toLowerCamelCase(input) == output

        where:
        input     | output
        'FOO'     | 'foo'
        'FOO_BAR' | 'fooBar'
    }
}
