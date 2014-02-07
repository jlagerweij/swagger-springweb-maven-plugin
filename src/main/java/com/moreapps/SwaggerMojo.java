package com.moreapps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.moreapps.swagger.Service;
import com.moreapps.swagger.ServiceApi;
import com.moreapps.swagger.ServiceApiDetail;
import com.moreapps.swagger.ServiceInfo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;

/**
 * @goal generate
 * @phase compile
 * @configurator include-project-dependencies
 * @requiresDependencyResolution runtime
 */
@Mojo(name = "swagger-springweb",
        defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        requiresDependencyResolution = ResolutionScope.RUNTIME,
        configurator = "include-project-dependencies")
public class SwaggerMojo extends AbstractMojo {
    @Parameter(required = true)
    private String title;
    @Parameter(required = true)
    private String description;
    @Parameter(required = true)
    private String termsOfServiceUrl;
    @Parameter(required = true)
    private String contact;
    @Parameter(required = true)
    private String license;
    @Parameter(required = true)
    private String licenseUrl;

    @Parameter(required = true)
    private String baseControllerPackage;
    @Parameter(required = true)
    private String baseModelPackage;
    @Parameter(required = true)
    private String basePath;
    @Parameter(required = true)
    private String servletPath;
    @Parameter(required = true)
    private String apiVersion;
    @Parameter(required = true)
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
            ensureDirectoryExists(outputDirectory);

            ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
            File serviceFile = new File(outputDirectory, "service.json");
            System.out.println("Writing to file " + serviceFile);
            objectWriter.writeValue(serviceFile, service);

            for (ServiceApi serviceApi : service.getApis()) {
                ServiceApiDetail details = serviceApi.getDetails();

                if (details.getResourcePath() == null) {
                    throw new MojoExecutionException("ResourcePath of " + serviceApi.getPath() + " cannot be null");
                }
                File detailOutputFile = new File(outputDirectory, details.getResourcePath() + ".json");
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
