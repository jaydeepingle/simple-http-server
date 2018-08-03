Description: <br/>
The Daemon.java contains a main class Daemon and 3 classes inside Daemon viz. Request, Response and HttpConnection.<br/>

First it will check for www dir within the present working dir. If its there it will move ahead else will terminate the program.<br/>
2 methods viz. initializeAccessCount and getAllMimeTypesAvailable are written to initialize the access count to the files
and get all the mime types available in the dir.<br/>

1. HttpConnection class handles the multiple clients using multithreading.<br/>
2. Request class processes the request from client. It determines the method type and will move ahead.<br/>
3. Response class handles the Response part which includes the header fields and the way to send file to a client.<br/>
--------------------------------------------------------------------------------------------<br/>
<br/>
Steps to Run:<br/>
```java -jar server.jar```<br/>
Resource Directory exists<br/>
Address: localhost/127.0.0.1<br/>
Port: 10080<br/>
--------------------------------------------------------------------------------------------
<br/>
Alternate Steps to Run:<br/>
```javac Daemon.java```<br/>
```java Daemon```<br/>
Resource Directory exists<br/>
Address: localhost/127.0.0.1<br/>
Port: 10080<br/>
In case there is no www dir it will show output as follows<br/>
```java Daemon```<br/>
Resource Directory does not exist<br/>
Exiting...<br/>
--------------------------------------------------------------------------------------------
<br/>
Output : <br/>
Server Side output: <br/>
Resource Directory exists<br/>
Address: localhost/127.0.0.1<br/>
Port: 10080<br/>
/sample.txt|/127.0.0.1|1<br/>
/sample.txt|/127.0.0.1|2<br/>
/sample.txt|/127.0.0.1|3<br/>
/sample.pdf|/127.0.0.1|1<br/>
/sample.pdf|/127.0.0.1|2<br/>
/sample.pdf|/127.0.0.1|3<br/>
/sample_files/al|/127.0.0.1|1<br/>
<br/>
1. Run using wget<br/>
<br/>
wget http://127.0.0.1:35942/sample.pdf --limit-rate=128k<br/>
--2016-09-07 09:58:01--  http://127.0.0.1:35942/sample.pdf<br/>
Connecting to 127.0.0.1:35942... connected.<br/>
HTTP request sent, awaiting response... 200 OK<br/>
Length: 12711311 (12M) [application/pdf]<br/>
Saving to: ‘sample.pdf’<br/>
<br/>
sample.pdf                           <br/>
100%[====================================================================>]  12.12M   128KB/s    in 97s <br/>

<br/>
2. Run using curl
<br/>
```curl -i http://127.0.0.1:35942/sample.txt```<br/>
HTTP/1.1 200 OK<br/>
Server: OpenJDK 64-Bit Server VM<br/>
Date: Wed, 07 Sep 2016 10:00:37 EDT<br/>
Last-Modified: Sun, 04 Sep 2016 22:45:12 EDT<br/>
Content-Type: text/plain<br/>
Content-Length: 12<br/>
Connection: Close<br/>
<br/>
Hello World
<br/>
3. Run using browser / Mozilla Firefox<br/>
<br/>
Congratulations, the server is up and running. !!<br/>
<br/>
However, if you are seeing this message, it means you have not configured the resource.dir property. Please set <br/>resource.dir property value to the directory where your resources reside.<br/>

--------------------------------------------------------------------------------------------

Running using make<br/>
<br/>
```make compile```<br/>
<br/>
javac Daemon.java<br/>
echo Main-Class: Daemon > MANIFEST.MF<br/>
jar -cvmf MANIFEST.MF server.jar *.class<br/>
added manifest<br/>
adding: Daemon$HttpConnection.class(in = 1456) (out= 836)(deflated 42%)<br/>
adding: Daemon$Request.class(in = 2419) (out= 1215)(deflated 49%)<br/>
adding: Daemon$Response.class(in = 5508) (out= 2962)(deflated 46%)<br/>
adding: Daemon.class(in = 3901) (out= 2114)(deflated 45%)<br/>
<br/>
<br/>
```make run```<br/>
<br/>
java -jar server.jar<br/>
Resource Directory exists<br/>
Address: localhost/127.0.0.1<br/>
Port: 10080<br/>
<br/>
--------------------------------------------------------------------------------------------<br/>
