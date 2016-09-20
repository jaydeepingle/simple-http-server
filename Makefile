compile:
	javac Daemon.java
	echo Main-Class: Daemon > MANIFEST.MF
	jar -cvmf MANIFEST.MF server.jar *.class
run:
	java -jar server.jar
