#!/bin/sh

rm -rf classestest
rm -rf classes

# javac -classpath ./classes:./classestest:/usr/share/java/hamcrest-core.jar:/usr/share/java/junit4.jar -d ./classestest -sourcepath ./srctests/ ./srctests/fr/insaRouen/iti/prog/asiaventure/AllTests.java
# 
# cd classestest
# java -ea -classpath ./classes:./classestest:/usr/share/java/hamcrest-core.jar:/usr/share/java/junit4.jar org.junit.runner.JUnitCore fr.insaRouen.iti.prog.asiaventure.AllTests
# cd ..

javac -classpath ./classes -sourcepath ./src/ -d ./classes/ $(find ./src -name "*.java")
javac -classpath ./classes:./classestest:/usr/share/java/* -sourcepath ./srctests -d ./classestest ./srctests/fr/insaRouen/iti/prog/asiaventure/AllTests.java
java -classpath ./classes:./classestest:/usr/share/java/* org.junit.runner.JUnitCore fr.insaRouen.iti.prog.asiaventure.AllTests
