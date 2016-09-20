Author   : Jaydeep Ingle 
Email ID : jingle1@binghamton.edu
Project0 : HTTP Server
CS557    : Introduction to Distributed Systems
Dir 	 : jingle1-project0

--------------------------------------------------------------------------------------------
Description: 
The Daemon.java contains a main class Daemon and 3 classes inside Daemon viz. Request, Response and HttpConnection.

First it will check for www dir within the present working dir. If its there it will move ahead else will terminate the program.
2 methods viz. initializeAccessCount and getAllMimeTypesAvailable are written to initialize the access count to the files
and get all the mime types available in the dir.

1. HttpConnection class handles the multiple clients using multithreading.

2. Request class processes the request from client. It determines the method type and will move ahead.

3. Response class handles the Response part which includes the header fields and the way to send file to a client.

--------------------------------------------------------------------------------------------

Steps to Run: 
1.
java -jar server.jar
Resource Directory exists
Address: localhost/127.0.0.1
Port: 10080

--------------------------------------------------------------------------------------------

Alternate Steps to Run:
1. 
$ javac Daemon.java

2. 
$ java Daemon
Resource Directory exists
Address: localhost/127.0.0.1
Port: 10080

In case there is no www dir it will show output as follows

$ java Daemon
Resource Directory does not exist
Exiting...

--------------------------------------------------------------------------------------------

Output : 
Server Side output: 

Resource Directory exists
Address: localhost/127.0.0.1
Port: 10080
/sample.txt|/127.0.0.1|1
/sample.txt|/127.0.0.1|2
/sample.txt|/127.0.0.1|3
/sample.pdf|/127.0.0.1|1
/sample.pdf|/127.0.0.1|2
/sample.pdf|/127.0.0.1|3
/sample_files/al|/127.0.0.1|1


1. Run using wget

$ wget http://127.0.0.1:35942/sample.pdf --limit-rate=128k
--2016-09-07 09:58:01--  http://127.0.0.1:35942/sample.pdf
Connecting to 127.0.0.1:35942... connected.
HTTP request sent, awaiting response... 200 OK
Length: 12711311 (12M) [application/pdf]
Saving to: ‘sample.pdf’

sample.pdf                           100%[====================================================================>]  12.12M   128KB/s    in 97s     


2. Run using curl

$ curl -i http://127.0.0.1:35942/sample.txt
HTTP/1.1 200 OK
Server: OpenJDK 64-Bit Server VM
Date: Wed, 07 Sep 2016 10:00:37 EDT
Last-Modified: Sun, 04 Sep 2016 22:45:12 EDT
Content-Type: text/plain
Content-Length: 12
Connection: Close

Hello World

3. Run using browser / Mozilla Firefox

Congratulations, the server is up and running. !!

However, if you are seeing this message, it means you have not configured the resource.dir property. Please set resource.dir property value to the directory where your resources reside.

--------------------------------------------------------------------------------------------

Running using make

$ make compile

javac Daemon.java
echo Main-Class: Daemon > MANIFEST.MF
jar -cvmf MANIFEST.MF server.jar *.class
added manifest
adding: Daemon$HttpConnection.class(in = 1456) (out= 836)(deflated 42%)
adding: Daemon$Request.class(in = 2419) (out= 1215)(deflated 49%)
adding: Daemon$Response.class(in = 5508) (out= 2962)(deflated 46%)
adding: Daemon.class(in = 3901) (out= 2114)(deflated 45%)


$ make run

java -jar server.jar
Resource Directory exists
Address: localhost/127.0.0.1
Port: 10080

--------------------------------------------------------------------------------------------
