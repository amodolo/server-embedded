# server-embedded
a simple playground to check the feasibility to embed a jetty and tomcat server inside am executable war

## Quick start

Compile the project with maven

    $ mvn clean package

Then launch the application using

    $ java -jar ./app/target/app.war

The application is deployed inside a jetty server at the url

    http://localhost:8080

The application exposed the following paths:
<table>
<tr>
<td><code>/</code></td>
<td>show the <code>index.html</code> content</td>
</tr>
<tr>
<td><code>/hello</code></td>
<td>show the content exposed by the <code>HelloServlet</code> class</td>
</tr>
</table>

## Project structure

This project contains two maven's modules:
1. **app**: will produce the web app and contains the web.xml file and the Launcher that runs the embedded server.
2. **core**: contains only a servlet and a web-fragment.xml.

The original aim it was to define the servlet inside the fragment and let the jetty publish it using the J2EE spec against the web.xml and web-fragments.
At the moment the fragment scanner has been disabled (with the metadata-complete="true") to minimize the startup time. 

