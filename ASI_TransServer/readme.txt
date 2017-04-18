To compile the transaction server application, run:
mvn clean package


To install the 3rd party jars into the Maven 3 repository, run the following commands (only need to do this once):

mvn install:install-file -Dfile=jars/MiscUtil.jar -DgroupId=asi -DartifactId=MiscUtil -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=jars/oroMatcher-1.0.7.jar -DgroupId=asi -DartifactId=oroMatcher -Dversion=1.0.7 -Dpackaging=jar
mvn install:install-file -Dfile=jars/xerces.jar -DgroupId=asi -DartifactId=xerces -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=jars/castor-0.9.3.9-xml.jar -DgroupId=castor -DartifactId=castor -Dversion=0.9.3.9 -Dpackaging=jar
