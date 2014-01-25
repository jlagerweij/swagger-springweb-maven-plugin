package com.moreapps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.knappsack.swagger4springweb.parser.ApiParser;
import com.knappsack.swagger4springweb.util.ScalaObjectMapper;
import com.moreapps.swagger.Service;
import com.moreapps.swagger.ServiceApi;
import com.moreapps.swagger.ServiceApiDetail;
import com.moreapps.swagger.ServiceInfo;
import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Goal which touches a timestamp file.
 *
 */
@Mojo(name = "swagger-springweb-maven-plugin", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class SwaggerMojo extends AbstractMojo {
    private String title;
    private String description;
    private String termsOfServiceUrl;
    private String contact;
    private String license;
    private String licenseUrl;

    private String baseControllerPackage;
    private String baseModelPackage;
    private String basePath;
    private String servletPath;
    private String apiVersion;
    private boolean ignoreUnusedPathVariables;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        SpringMvcParser springMvcParser = new SpringMvcParser();
        springMvcParser.setApiVersion(apiVersion);
        springMvcParser.setBasePath(basePath);

        Service service = springMvcParser.parse(baseControllerPackage);

        ServiceInfo info = new ServiceInfo();
        info.setTitle(title);
        info.setDescription(description);
        info.setTermsOfServiceUrl(termsOfServiceUrl);
        info.setContact(contact);
        info.setLicense(license);
        info.setLicenseUrl(licenseUrl);
        service.setInfo(info);

        try {
            File baseOutput = new File("tt");
            ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
            File serviceFile = new File(baseOutput, "service.json");
            System.out.println("Writing to file " + serviceFile);
            objectWriter.writeValue(serviceFile, service);

            for (ServiceApi serviceApi : service.getApis()) {
                ServiceApiDetail details = serviceApi.getDetails();

                if (details.getResourcePath() == null) {
                    throw new MojoExecutionException("ResourcePath of " + serviceApi.getPath() + " cannot be null");
                }
                File detailOutputFile = new File(baseOutput, details.getResourcePath() + ".json");
                System.out.println("Writing to file " + detailOutputFile);
                ensureDirectoryExists(detailOutputFile.getParentFile());

                objectWriter.writeValue(detailOutputFile, details);
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void ensureDirectoryExists(File directory) throws MojoExecutionException {
        if (directory.mkdirs()) {
            System.out.println("Created directory " + directory);
        }
        if (!directory.exists()) {
            throw new MojoExecutionException("Could not output to directory for output: " + directory);
        }
    }

    public void execute1() throws MojoExecutionException {
        ApiInfo apiInfo = new ApiInfo(title, description, termsOfServiceUrl, contact, license, licenseUrl);
        List<String> baseControllerPackages = new ArrayList<String>();
        baseControllerPackages.add(baseControllerPackage);
        List<String> baseModelPackages = new ArrayList<String>();
        baseModelPackages.add(baseModelPackage);
        List<String> ignorableAnnotations = new ArrayList<String>();

        ApiParser apiParser = new CustomApiParser(apiInfo, baseControllerPackages, baseModelPackages, basePath,
                servletPath, apiVersion, ignorableAnnotations, ignoreUnusedPathVariables);

        Map<String,ApiListing> apiListings = apiParser.createApiListings();

        ResourceListing resourceList = apiParser.getResourceListing(apiListings);

        ScalaObjectMapper mapper = new ScalaObjectMapper();
        try {
            for (String apiLocation : apiListings.keySet()) {
                File apiLocationOutputFile = new File(outputDirectory, apiLocation + ".json");
                if(apiLocationOutputFile.getParentFile().mkdirs()) {
                    System.out.println("Created directory " + apiLocationOutputFile.getParentFile());
                }
                if (!apiLocationOutputFile.getParentFile().exists()) {
                    throw new MojoExecutionException("Could not output to directory for output: " + apiLocationOutputFile.getParentFile());
                }
                mapper.writerWithDefaultPrettyPrinter().writeValue(apiLocationOutputFile, apiListings.get(apiLocation));
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputDirectory, "service.json"), resourceList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public void setBaseControllerPackage(String baseControllerPackage) {
        this.baseControllerPackage = baseControllerPackage;
    }

    public void setBaseModelPackage(String baseModelPackage) {
        this.baseModelPackage = baseModelPackage;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public void setIgnoreUnusedPathVariables(boolean ignoreUnusedPathVariables) {
        this.ignoreUnusedPathVariables = ignoreUnusedPathVariables;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
