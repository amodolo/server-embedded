package org.example.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;

public class Launcher {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setDescriptor("WEB-INF/web.xml");
        URL webAppDir = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
        webapp.setWar(webAppDir.toURI().toString());
        webapp.setParentLoaderPriority(true);
        server.setHandler(webapp);

        server.start();
        server.join();
    }
}
