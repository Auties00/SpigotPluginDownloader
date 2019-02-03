package download;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.HttpMethod;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.gson.Gson;
import download.template.Resource;
import utils.Utils;

import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

public class DependencyDownloader {
    public File getResource(String name, String id) throws IOException {
        File file = File.createTempFile(name, " .jar");
        file.deleteOnExit();

        try {
            URL url = new URL("https://api.spiget.org/v2/resources/" + id + "/download");
            tryCode(file, url);
        } catch (FileNotFoundException e) {
            System.out.println("Trying to download file using custom api...");
            return downloadIfFail(name, id);
        }catch (IOException e){
            System.out.println("Error: IOExetption");
            e.printStackTrace();
            return null;
        }

        return file;
    }

    private File downloadIfFail(String name, String id) throws IOException {
        File json = File.createTempFile(name ,".json");
        File file = File.createTempFile(name + "retry", ".jar");

        file.deleteOnExit();
        json.deleteOnExit();
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/" + id);
            tryCode(json, url);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        Gson gson = new Gson();
        Resource resource;
        try (Reader reader = new FileReader(json)) {
            resource = gson.fromJson(reader, Resource.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            InputStream in = getInputStream("https://www.spigotmc.org/" + resource.getFile().getUrl());
            if(in == null){
                System.out.println("NULL");
                return null;
            }

            writeCode(file, in);
        }catch (IOException e){
            e.printStackTrace();
           return  null;
        }

        return file;
    }


    private InputStream getInputStream(String url) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setTimeout(15000);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

        for (HttpCookie temp : Objects.requireNonNull(Utils.getCookies("https://www.google.com"))) {
            com.gargoylesoftware.htmlunit.util.Cookie cookie = new Cookie(temp.getDomain(), temp.getName(), temp.getValue());
            webClient.getCookieManager().addCookie(cookie);
        }

        InputStream inputStream;
        try {
            WebRequest wr = new WebRequest(new URL(url), HttpMethod.GET);
            Page page = webClient.getPage(wr);
            if (!(page instanceof HtmlPage)) {
                System.out.println("NO HTML");
                return null;
            }

            if (!((HtmlPage) page).asXml().contains("DDoS protection by Cloudflare")) {
                System.out.println("NOT CLOUDFARE");
                return null;
            }

            try {
                Thread.sleep(9000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            inputStream = webClient.getCurrentWindow().getEnclosedPage().getWebResponse().getContentAsStream();
            System.out.println(inputStream);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        return inputStream;
    }

    private void tryCode(File file, URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");

        InputStream in = connection.getInputStream();
        writeCode(file, in);
    }

    private void writeCode(File file, InputStream in) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

        byte[] buffer = new byte[1024];

        int numRead;
        while ((numRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, numRead);
        }

        in.close();
        out.close();
    }
}
