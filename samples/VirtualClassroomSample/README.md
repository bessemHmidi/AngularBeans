# AngularBeans Demo App
A little app simulating a VirtualClassRoom scenario to demonstrate AngularBeans.

# Requirements:
1. Make sure you have [maven](https://maven.apache.org/) installed and configured in your PATH.
2. A JavaEE7 Web Container (see [Compatible servers](https://github.com/bessemHmidi/AngularBeans/issues/12)).

# How to run the demo:
1. Under the *angular-beans* directory run `mvn clean install`.
3. Navigate to the demoApp directory and run `mvn clean compile package`.
4. Deploy the war file to your running application server.  
If you don't have a running application server, you can run the application on the embedded Wildfly: `mvn wildfly:run` 
5. In your browser, navigate localhost:8080/AngularBeansDemo
