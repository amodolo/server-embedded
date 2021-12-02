package org.example.app;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Constants;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.buf.UriUtil;
import org.example.core.servlet.ContextListener;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        long start = System.currentTimeMillis();

        server = new Tomcat();
        server.setBaseDir(createTempDir("tomcat").getAbsolutePath());
        server.setPort(port);
        server.getHost().setAutoDeploy(false);

        File docBase = new File(server.getHost().getAppBaseFile(), contextPath);

        Context ctx = server.addContext(server.getHost(), contextPath, docBase.getAbsolutePath());

        ctx.setParentClassLoader(getClass().getClassLoader());

        server.getHost().getAppBaseFile().mkdir();
        URL thisJar = getClass().getClassLoader().getResource("/");
        assert thisJar != null;
        ExpandWar.expand(server.getHost(), thisJar, contextPath);


        ContextConfig contextConfig = new ContextConfig();
        contextConfig.setDefaultWebXml(server.noDefaultWebXmlPath());
        server.getHost().setConfigClass(contextConfig.getClass().getName());
        ctx.addLifecycleListener(contextConfig);
        ctx.setConfigFile(getWebappConfigFile(docBase.getAbsolutePath(), contextPath));


        ctx.addApplicationListener(ContextListener.class.getName());
        ctx.addLifecycleListener(server.getDefaultWebXmlListener());
        ctx.addLifecycleListener(event -> {
            if (Lifecycle.AFTER_START_EVENT.equals(event.getType())) {
                long elapsed = System.currentTimeMillis() - start;
                System.out.printf("Server started in %d ms%n", elapsed);
            }
        });


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

    protected URL getWebappConfigFile(String path, String contextName) {
        File docBase = new File(path);
        if (docBase.isDirectory()) {
            return getWebappConfigFileFromDirectory(docBase, contextName);
        } else {
            return getWebappConfigFileFromWar(docBase, contextName);
        }
    }

    private URL getWebappConfigFileFromDirectory(File docBase, String contextName) {
        URL result = null;
        File webAppContextXml = new File(docBase, Constants.ApplicationContextXml);
        if (webAppContextXml.exists()) {
            try {
                result = webAppContextXml.toURI().toURL();
            } catch (MalformedURLException e) {
//                Logger.getLogger(getLoggerName(getHost(), contextName)).log(Level.WARNING,
//                    "Unable to determine web application context.xml " + docBase, e);
            }
        }
        return result;
    }

    private URL getWebappConfigFileFromWar(File docBase, String contextName) {
        URL result = null;
        try (JarFile jar = new JarFile(docBase)) {
            JarEntry entry = jar.getJarEntry(Constants.ApplicationContextXml);
            if (entry != null) {
                result = UriUtil.buildJarUrl(docBase, Constants.ApplicationContextXml);
            }
        } catch (IOException e) {
//            Logger.getLogger(getLoggerName(getHost(), contextName)).log(Level.WARNING,
//                "Unable to determine web application context.xml " + docBase, e);
        }
        return result;
    }
}
