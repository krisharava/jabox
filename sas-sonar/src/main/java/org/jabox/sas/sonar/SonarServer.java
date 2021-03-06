/*
 * Jabox Open Source Version
 * Copyright (C) 2009-2010 Dimitris Kapanidis                                                                                                                          
 * 
 * This file is part of Jabox
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package org.jabox.sas.sonar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.codehaus.plexus.util.FileUtils;
import org.jabox.apis.embedded.AbstractEmbeddedServer;
import org.jabox.environment.Environment;
import org.jabox.maven.helper.MavenDownloader;
import org.jabox.utils.DownloadHelper;
import org.jabox.utils.Unzip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sonar Server.
 */
public class SonarServer extends AbstractEmbeddedServer {
    private static final long serialVersionUID = 7700819637077613916L;

    private static final Logger LOGGER = LoggerFactory
        .getLogger(SonarServer.class);

    private final String version = "3.3.2";

    public static void main(final String[] args) throws Exception {
        new SonarServer().injectPlugins();
    }

    @Override
    public String getServerName() {
        return "sonar";
    }

    @Override
    public String getWarPath() {
        File downloadsDir = Environment.getDownloadsDir();

        // Download the sonar.zip
        File zipFile = new File(downloadsDir, "sonar.zip");
        String url =
            "http://dist.sonar.codehaus.org/sonar-" + version + ".zip";
        zipFile = DownloadHelper.downloadFile(url, zipFile);
        File sonarBaseDir = getSonarBaseDir();

        if (!sonarBaseDir.exists()) {
            try {
                Unzip.unzip(zipFile, downloadsDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File sonarWar = execBuildWar(sonarBaseDir);
        injectPlugins();
        return sonarWar.getAbsolutePath();
    }

    /**
     * @param downloadsDir
     * @return
     */
    private File getSonarBaseDir() {
        File downloadsDir = Environment.getDownloadsDir();
        return new File(downloadsDir, "sonar-" + version);
    }

    public List<String> plugins = getDefaultPlugins();

    private void injectPlugins() {
        for (String plugin : plugins) {
            injectPlugin(plugin);
        }
    }

    private void injectPlugin(final String plugin) {
        String groupId = plugin.split(":")[0];
        String artifactId = plugin.split(":")[1];
        String version = plugin.split(":")[2];
        File file =
            MavenDownloader.downloadArtifact(groupId, artifactId, version,
                "jar");
        try {
            FileUtils.copyFile(file,
                new File(getSonarPluginDir(), file.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    private List<String> getDefaultPlugins() {
        List<String> pl = new ArrayList<String>();
        pl.add("org.codehaus.sonar-plugins.scm-activity:sonar-scm-activity-plugin:1.5");
        pl.add("org.codehaus.sonar-plugins:sonar-quality-index-plugin:1.1.3");
        pl.add("org.codehaus.sonar-plugins:sonar-fb-contrib-plugin:1.1");
        pl.add("org.codehaus.sonar-plugins:sonar-technicaldebt-plugin:1.2.1");
        pl.add("org.codehaus.sonar-plugins:sonar-switch-off-violations-plugin:1.3");
        pl.add("org.codehaus.sonar-plugins:sonar-taglist-plugin:1.0");
        pl.add("org.codehaus.sonar-plugins:sonar-timeline-plugin:1.3");
        pl.add("org.codehaus.sonar-plugins:sonar-security-rules-plugin:0.3.2");
        pl.add("org.codehaus.sonar-plugins:sonar-motion-chart-plugin:1.4");
        pl.add("org.codehaus.sonar-plugins:sonar-violationdensity-plugin:1.2");
        pl.add("org.codehaus.sonar-plugins:sonar-total-quality-plugin:1.0.2");
        pl.add("org.codehaus.sonar-plugins:sonar-cutoff-plugin:0.2");
        pl.add("org.codehaus.sonar-plugins:sonar-artifact-size-plugin:0.3");
        pl.add("org.codehaus.sonar-plugins:sonar-build-breaker-plugin:1.0");
        pl.add("org.codehaus.sonar-plugins:sonar-useless-code-tracker-plugin:0.5");
        return pl;
    }

    /**
     * Executes the Build War script.
     */
    private File execBuildWar(final File sonarBaseDir) {
        File sonarWar = new File(sonarBaseDir, "war/sonar.war");
        if (sonarWar.exists()) {
            return sonarWar;
        }

        File script = getBuildSonarScript(sonarBaseDir);
        new File(sonarBaseDir, "war/apache-ant-1.7.0/bin/ant")
            .setExecutable(true);
        new File(sonarBaseDir, "war/apache-ant-1.7.0/bin/ant.bat")
            .setExecutable(true);
        try {
            Process p =
                Runtime.getRuntime().exec(script.getAbsolutePath(), null,
                    new File(sonarBaseDir, "war"));
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            p.waitFor();
            System.out.println(p.exitValue());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sonarWar;
    }

    private File getBuildSonarScript(final File sonarBaseDir) {
        if (Environment.isWindowsPlatform()) {
            return new File(sonarBaseDir, "war/build-war.bat");
        }
        File file = new File(sonarBaseDir, "war/build-war.sh");
        file.setExecutable(true);
        return file;
    }

    public static void doUnzip(final String inputZip,
            final String destinationDirectory) throws IOException {
        int BUFFER = 2048;
        List<String> zipFiles = new ArrayList<String>();
        File sourceZipFile = new File(inputZip);
        File unzipDestinationDirectory = new File(destinationDirectory);
        unzipDestinationDirectory.mkdir();

        ZipFile zipFile;
        // Open Zip file for reading
        zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);

        // Create an enumeration of the entries in the zip file
        Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = zipFileEntries.nextElement();

            String currentEntry = entry.getName();

            File destFile =
                new File(unzipDestinationDirectory, currentEntry);
            destFile =
                new File(unzipDestinationDirectory, destFile.getName());

            if (currentEntry.endsWith(".zip")) {
                zipFiles.add(destFile.getAbsolutePath());
            }

            // grab file's parent directory structure
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            try {
                // extract file if not a directory
                if (!entry.isDirectory()) {
                    BufferedInputStream is =
                        new BufferedInputStream(
                            zipFile.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest =
                        new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            destFile.setExecutable(true);

            for (Iterator<String> iter = zipFiles.iterator(); iter
                .hasNext();) {
                String zipName = iter.next();
                doUnzip(zipName, destinationDirectory + File.separatorChar
                    + zipName.substring(0, zipName.lastIndexOf(".zip")));
            }

        }
    }

    private File getSonarPluginDir() {
        return new File(getSonarBaseDir(), "extensions/downloads/");
    }

}
