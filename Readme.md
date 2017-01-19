# env-config-loader-groovy

Utility library to override an object's properties with environment variable values.  

## Main Purpose
Let you define your application's configuration in a *typed* configuration, 
and allow its settings to be overridden by environment variables.

There are a dozen ways to configure your application, and configuration management is often rife confusion, rot, and bugs.  

The approach this tool takes is toward the https://12factor.net/config
technique, but with the added benefit of using a typed configuration object, which lets you manage your configuration like code.  

The goal of this tool is to stay small and focused for the above purpose.  If you're looking for file overrides or other 

The main class/method is EnvConfigLoader.overrideFromEnvironment(T config, String environmentPrefix)
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

Choose a prefix for your environment-specific overrides.  e.g. Choosing FOO as the prefix: 
`export FOO_HOST_NAME="foo.com"`

Then in your Registry or wherever you initialize your configuration, do something like this:
```groovy
AppConfig appConfig = EnvConfigLoader.overrideFromEnvironment(new AppConfig(), "FOO") 
```

Your AppConfig will end up with a hostName of `foo.com` and port of `80`.  
i.e. It will have modified the hostName, and left the port with the original value.

## Requirements, Behaviors, Limitations
- Your configuration object must implement `Cloneable`.
- Your property names must strictly match camelCase naming structure. 
- It currently only supports a flat set of properties. i.e. It doesn't support nested objects in configuration. 
That would be nice in some ways, but environment variables don't seem to match a configuration hierarchy approach.
- It currently supports Strings, Integers, and BigDecimal types.  Other types might work, but aren't tested.  (Totally open to suggestion on more types; just haven't seen a need.)  

## Dependencies
groovy-all, logback-classic, and spock for testing.

## FAQs

##### Why not just use a ConfigObject for configuration?
- How do you know where a config property is being used?  Or not used at all?  Or if it's set to the wrong type? And *when* do you tend to discover these problems?

## Issues / Questions
Please open an issue and let me know if you think something's missing, confusing, or broken.   

