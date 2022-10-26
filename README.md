# Camel DHIS2 Component

The Camel DHIS2 component leverages the [DHIS2 Java SDK](https://github.com/dhis2/dhis2-java-sdk) to integrate [Apache Camel](https://camel.apache.org/) with the [DHIS2 Web API](https://docs.dhis2.org/en/develop/using-the-api/dhis-core-version-master/introduction.html).

# Contents

- [Dependencies](#dependencies)
- [URI Format](#uri-format)
- [Configuring Options](#configuring-options)
  - [Configuring Component Options](#configuring-component-options)
  - [Configuring Endpoint Options](#configuring-endpoint-options)
- [Component Options](#component-options)
- [Endpoint Options](#endpoint-options)
  - [Path Parameters](#path-parameters)
  - [Query Parameters](#query-parameters)
- [API Parameters](#api-parameters)
  - [API: get](#api-get)
  - [API: post](#api-post)
  - [API: resourceTables](#api-resourcetables)
- [Examples](#examples)

## Dependencies

Maven users will need to add the following dependency to their `pom.xml`.

```xml
<dependency>
    <groupId>org.hisp.dhis.integration.camel</groupId>
    <artifactId>camel-dhis2</artifactId>
    <version>${camel.dhis2.version}</version>
</dependency>
```

where `${camel.dhis2.version}` must be replaced by the actual version of the component.

## URI Format

The DHIS2 Component uses the following URI format:

```
dhis2://operation/method[?options]
```

You can append query options to the URI in the following format, ?options=value&option2=value&…​

## Configuring Options

Camel components are configured on two separate levels:

* component level
* endpoint level

### Configuring Component Options

The component level is the highest level which holds general and common configurations that are inherited by the endpoints. For example a component may have security settings, credentials for authentication, urls for network connection and so forth.

Some components only have a few options, and others may have many. Because components typically have pre configured defaults that are commonly used, then you may often only need to configure a few options on a component; or none at all.

Configuring components can be done with the [Component DSL](https://camel.apache.org/manual/component-dsl.html), in a configuration file (application.properties|yaml), or directly with Java code.

### Configuring Endpoint Options

Where you find yourself configuring the most is on endpoints, as endpoints often have many options, which allows you to configure what you need the endpoint to do. The options are also categorized into whether the endpoint is used as consumer (from) or as a producer (to), or used for both.

Configuring endpoints is most often done directly in the endpoint URI as path and query parameters. You can also use the [Endpoint DSL](https://camel.apache.org/manual/Endpoint-dsl.html) as a type safe way of configuring endpoints.

A good practice when configuring options is to use [Property Placeholders](https://camel.apache.org/manual/using-propertyplaceholder.html), which allows to not hardcode urls, port numbers, sensitive information, and other settings. In other words placeholders allows to externalize the configuration from your code, and gives more flexibility and reuse.

The following two sections lists all the options, firstly for the component followed by the endpoint.

## Component Options

The DHIS2 component supports the options listed below.

| Naming              | Description                                                                                                 | Default | Type                                          |
|---------------------|-------------------------------------------------------------------------------------------------------------|---------|-----------------------------------------------|
| baseApiUrl (common) | DHIS2 server base API URL                                                                                   |         | String                                        |
| username (common)   | Username for use for basic authentication                                                                   |         | String                                        |
| password (common)   | Password for use for basic authentication                                                                   |         | String                                        |
| client (advanced)   | Dhis2Client is an expensive object to create. To avoid creating multiple instances, it can be set directly. |         | org.hisp.dhis.integration.sdk.api.Dhis2Client |

## Endpoint Options

The DHIS2 endpoint is configured using URI syntax:

```
dhis2:apiName/methodName
```

with the following path and query parameters:

### Path Parameters

| Naming              | Description                                                                                                                    | Default | Type         |
|---------------------|--------------------------------------------------------------------------------------------------------------------------------|---------|--------------|
| apiName (common)    | **Required** What kind of operation to perform. Enum values: <br/>  <ul><li>GET</li><li>POST</li><li>RESOURCE_TABLES</li></ul> |         | Dhis2ApiName |
| methodName (common) | **Required** What sub operation to use for the selected operation                                                              |         | String       |

### Query Parameters

| Naming              | Description                                                       | Default | Type                                          |
|---------------------|-------------------------------------------------------------------|---------|-----------------------------------------------|
| baseApiUrl (common) | DHIS2 server base API URL                                         |         | String                                        |
| inBody (common)     | Sets the name of a parameter to be passed in the exchange In Body |         | String                                        |
| client (advanced)   | To use the custom client                                          |         | org.hisp.dhis.integration.sdk.api.Dhis2Client |
| password (security) | Password to use for basic authentication                          |         | String                                        |
| username (security) | Username to use for basic authentication                          |         | String                                        |


## API Parameters

The DHIS2 endpoint is an API-based component and has additional parameters based on which API name and API method is used. The API name and API method is located in the endpoint URI as the apiName/methodName path parameters:

```
dhis2:apiName/methodName
```

The API names as listed in the table below:

| API Name       | Type | Description                                                                             |
|----------------|------|-----------------------------------------------------------------------------------------|
| get            | Both | API for the get operation, which fetches a resource or a list resources from the server |
| post           | Both | API for the create operation, which creates a new resource on the server                |
| resourceTables | Both | API for resource and analytic operations                                                |

Each API is documented in the following sections to come.

### API: get

**Both producer and consumer are supported**

The get API is defined in the syntax as follows:

```
dhis2:get/methodName?[parameters]
```

The method(s) is listed in the table below, followed by detailed syntax for each method. (API methods can have a shorthand alias name which can be used in the syntax instead of the name)

| Method     | Alias | Description                  |
|------------|-------|------------------------------|
| resource   |       | Retrieve a resource          |
| collection |       | Retrieve a list of resources |

#### METHOD resource

Signatures:

* java.io.InputStream resource(java.lang.String path, java.lang.String fields, java.lang.String filter, java.util.Map<String, Object> queryParams)

The get/resource API method has the parameters listed in the table below:

| Parameter   | Description                             | Type   |
|-------------|-----------------------------------------|--------|
| path        | Resource URL path                       | String |
| fields      | Comma-delimited list of fields to fetch | String |
| filter      | Search criteria                         | String |
| queryParams | Custom query parameters                 | Map    |

In addition to the parameters above, the get/resource API can also use any of the [Query Parameters](#query-parameters).

Any of the parameters can be provided in either the endpoint URI, or dynamically in a message header. The message header name must be of the format CamelDhis2.parameter. The inBody parameter overrides message header, i.e. the endpoint parameter inBody=myParameterNameHere would override a CamelDhis2.myParameterNameHere header.

#### METHOD collection

Signatures:

* java.util.Iterator collection(java.lang.String path, java.lang.String itemType, java.lang.Boolean paging, java.lang.String fields, java.lang.String filter)

The get/collection API method has the parameters listed in the table below:

| Parameter   | Description                                               | Type    |
|-------------|-----------------------------------------------------------|---------|
| path        | Resource URL path                                         | String  |
| itemType    | Fully-qualified Java class name to deserialise items into | String  |
| paging      | Turn paging on/off                                        | Boolean |
| fields      | Comma-delimited list of fields to fetch                   | String  |
| filter      | Search criteria                                           | String  |
| queryParams | Custom query parameters                                   | Map     |

In addition to the parameters above, the get/collection API can also use any of the [Query Parameters](#query-parameters).

Any of the parameters can be provided in either the endpoint URI, or dynamically in a message header. The message header name must be of the format CamelDhis2.parameter. The inBody parameter overrides message header, i.e. the endpoint parameter inBody=myParameterNameHere would override a CamelDhis2.myParameterNameHere header.

### API: post

**Both producer and consumer are supported**

The post API is defined in the syntax as follows:

```
dhis2:post/methodName?[parameters]
```

#### METHOD resource 

Signatures:

* java.io.InputStream resource(java.lang.String path, java.lang.Object resource, java.util.Map<String, Object queryParams)

The post/resource API method has the parameters listed in the table below:

| Parameter   | Description             | Type   |
|-------------|-------------------------|--------|
| path        | Resource URL path       | String |
| resource    | New resource            | Object |
| queryParams | Custom query parameters | Map    |

In addition to the parameters above, the post/resource API can also use any of the [Query Parameters](#query-parameters).

Any of the parameters can be provided in either the endpoint URI, or dynamically in a message header. The message header name must be of the format CamelDhis2.parameter. The inBody parameter overrides message header, i.e. the endpoint parameter inBody=myParameterNameHere would override a CamelDhis2.myParameterNameHere header.

### API: resourceTables

**Both producer and consumer are supported**

The resourceTables API is defined in the syntax as follows:

```
dhis2:resourceTables/methodName?[parameters]
```

#### METHOD analytics 

Signatures:

* void analytics(java.lang.Boolean skipAggregate, java.lang.Boolean skipEvents, java.lang.Integer lastYears, java.lang.Integer, interval)
  The post/resource API method has the parameters listed in the table below:

The resourceTables/analytics API method has the parameters listed in the table below:

| Parameter     | Description                                             | Type    |
|---------------|---------------------------------------------------------|---------|
| skipAggregate | Skip generation of aggregate data and completeness data | Boolean |
| skipEvents    | Skip generation of event data                           | Boolean |
| lastYears     | Number of last years of data to include                 | Integer |
| interval      | Duration in milliseconds between completeness checks    | Integer |

In addition to the parameters above, the resourceTables/analytics API can also use any of the [Query Parameters](#query-parameters).

Any of the parameters can be provided in either the endpoint URI, or dynamically in a message header. The message header name must be of the format CamelDhis2.parameter. The inBody parameter overrides message header, i.e. the endpoint parameter inBody=myParameterNameHere would override a CamelDhis2.myParameterNameHere header.

## Examples
* Fetch a resource

  ```java
  from("direct:fetch")
  .to("dhis2://get/resource?path=organisationUnits/O6uvpzGd5pu&client=#dhis2Client")
  .unmarshal().json(OrganisationUnit.class);
  ```

* Create a resource

  ```java
  from("direct:create")
  .to("dhis2://post/resource?path=dataValueSets&inBody=resource&client=#dhis2Client");
  ```

* Run analytics

  ```java
  from("direct:analytics")
  .to("dhis2://resourceTables/analytics?&skipEvents=true&lastYears=1&client=#dhis2Client");
  ```