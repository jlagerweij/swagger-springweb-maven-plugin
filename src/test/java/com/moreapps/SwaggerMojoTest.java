package com.moreapps;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SwaggerMojoTest {

    @Test
    public void testGenerate() throws MojoExecutionException {
        SwaggerMojo swaggerMojo = new SwaggerMojo();
        swaggerMojo.setTitle("Cars API");
        swaggerMojo.setDescription("API for Cars.");
        swaggerMojo.setTermsOfServiceUrl("http://www.morecars.nl");
        swaggerMojo.setContact("test@test.com");
        swaggerMojo.setLicense("Commercial Cars License");
        swaggerMojo.setLicenseUrl("http://www.morecars.nl/cars-license.html");

        swaggerMojo.setBaseControllerPackage("org.example");
        swaggerMojo.setBaseModelPackage("org.example.model");
        swaggerMojo.setBasePath("/newapidocs");
        swaggerMojo.setServletPath("http://localhost/newapidocs");
        swaggerMojo.setApiVersion("v1.0");

        swaggerMojo.setOutputDirectory(new File("target"));

        swaggerMojo.execute();

        assertThat(new File("target/service.json").exists(), is(true));
        assertThat(new File("target/cars.json").exists(), is(true));
    }

}
