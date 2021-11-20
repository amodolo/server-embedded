package org.example.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;

public class JettyServer implements WebServer {
    private Server server;
    private final int port;

    public JettyServer(int port) {
        this.port = port;
    }

    @Override
    public void start() throws Exception{
        server = new Server(port);
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setDescriptor("WEB-INF/web.xml");
        URL webAppDir = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
        webapp.setWar(webAppDir.toURI().toString());
        webapp.setParentLoaderPriority(true);
        server.setHandler(webapp);

        server.start();
        server.setStopAtShutdown(false);
    }

    @Override
    public void stop() {
        try {
            server.stop();
            server.destroy();
        } catch (Exception e) {
            // ignore
        }
    }
}
