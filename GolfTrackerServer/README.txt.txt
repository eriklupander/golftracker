This project/module should contain the Java EE 6 server application for GolfTracker. It's currently running on top of H2 and JBoss AS 7

Build using maven:

mvn clean package

To deploy to JBoss7: (Might require JBOSS_HOME env var set etc.)

mvn jboss-as:deploy