package com.moreapps;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class SwaggerMojoTest {

    @Test
    public void testGenerate() throws MojoExecutionException {
        new SwaggerMojo().execute();
    }

}
