package assignment1;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;


// RequestHandler is thread that process requests of one client connection
public class RequestHandler extends Thread 
{
	Socket clientSocket;
	InputStream inFromClient;
	OutputStream outToClient;
	byte[] request = new byte[1024];
	private ProxyServer server;


	public RequestHandler(Socket clientSocket, ProxyServer proxyServer) {

		
		this.clientSocket = clientSocket;
		

		this.server = proxyServer;

		try {
			clientSocket.setSoTimeout(2000);
			inFromClient = clientSocket.getInputStream();
			outToClient = clientSocket.getOutputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	@Override
	public void run() {
		try{	
			int num_byte = inFromClient.read(request);
			String requeststring = new String(request, 0, num_byte);
			String arrayString[] = requeststring.split(" ");
			String get = arrayString[0];
			String urlstring = "";
			server.writeLog(clientSocket.getInetAddress().getHostAddress() + " " + urlstring);

			if(get.equalsIgnoreCase("Get"))
			{
			System.out.println(requeststring);

				if(server.getCache(urlstring) != null)
				{
					System.out.println(urlstring);
					sendCachedInfoToClient(server.getCache(urlstring));
				}
				else
					proxyServertoClient(request);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		/**
			 * To do
			 * Process the requests from a client. In particular, 
			 * (1) Check the request type, only process GET request and ignore others
                         * (2) Write log.
			 * (3) If the url of GET request has been cached, respond with cached content
			 * (4) Otherwise, call method proxyServertoClient to process the GET request
			 *
		*/
	}
	
	private void proxyServertoClient(byte[] clientRequest) {

		FileOutputStream fileWriter = null;
		Socket toWebServerSocket;
		InputStream inFromServer;
		OutputStream outToServer;
		
		// Create Buffered output stream to write to cached copy of file
		String fileName = "cached/" + generateRandomFileName() + ".dat";
		
		// to handle binary content, byte is used
		byte[] serverReply = new byte[4096];
		
		try
		{
			URLResponse result = new URLResponse(clientRequest);
			URL url = new URL(result.getURLString());
			clientSocket.get
			toWebServerSocket = new Socket(clientSocket.getInetAddress(), 80);
			inFromServer = toWebServerSocket.getInputStream();
			outToServer = toWebServerSocket.getOutputStream();
			fileWriter = new FileOutputStream(new File(fileName));

			outToServer.write(clientRequest);
			outToServer.flush();
			while(inFromServer.read(serverReply) != -1){
				outToClient.write(serverReply, 0, inFromServer.read(serverReply));
				outToClient.flush();
				fileWriter.write(serverReply, 0, inFromServer.read(serverReply));
				fileWriter.close();
			}
			server.putCache(clientSocket.getInetAddress().getHostAddress(), fileName);
			fileWriter.close();
			inFromServer.close();
			outToServer.close();
			toWebServerSocket.close();
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		/**
		 * To do
		 * (1) Create a socket to connect to the web server (default port 80)
		 * (2) Send client's request (clientRequest) to the web server, you may want to use flush() after writing.
		 * (3) Use a while loop to read all responses from web server and send back to client
		 * (4) Write the web server's response to a cache file, put the request URL and cache file name to the cache Map
		 * (5) close file, and sockets.
		*/
		
	}
	
	
	
	// Sends the cached content stored in the cache file to the client
	private void sendCachedInfoToClient(String fileName) {

		try {

			byte[] bytes = Files.readAllBytes(Paths.get(fileName));

			outToClient.write(bytes);
			outToClient.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			if (clientSocket != null) {
				clientSocket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	
	// Generates a random file name  
	public String generateRandomFileName() {

		String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
		SecureRandom RANDOM = new SecureRandom();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; ++i) {
			sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}

}
