package utils;

import java.net.*;
import java.util.List;

public class Utils {
    public static List<HttpCookie> getCookies(String urlName){
        List<HttpCookie> cookies;
        try {
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);

            URL url = new URL(urlName);
            URLConnection connection = url.openConnection();
            connection.getContent();

            CookieStore cookieJar =  manager.getCookieStore();
            cookies = cookieJar.getCookies();
        } catch(Exception e) {
            System.out.println("Unable to get cookie using CookieHandler");
            e.printStackTrace();
            return null;
        }

        return cookies;
    }
}


