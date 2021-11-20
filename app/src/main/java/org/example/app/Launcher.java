package org.example.app;

public class Launcher implements Runnable {
    private final WebServer server;

    public static void main(String[] args) throws Exception {
        new Launcher().start();
    }

    public Launcher() {
        Runtime.getRuntime().addShutdownHook(new Thread(this));
//        server = new TomcatServer(8080);
        server = new JettyServer(8080);
    }

    public void start() throws Exception {
        server.start();
    }

    @Override
    public void run() {
        System.out.println("shutdown...");
        server.stop();
    }
}
