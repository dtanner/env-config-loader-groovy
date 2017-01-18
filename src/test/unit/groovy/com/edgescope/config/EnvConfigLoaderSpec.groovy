package com.edgescope.config

import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

class EnvConfigLoaderSpec extends Specification {

    @ConfineMetaClassChanges(EnvConfigLoader)
    def "Load"() {
        when: "a default appConfig is loaded"

        TestAppConfig appConfig = new TestAppConfig()

        then: "it contains some defaults specified in the class"
        appConfig.stringValue == 'test'

        when: "we override with an known environment variable"
        EnvConfigLoader.metaClass.static.getenv = { [TEST_STRING_VALUE: "b", TEST_INT_VALUE: "2", TEST_BIG_DECIMAL_VALUE: "2.0"] }
        appConfig = EnvConfigLoader.overrideFromEnvironment(appConfig, 'TEST')

        then: "non-overridden values are left alone"
        appConfig.untouchedValue == "untouched"

        and: "other values are properly overridden"
        appConfig.stringValue == 'b'
        appConfig.intValue == 2
        appConfig.bigDecimalValue == 2.0
    }
}
