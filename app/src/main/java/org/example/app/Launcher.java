package org.example.app;

//import org.apache.catalina.startup.Tomcat;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.*;
//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Launcher {
    public static final String WEBAPP_RESOURCES_LOCATION = "webapp";

//    public static void main(String[] args) throws Exception {
//        System.out.println("Launching app...");
//
//
//
//        //tomcat
////        Tomcat tomcat = new Tomcat();
////        tomcat.setPort(8080);
////
////
//////        URL webAppDir = Thread.currentThread().getContextClassLoader().getResource(WEBAPP_RESOURCES_LOCATION);
////        String webAppDir = Launcher.class.getProtectionDomain().getCodeSource().getLocation().toString();
////        webAppDir = webAppDir.substring(6);
//////        if (webAppDir == null) {
//////            throw new RuntimeException(String.format("No %s directory was found into the JAR file", WEBAPP_RESOURCES_LOCATION));
//////        }
////        tomcat.addWebapp("/", webAppDir.toString());
////
////        tomcat.start();
////        tomcat.getServer().await();
//
//        //jetty
//        Server server = new Server(8080);
//        WebAppContext webapp = new WebAppContext();
//        webapp.setContextPath("/");
//        webapp.setDescriptor("WEB-INF/web.xml");
//        URL webAppDir = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
//        webapp.setWar(webAppDir.toURI().toString());
//        webapp.setParentLoaderPriority(true);
//        webapp.setConfigurations(new Configuration[]
//                {
//                        new AnnotationConfiguration(),
//                        new WebInfConfiguration(),
//                        new WebXmlConfiguration(),
//                        new MetaInfConfiguration(),
//                        new FragmentConfiguration(),
//                        new EnvConfiguration(),
//                        new PlusConfiguration(),
//                        new JettyWebXmlConfiguration()
//                });
//
//        server.setHandler(webapp);
//
//        server.start();
//        server.join();
//    }





    public static void main(String[] args) throws Exception
    {
        int port = 8080;
        Server server = new Server(port);

        URI webResourceBase = findWebResourceBase(server.getClass().getClassLoader());
        System.err.println("Using BaseResource: " + webResourceBase);
        WebAppContext context = new WebAppContext();
        context.setBaseResource(Resource.newResource(webResourceBase));
        context.setConfigurations(new Configuration[]
                {
                        new AnnotationConfiguration(),
                        new WebInfConfiguration(),
                        new WebXmlConfiguration(),
                        new MetaInfConfiguration(),
                        new FragmentConfiguration(),
                        new EnvConfiguration(),
                        new PlusConfiguration(),
                        new JettyWebXmlConfiguration()
                });

        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
        server.start();
        server.dump(System.err);
        server.join();
    }

    private static URI findWebResourceBase(ClassLoader classLoader)
    {
//        String webResourceRef = "src/main/webapp/WEB-INF/web.xml";
        String webResourceRef = "WEB-INF/web.xml";

        try
        {
            // Look for resource in classpath (best choice when working with archive jar/war file)
            URL webXml = classLoader.getResource('/'+webResourceRef);
            if (webXml != null)
            {
                URI uri = webXml.toURI().resolve("..").normalize();
                System.err.printf("WebResourceBase (Using ClassLoader reference) %s%n", uri);
                return uri;
            }
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("Bad ClassPath reference for: " + webResourceRef,e);
        }

        // Look for resource in common file system paths
//        try
//        {
//            Path pwd = new File(System.getProperty("user.dir")).toPath().toRealPath();
//            FileSystem fs = pwd.getFileSystem();
//
//            // Try the generated maven path first
//            PathMatcher matcher = fs.getPathMatcher("glob:**/embedded-servlet-*");
//            try (DirectoryStream<Path> dir = Files.newDirectoryStream(pwd.resolve("target")))
//            {
//                for(Path path: dir)
//                {
//                    if(Files.isDirectory(path) && matcher.matches(path))
//                    {
//                        // Found a potential directory
//                        Path possible = path.resolve(webResourceRef);
//                        // Does it have what we need?
//                        if(Files.exists(possible))
//                        {
//                            URI uri = path.toUri();
//                            System.err.printf("WebResourceBase (Using discovered /target/ Path) %s%n", uri);
//                            return uri;
//                        }
//                    }
//                }
//            }
//
//            // Try the source path next
//            Path srcWebapp = pwd.resolve("src/main/webapp/" + webResourceRef);
//            if(Files.exists(srcWebapp))
//            {
//                URI uri = srcWebapp.getParent().toUri();
//                System.err.printf("WebResourceBase (Using /src/main/webapp/ Path) %s%n", uri);
//                return uri;
//            }
//        }
//        catch (Throwable t)
//        {
//            throw new RuntimeException("Unable to find web resource in file system: " + webResourceRef, t);
//        }

        throw new RuntimeException("Unable to find web resource ref: " + webResourceRef);
    }

}
