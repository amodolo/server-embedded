# server-embedded
a simple playground to check the feasibility to embed a jetty server inside a fatjar

## Quick start

Compile the project with maven

    $ mvn clean package

Then launch the application using

    $ java -jar ./app/target/app.jar

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
But, as far as the maven-assembly-plugin (which is the one used to generate the fat jar) produces an [unshaded fat jar](https://stackoverflow.com/a/39030649/17405757),
it is not possible to use the web-fragment (the fragment must be included inside a dependent jar, it is not possible to load
a fragment if it is part of the final web-app).

