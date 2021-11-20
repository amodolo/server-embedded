package org.example.core.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("context started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("context shutdown");
    }
}
