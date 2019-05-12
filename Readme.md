# Deprecated
I originally created this library, but wanted the same capability for other JDK languages, so created https://github.com/dtanner/env-override.  That library now has more features and is what I'll continue to maintain and improve.

# env-config-loader-groovy

Utility library to override an object's properties with environment variable values.  

[![Build Status](https://travis-ci.org/dtanner/env-config-loader-groovy.svg?branch=master)](https://travis-ci.org/dtanner/env-config-loader-groovy)

Available from the jcenter repository as: compile 'com.edgescope:env-config-loader-groovy:1.0.0'

## Main Purpose
Let you define your application's configuration in a **typed** configuration, 
and allow its settings to be overridden by environment variables.

There are a dozen ways to configure your application, and configuration management is often rife with confusion, rot, and bugs.  

The approach this tool takes is toward the https://12factor.net/config
technique, with the added benefit of using a typed configuration object, which lets you manage your configuration like code. 

The main class/method is `EnvConfigLoader.overrideFromEnvironment(T config, String environmentPrefix)`
where config is some object you've created, used for storing your config settings. 

## Example Usage
See the EnvConfigLoaderSpec and TestAppConfig for more thorough examples, but here's the idea:

Given an object that you used to store your configuration settings, with some local dev/testing defaults:
```groovy
AppConfig {
    String hostName = "test.foo.com"
    String port = 80
}
```

Choose a prefix for your environment-specific overrides.  e.g.: 
`export FOO_HOST_NAME="foo.com"`

Then wherever you initialize your app's startup configuration, do something like this:
```groovy
AppConfig appConfig = EnvConfigLoader.overrideFromEnvironment(new AppConfig(), "FOO") 
```

The AppConfig instance will end up with a hostName of `foo.com` and port of `80`.  
i.e. It will have modified the hostName, and left the port with the original value.

## Requirements, Behaviors, Limitations
- Your configuration object must implement `Cloneable`. `overrideFromEnvironment` will **not** mutate your original object.
- Your property names must strictly match camelCase naming structure.
- It currently only supports a flat set of properties. i.e. It doesn't support nested objects in configuration.  
- It currently supports Strings, Integers, and BigDecimal types.  Other types might work, but aren't tested.  (I'm totally open to suggestion on more types; just haven't seen a need yet.)  

## Dependencies
groovy-all, logback-classic, and spock for testing. 

## Issues / Questions
Please open an issue and let me know if you think something's missing, confusing, or broken.   

