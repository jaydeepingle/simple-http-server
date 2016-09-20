// packages import
import java.lang.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.text.*;

// Main Class
public class Daemon {
	// Resource Directory
	private static String baseResourcePath = System.getProperty("user.dir") + "/www";

	// Mime Type HashMap
	private static Map<String, String> mimeTypeMap = new HashMap<String, String>();

	// File Count HashMap
	private static Map<String, Integer> fileCount = new HashMap<String, Integer>();

	// Method to initialize the file access count to 0
	public void initializeAccessCount(String directoryName, ArrayList<File> files) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
				fileCount.put("/" + file.getPath().split("www/")[1], 0);
			} else if (file.isDirectory()) {
				initializeAccessCount(file.getAbsolutePath(), files);
			}
		}
	}

	// Method to get the map of all the mime types available
	public void getAllMimeTypesAvailable() throws Exception {
		String filePath = "/etc/mime.types";
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		int count = 0;
		String cats[] = null;

		while ((line = reader.readLine()) != null) {
			if (!(line.trim().isEmpty()) && line.indexOf("#") == -1) {
				String[] lineFragment = line.split("\\s+");
				if (lineFragment.length == 1) {
					;
				} else if (lineFragment.length == 2) {
					mimeTypeMap.put(lineFragment[1], lineFragment[0]);
				} else if (lineFragment.length > 2) {
					for (int i = 1; i < lineFragment.length; i++) {
						mimeTypeMap.put(lineFragment[i], lineFragment[0]);
					}
				}
			}
		}
	}

	// Constructor for calling the initial methods to get all the mime types available and to initialize the File Access count
	public Daemon() {
		// Files with count in the baseResourcePath
		ArrayList<File> files = new ArrayList<File>();
		initializeAccessCount("www", files);

		// Mime Type
		try {
			getAllMimeTypesAvailable();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	// Class to deal with the requests received from multiple clients
	static class Request {

		private Map<String, List<String>> requestHeaders;
		private URI resource;
		private String method;
		private String httpVersion;

		private Request() {
			requestHeaders = new HashMap<>();
		}

		private void addHeader(String name, String value) {
			name = name.toLowerCase();
			List<String> values = requestHeaders.get(name);
			if (values == null) {
				requestHeaders.put(name, (values = new ArrayList<>(2)));
			}
			values.add(value);
		}

		static Request parse(InputStream stream) throws IOException {
			Request request = new Request();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String requestLine = br.readLine();
			String[] parts = requestLine.split(" ");
			request.method = parts[0];
			request.resource = URI.create(parts[1]);
			request.httpVersion = parts[2];
			while (br.ready()) {
				String headerLine = br.readLine().trim();
				if (headerLine.isEmpty()) { // reached end of headers
					break;
				}
				String[] nv = headerLine.split(":");
				request.addHeader(nv[0].trim(), nv[1].trim());
			}
			return request;
		}

		public URI getResource() {
			return resource;
		}

		public String getMethod() {
			return method;
		}

		public String getHttpVersion() {
			return httpVersion;
		}

		// return an immutable map of parsed headers
		public Map<String, List<String>> getRequestHeaders() {
			return Collections.unmodifiableMap(requestHeaders);
		}
	}

	static class Response {
		// Response Strings for browsers
		private static final byte[] resourceNotFoundResponse = ("<html><head><title>404 Not Found</title></head><body>"
				+ "<h2>404 Not Found</h2><p> The requested resource could not be found on this server</p>"
				+ "</body></html>").getBytes();
		private static final byte[] serverWelcomeMsg = ("<html><head><title>Kudos</title></head><body>"
				+ "<h2>Congratulations, the server is up and running...!!</h2>" + "</body></html>").getBytes();
		private static final byte[] getRequestsOnly = ("<html><head><title>Sorry</title></head><body>"
				+ "<h2>Sorry... This server processes only GET requests...!!!</h2><p>However, if you are seeing this message, it means you have not configured the resource.dir property. Please set resource.dir property value to the directory where your resources reside.</p>"
				+ "</body></html>").getBytes();
		private static final byte[] internalServerError = ("<html><head><title>500 Internal Server Error</title></head><body>"
				+ "<h2>404 Not Found</h2><p>Internal Server Error</p>"
				+ "</body></html>").getBytes();

		private static final String CRLF = "\r\n";

		InputStream in;
		private String statusLine;
		private Date currentDate;
		private String serverDetails = System.getProperty("java.vm.name");
		private Date lastModifiedDate;
		private long contentLength;
		private String contentType;

		public Response(Request request) throws IOException {
			this(request, baseResourcePath);
		}

		public Response(boolean getRequestsOnlyFlag) {
			statusLine = "HTTP/1.1 405 OK";
			contentType = "text/html";
			contentLength = getRequestsOnly.length;
			currentDate = new Date();
			in = new ByteArrayInputStream(getRequestsOnly);
		}

		public Response(Request request, String baseURI) throws IOException {
			String filePath = null;
			currentDate = new Date();
			if ("/".equals(request.getResource().getPath())) {
				filePath = baseURI + "/index.html";
			} else {
				filePath = baseURI + request.getResource().getPath();
			}
			File file = new File(filePath);
			boolean resourceMissing = false;
			if (file.exists()) {
				try {
					in = new FileInputStream(file);
					statusLine = "HTTP/1.1 200 OK";
					lastModifiedDate = new Date(file.lastModified());
					contentType = mimeTypeMap.get(file.toPath().toString().split("\\.(?=[^\\.]+$)")[1]);
					if (contentType.isEmpty()) {
						contentType = "application/octet-stream";
						// contentType = Files.probeContentType(file.toPath());
						// using Library Function
					}
					contentLength = file.length();
				} catch (FileNotFoundException e) {
					resourceMissing = true;
				}
			} else if ("/".equals(request.getResource().getPath())) {
				statusLine = "HTTP/1.1 200 OK";
				contentType = "text/html";
				contentLength = serverWelcomeMsg.length;
				in = new ByteArrayInputStream(serverWelcomeMsg);
			} else {
				resourceMissing = true;
			}
			if (resourceMissing) {
				statusLine = "HTTP/1.1 404 NotFound";
				contentType = "text/html";
				contentLength = resourceNotFoundResponse.length;
				in = new ByteArrayInputStream(resourceNotFoundResponse);
			} 
		}

		// method to print the details of the requested resources
		private void writeRequestedResource(String fileName, InetAddress clientIPAddress, Integer count) {
			System.out.println(fileName + "|" + clientIPAddress + "|" + count);
		}

		// writing headers
		private void writeHeaders(OutputStream out) throws IOException {
			StringBuilder builder = new StringBuilder();
			builder.append(statusLine).append(CRLF);
			builder.append("Server: ").append(serverDetails).append(CRLF);
			builder.append("Date: ").append(formatHttpDate(currentDate)).append(CRLF);
			if (lastModifiedDate != null) {
				builder.append("Last-Modified: ").append(formatHttpDate(lastModifiedDate)).append(CRLF);
			}
			builder.append("Content-Type: ").append(contentType).append(CRLF);
			builder.append("Content-Length: ").append(String.valueOf(contentLength)).append(CRLF);
			builder.append("Connection: Close").append(CRLF);
			builder.append(CRLF);
			out.write(builder.toString().getBytes());
			// end headers;
		}

		private void writeBody(OutputStream out) throws IOException {
			byte[] buff = new byte[(int) Math.min(contentLength, 4096)];
			int read;
			while ((read = in.read(buff, 0, buff.length)) > 0) {
				out.write(buff, 0, read);
			}
			in.close();
			out.flush();
		}

		private void incrementAccessCount(String filePath, Integer count) {
			fileCount.put(filePath, (count + 1));
		}

		public void write(OutputStream out, InetAddress clientIPAddress, String filePath) throws IOException {
			File newFile = new File(filePath);
			if(newFile.exists()) {
				incrementAccessCount(filePath, fileCount.get(filePath)); // increment access count
				writeRequestedResource(filePath, clientIPAddress, fileCount.get(filePath));
			} else {
				//System.out.println(fileCount.get(filePath));
				if(fileCount.get(filePath) != null) {
					fileCount.put(filePath,(fileCount.get(filePath) + 1));
				} else {
					fileCount.put(filePath, 1);
				}
				writeRequestedResource(filePath, clientIPAddress, fileCount.get(filePath));
			}
			writeHeaders(out); // write the response headers
			writeBody(out); // write the content
		}

		static String formatHttpDate(Date date) {
			DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
			return df.format(date);
		}
	}

	static class HttpConnection implements Runnable {
		Socket client;

		public HttpConnection(Socket client) {
			this.client = client;
		}

		@Override
		// This method handles multiple clients simultaneously
		// synchronized will take care of the synchronization among the clients
		public void run() {
			synchronized (client) {
				try {
					Request request = Request.parse(client.getInputStream());
					Response response;
					if (request.method.equals("GET")) {
						response = new Response(request);
					} else {
						response = new Response(true);
					}
					response.write(client.getOutputStream(), client.getInetAddress(), request.getResource().getPath());
					client.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
	}

	public static void main(String args[]) throws Exception {
		// Checking whether Resource Directory is available or not else exiting
		if (!new File(System.getProperty("user.dir") + "/www").exists()) {
			System.out.println("Resource Directory does not exist" + "\n" + "Exiting...");
			return;
		} else {
			System.out.println("Resource Directory exists");
		}

		// Instantiating Daemon class to initialize the files with the access counts
		// Also it will preserve a hashMap with the file extension and mimetype
		Daemon d = new Daemon();

		// Starting the server
		InetAddress serverIPAddress = InetAddress.getLocalHost(); //
		String addressString = serverIPAddress.toString();
		addressString = addressString.split("/")[0].trim();
		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(10080, 50, serverIPAddress);
		System.out.println("Address: " + addressString + "\n" + "Port: " + server.getLocalPort());
		while (true) {
			Socket clientSocket = server.accept();
			new Thread(new HttpConnection(clientSocket)).start();
		}
	}
}