package com.moreapps;

import com.knappsack.swagger4springweb.parser.ApiParser;
import com.knappsack.swagger4springweb.util.JavaToScalaUtil;
import com.knappsack.swagger4springweb.util.ScalaObjectMapper;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ApiListingReference;
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
@Mojo(name = "touch", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class SwaggerMojo extends AbstractMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        /*
        File f = outputDirectory;

        if (!f.exists()) {
            f.mkdirs();
        }

        File touch = new File(f, "touch.txt");

        FileWriter w = null;
        try {
            w = new FileWriter(touch);

            w.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
*/

        ApiInfo apiInfo = new ApiInfo("MoreApps API", "API for the More app.", "http://www.moreaps.nl", "developers@moreapps.nl", "Commercial", "http://www.moreapps.nl");
        List<String> baseControllerPackages = new ArrayList<String>();
        baseControllerPackages.add("org.example.controllers");
        List<String> baseModelPackages = new ArrayList<String>();
        baseModelPackages.add("org.example.model");
        String basePath = "/newapidocs";
        String servletPath = "http://localhost/newapidocs";
        String apiVersion = "v1.0";
        List<String> ignorableAnnotations = new ArrayList<String>();
        boolean ignoreUnusedPathVariables = false;

        ApiParser apiParser = new CustomApiParser(apiInfo, baseControllerPackages, baseModelPackages, basePath,
                servletPath, apiVersion, ignorableAnnotations, ignoreUnusedPathVariables);

        Map<String,ApiListing> apiListings = apiParser.createApiListings();

        ResourceListing resourceList = apiParser.getResourceListing(apiListings);

        String target = "target/";
        ScalaObjectMapper mapper = new ScalaObjectMapper();
        try {
            for (String apiLocation : apiListings.keySet()) {
                if(!new File(target, apiLocation).getParentFile().mkdirs()) {
                    System.out.println("Could not create directory " + apiLocation);
                }
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File(target, apiLocation + ".json"), apiListings.get(apiLocation));
            }
//            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("service1.json"), apiListings);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(target, "service.json"), resourceList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    private void writeInDirectory(File dir, Documentation doc) throws GenerateException {
        String filename = resourcePathToFilename(doc.getResourcePath());
        try {
            File serviceFile = createFile(dir, filename);
            mapper.writerWithDefaultPrettyPrinter().writeValue(serviceFile, doc);
        } catch (IOException e) {
            throw new GenerateException(e);
        }
    }

    protected File createFile(File dir, String outputResourcePath) throws IOException {
        File serviceFile;
        int i = outputResourcePath.lastIndexOf("/");
        if (i != -1) {
            String fileName = outputResourcePath.substring(i + 1);
            String subDir = outputResourcePath.substring(0, i);
            File finalDirectory = new File(dir, subDir);
            finalDirectory.mkdirs();
            serviceFile = new File(finalDirectory, fileName);
        } else {
            serviceFile = new File(dir, outputResourcePath);
        }
        while (!serviceFile.createNewFile()) {
            serviceFile.delete();
        }
        LOG.info("Creating file " + serviceFile.getAbsolutePath());
        return serviceFile;
    }
*/
}
