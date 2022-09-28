package assignment1;
//import java.io.*;
import java.net.*;

public class Client
{
    //Let the URL = "http://www.cs.ndsu.nodak.edu/aboutus.html"
    private static assignment1.RequestHandler handlerTest;
    public static void main(String[] args)
    {
        buildRequestHandler("http://www.cs.ndsu.nodak.edu/aboutus.html");
        handlerTest.run();
    }

    private static void buildRequestHandler(String urlString)
    {
        try
        {
            URL urlTest = new URL(urlString);
            Socket socketTest = new Socket(urlTest.getHost(), 80);
            ProxyServer serverTest = new ProxyServer();
            handlerTest = new RequestHandler(socketTest, serverTest);
        }
        catch (Exception ex)
        {
            if (ex instanceof MalformedURLException)
            {
                System.out.println("Invalid URL");
            }
            else if (ex instanceof UnknownHostException)
            {
                System.out.println("Unknown Host");
            }
            else
            {
                System.out.println(ex.getMessage());
            }
        }
    }
}