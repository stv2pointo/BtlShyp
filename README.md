# BtlShyp

![Battleship](./docs/battleship.jpg)
<sub>(_Image courtesy of [pixabay](https://pixabay.com/en/nimitz-aircraft-carrier-2486752/)_)</sub>

A Battle Ship Game

-------------------

## Requirements

* Java 8
* [Gson](https://github.com/google/gson) - JSON Serializer and Deserializer
  * Download the JAR file from https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.2
  * (The JAR file download is a little hidden, but it is towards the top of the center section next to a "pom" and
  "View All" link)
* [SLF4J](https://www.slf4j.org/) - Simple Logging Facade for Java
  * Download the latest stable version's zip file from https://www.slf4j.org/download.html
  * Extract the contents of the zip file (which will include dozens of JAR files)
  * Include `slf4j-api-X.X.XX.jar` and `slf4j-jdk14-X.X.XX.jar` into your project
* [JUnit 4](http://junit.org/junit4/) - Unit Testing Framework
  * The latest version of JUnit is JUnit 5. However, to simplify the project setup we will be using JUnit 4 because it
  comes packaged into just two jars instead of several.
    * (The recommended way of including and running JUnit is using Maven or Gradle but we aren't using either for this
    project.)
  * Download and include the "latest" `junit.jar` from https://github.com/junit-team/junit4/wiki/Download-and-Install
* [Hamcrest](http://hamcrest.org/JavaHamcrest/) - "Matchers" for unit testing and assertions
  * Download and include the latest `hamcrest-all` jar from
  https://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.hamcrest%20hamcrest-all
* [Mockito](http://site.mockito.org/) - Mock Objects for Testing
  * Mockito allows you to "mock" in a "fake" object and define what it does when a method call is made to it. This makes
    it possible to test things which would ordinarily have alot of tedious or complex setup.
  * Download and include the latest `mockito-core` jar from https://search.maven.org/#search%7Cga%7C1%7Cmockito-core
  * Mockito has a few other dependencies you'll need to include as well:
    * Download and include the latest `byte-buddy` and `byte-buddy-agent` from 
    https://search.maven.org/#search%7Cga%7C1%7Cbyte-buddy
    * Download and include the latest `objenesis` from https://search.maven.org/#search%7Cga%7C1%7Cobjenesis
* [Lombok](https://projectlombok.org/) - Boilerplate Code Reduction Library
  * Download the JAR file from https://projectlombok.org/download
  * See additional setup instructions for various IDEs below to complete Lombok setup

-------------------

## Project Setup

1. Clone the project
2. Import into your chosen IDE
3. Import all necessary libraries into IDE

### Lombok Setup

This project uses Lombok. Lombok will need to be included in your project / on your classpath in order for the project
to compile and run.

What's Lombok? Lombok uses annotations to behind the scenes implement common POJO boilerplate code, such as Getters &
Setters, Builders, Constructors, etc. It helps keep code clean and concise.
Read more about it at https://projectlombok.org/.

**Eclipse:**
* Download the Lombok JAR
* Import the JAR as a Library to your project
* Follow the instructions listed here: https://projectlombok.org/setup/eclipse
* _(Note that in a few instances installing the Lombok plugin to Eclipse it "broke" the shortcuts to launch Eclipse so
you may need to recreate those)_

**IntelliJ:**
* Download the Lombok JAR
* Import the JAR as a Library to your project
* Install the Lombok IntelliJ plugin
  * File -> Settings -> Plugins
  * Search for `Lombok`
  * You should see `No plugins found. Search in Repository`. Click on the `Search in Repository` link.
  * Install the `Lombok Plugin`
  * Restart IntelliJ when prompted
* Enable Annotation Processing inside IntelliJ:
  * File -> Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors -> Enable annotation
  processing

-------------------

## Testing

For additional information about JUnit testing, please see http://www.vogella.com/tutorials/JUnit/article.html

### How To Run Tests

**Eclipse:**
* http://www.vogella.com/tutorials/JUnit/article.html#eclipse-support-for-junit-4

**IntelliJ:**
* Open the `TestRunner` class
* Click the double "play" arrows in the left side near the line numbers to launch the test suite

-------------------

## Packaging Project Jar

**Eclipse:**

Configuration Setup:
To export a runnable Jar, you need to set up a default Run configuration first.
* Run --> Run Configurations
* Select Java Application from the options menu on the left side
* Right-click --> New
* Name: (enter your configuration name here)
* Project: BtlShyp
  * can use browse to navigate to it
* Main class: main.btlshyp.Main 
  * can use browse to navigate to it
* Apply --> Close

To Export Jar:
Once your configuration is set up, you can export the Jar file
* File --> Export
* Java --> Runnable JAR file --> Next
  * Be sure to select `Runnable JAR file` *not* JAR File
* Launch configuration --> select the configuration you just made
* Export Destination: --> give it a destination and name on the file system
* Library handling: --> select `Extract required libraries into generated JAR`
  * Be sure to select the correct option or your Jar will be missing Lombok, SLF4J, etc.
* Finish
  * Select OK on the Runnable JAR File Export dialogue box

Testing:
You can test your new runnable jar by navigating to it with the command prompt and running `java -jar {your_jar_name}.jar`

**IntelliJ:**

Setup:
* File --> Project Structure --> Project Settings --> Artifacts
* Click green plus sign
* Jar -> From modules with dependencies...
* Click the 3-dot icon next to the Main class field to browse to the Main class
* Select `main.btlshyp.Main` and click OK
* Leave the option `extract to the Target JAR` option checked
  * _This ends up creating and "uber-jar" where are dependencies are included within the jar. This creates a larger jar,
  but none of our dependencies are currently large enough for us to cause additional work while exporting
* Click OK. Click Apply.

To Build Jar:
* Build --> Build Artifact
* BtlShyp.jar --> Build
* JAR will be created in the `/out/artifacts/BtlyShyp_jar/` directory

-------------------

## Configuring IP Address & Port

A default server IP address and Port are configured within `Main.java`. However, these values can be overwritten by
passing in commandline arguments via the commandline. You can (optionally) pass a server ip address followed by an 
(optional) port.
 
After exporting a jar of the project you can launch the application using syntax similar to

```
java -jar BtlShyp.jar [server_ip] [server_port]
```

Example:
```
java -jar BtlShyp.jar ec2-34-224-216-23.compute-1.amazonaws.com 8989
```

-------------------

## Running Server Locally

For local development or demonstration, it is possible to run the server application locally. The server is a Node.js
application which is started outside the client. Then a second "Module" is run locally and "connected" to the server.
At this point you can use `localhost` to connect to the server.

**Extract Server Code**
The server code comes packaged inside a zip file located inside the `/server/` directory. Unzip the files within the zip
file to a location where you will run the server from.

**Install Node.js**
The server runs on [Node.js](https://nodejs.org/en/). Full install instructions aren't provided here, but you will need
to have Node.js installed and running on your machine. Instructions for installing Node.js can be found
[HERE](https://nodejs.org/en/download/). An alternate installation path would be to install
[Node Version Manager(NVM)](https://github.com/creationix/nvm) which makes setting up Node.js on some platforms (Linux)
very easy.

**Clone & Setup BtlShyp Server Module**
The BtlShyp server "Module" resides on a branch in this repository. The recommended way to get this Module is to clone
this repository a second time then checkout the `server` branch.

Once checked out, you should import/open/create a new project in your IDE using this code within the server branch.

In the extracted server code from an earlier step, navigate to `server/java_interface` and locate the
`ipc_interface.jar` file. Copy this jar file into your project and include it as a library.

A second jar, `json-20160212.jar`, is included in this project's `/server/` directory alongside the `server.zip` file.
This json jar also needs to be imported into your project as a library.

The final jars which are required for the Module to run are Lombok and JUnit. Import the same jars which were included
in the original project into the server project. At this point the BtlShyp Module should be ready.

**Launch Server & Module**

Open a commandline interface and browse to the extracted server directory. In the root directory of the extracted files,
launch the server by running the command:

```
node server.js
```

You should see output similar to:
```
INFO - Server: Setting up log manager
INFO - Server: Creating network server
INFO - Server: Getting IPC pipe
INFO - IPC: Listening on pipe address 8990
INFO - Server: Listening on port 8989
```

Leave this commandline window open for the duration of the time you want the server to be running.

Within your IDE, open the `GameHandler.java` file. Run the `main()` method inside the GameHandler. In the terminal
window where your server code is running on Node.js, you should see lines similar to the following:

```
INFO - LogManager: Setting up a new logger for BtlShyp
INFO - LogManager: Updating BtlShyp level to DEBUG
INFO - BtlShyp: Created handler
```

The server is now running and the BtlShyp Module is running as part of that server. You can now run your application
by configuring it to run against localhost. See the Configuring IP Address & Port section for instructions on running
the server on a different IP address.

After running your GUI client, you should see messages in the server's log showing up in your terminal window. For
example, after logging in you should see messages similar to the following show:

```
INFO - Message_Handler: Logging in Christopher
DEBUG - BtlShyp: Attempting to join Christopher to a lobby
DEBUG - BtlShyp: Sent response to Christopher
```

**Additional Information**
In the extracted server code from `server.zip` there is a `README.md` file which contains information related to
configuring the server application. See that README for additional questions you might have.

-------------------

## Design Documentation

All design documentation is included in this project [HERE](./docs/design/README.md).

