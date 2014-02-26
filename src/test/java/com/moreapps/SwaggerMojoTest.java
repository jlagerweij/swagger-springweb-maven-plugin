package com.moreapps;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moreapps.swagger.Service;
import com.moreapps.swagger.ServiceApiDetail;
import com.moreapps.swagger.ServiceModelProperty;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SwaggerMojoTest {

    @Test
    public void testGenerate() throws MojoExecutionException, IOException {
        SwaggerMojo swaggerMojo = new SwaggerMojo();
        swaggerMojo.setTitle("Cars API");
        swaggerMojo.setDescription("API for Cars.");
        swaggerMojo.setTermsOfServiceUrl("http://www.morecars.nl");
        swaggerMojo.setContact("test@test.com");
        swaggerMojo.setLicense("Commercial Cars License");
        swaggerMojo.setLicenseUrl("http://www.morecars.nl/cars-license.html");

        swaggerMojo.setBaseControllerPackage("org.example");
        swaggerMojo.setBasePath("/newapidocs");
        swaggerMojo.setApiVersion("v1.0");

        swaggerMojo.setOutputDirectory(new File("target"));

        swaggerMojo.execute();

        assertThat(new File("target/service.json").exists(), is(true));
        assertThat(new File("target/cars.json").exists(), is(true));

        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper(jsonFactory);

        Service service = objectMapper.readValue(new File("target/service.json"), Service.class);
        assertThat(service.getBasePath(), is("/newapidocs"));
        assertThat(service.getApis().get(0).getPath(), is("/cars.{format}"));
        assertThat(service.getApis().get(1).getPath(), is("/cars_{carId}_wheels.{format}"));
        assertThat(service.getApis().get(2).getPath(), is("/v1_0_users.{format}"));
        assertThat(service.getApis().get(3).getPath(), is("/vehicle.{format}"));

        ServiceApiDetail carsApiDetails = objectMapper.readValue(new File("target/cars.json"), ServiceApiDetail.class);
        assertThat(carsApiDetails.getApis().get(0).getOperations().get(0).getMethod(), is("DELETE"));
        assertThat(carsApiDetails.getApis().get(0).getOperations().get(0).getResponseClass(), is("Car"));

        ServiceModelProperty wheels = carsApiDetails.getModels().get("Car").getProperties().get("wheels");
        assertThat(wheels.getType(), is("array"));
        assertThat(wheels.getItems().get("$ref"), is("Wheel"));

        ServiceApiDetail vehicleApiDetails = objectMapper.readValue(new File("target/vehicle.json"), ServiceApiDetail.class);
        assertThat(vehicleApiDetails.getModels().get("Vehicle").getDiscriminator(), is("type"));
        assertThat(vehicleApiDetails.getModels().get("Vehicle").getSubTypes().get(0), is("Car"));
        assertThat(vehicleApiDetails.getModels().get("Vehicle").getSubTypes().get(1), is("Bike"));

        ServiceApiDetail users = objectMapper.readValue(new File("target/v1_0_users.json"), ServiceApiDetail.class);
        assertThat(users.getApis().size(), is(5));
    }

}
