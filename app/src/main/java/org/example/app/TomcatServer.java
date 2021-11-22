package org.example.app;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class TomcatServer implements WebServer {
    private Tomcat server;
    private final int port;
    private final String contextPath;

    public TomcatServer(int port, String contextPath) {
        this.port = port;
        this.contextPath = contextPath;
    }

    @Override
    public void start() throws Exception {
        server = new Tomcat();
        server.setBaseDir(createTempDir("tomcat").getAbsolutePath());
        server.setPort(port);
        server.getHost().setAutoDeploy(false);
        if (server.getHost() instanceof StandardHost) {
            ((StandardHost)server.getHost()).setUnpackWARs(false);
        }

        server.getHost().setAppBase(".");
        URL docBase = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
        server.addWebapp(contextPath, docBase);

        server.start();

        startDaemonAwaitThread();
    }

    private void startDaemonAwaitThread() {
        Thread awaitThread = new Thread(() -> server.getServer().await());
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override
    public void stop() {
        try {
            server.stop();
            server.destroy();
        } catch (LifecycleException e) {
            // ignore
        }
    }

    protected File createTempDir(String prefix) {
        try {
            File tempDir = Files.createTempDirectory(prefix + "." + port + ".").toFile();
            tempDir.deleteOnExit();
            return tempDir;
        } catch (IOException ex) {
            throw new RuntimeException(
                "Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"), ex);
        }
    }
}
