package org.example.app;

public class Launcher {

    public static void main(String[] args) throws Exception {
        WebServer server = new TomcatServer(8080, "/context");
//        server = new JettyServer(8080);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("shutdown...");
            server.stop();
        }));
    }
}
