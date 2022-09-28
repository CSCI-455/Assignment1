package assignment1;


public class URLResponse {
    private String[] arrayString;

    public URLResponse(byte[] request)
    {
        String requestString = new String(request);
        arrayString = requestString.split(" ");
    }

    public String getHttpMethod()
    {
        return arrayString[0];
    }

    public String getURLString()
    {
        return arrayString[1];
    }
}
