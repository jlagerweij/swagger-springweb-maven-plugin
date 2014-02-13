# Swagger SpringWeb Maven plugin


## Setup guide
Use Maven to build.

```
mvn clean install
```

## How to use the plugin
Integrate the swagger-springweb-maven-plugin in your project by adding 

```
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>com.moreapps</groupId>
                <artifactId>swagger-springweb-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <title>Acme Services API</title>
                    <description>Programming API for the Acme platform.</description>
                    <termsOfServiceUrl>http://www.acme.com</termsOfServiceUrl>
                    <contact>support@acme.com</contact>
                    <license>Commercial</license>
                    <licenseUrl>http://www.acme.com/license</licenseUrl>
                    <baseControllerPackage>com.acme.api.endpoints</baseControllerPackage>
                    <basePath>/docs</basePath>
                    <apiVersion>v1.0</apiVersion>
                    <outputDirectory>${project.build.outputDirectory}/docs</outputDirectory>
                </configuration>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>swagger-springweb</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

In the compile phase the `service.json` and the other JSON files are generated to the `outputDirectory`. You need to place the JSON files in a webserver for use.



## Swagger-UI
Download Swagger UI from: https://github.com/wordnik/swagger-ui/tree/master/dist
Place it in an Apache web server folder.
In the `index.html` file you can change the default url to location of the generated JSON files:

```
url: "http://localhost/swagger-files/service.json"
```

Apache might also need access control set, to allow for localhost to retrieve the JSON files:

```
Header add Access-Control-Allow-Origin "*"
Header add Access-Control-Allow-Headers "origin, x-requested-with, content-type"
Header add Access-Control-Allow-Methods "PUT, GET, POST, DELETE, OPTIONS"
```
