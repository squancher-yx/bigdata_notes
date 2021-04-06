package arango;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShutDownService {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String account = "root";
        String passowrd = "123321";
        String authorization = "Basic "+(encryptBASE64(account,passowrd));
        String http = "http://192.168.121.128:8530/_admin/shutdown";
        URL url = null;


        HttpURLConnection httpUrlConnection = null ;
        InputStream inputStream = null;
        BufferedReader in = null;
        String str;
        try {
            url = new URL(http);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(1000);
            httpUrlConnection.setReadTimeout(1000);
            httpUrlConnection.setRequestMethod("DELETE");
            httpUrlConnection.setRequestProperty("authorization","Basic "+(encryptBASE64(account,passowrd)));
            inputStream = httpUrlConnection.getInputStream();
            in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((str = in.readLine()) != null) {
                System.out.println("response:"+str);
            }
        } catch (Exception e) {
            System.err.println("shutdown error:"+e.getMessage());
//            e.printStackTrace();
        }
    }
    public static String encryptBASE64(String username, String password) {
        byte[] key = (username+":"+password).getBytes();
        return  new String(Base64.encodeBase64(key));
    }
}


