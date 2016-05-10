package com.pixalione.maillageinterne.semanticproximity.Services;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by OUASMINE Mohammed Amine on 09/05/2016.
 */
public class GetSourceCode {

    private String sourcepage = null;
    private static final Pattern CHARSET_HEADER = Pattern.compile("charset=([-_a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

    public GetSourceCode(String url) throws IOException, URISyntaxException {
        String charsetName = "UTF-8";
        URL myUrl = new URL(url);
        String nullFragment = null;
        URI uri = new URI(myUrl.getProtocol(), myUrl.getHost(), myUrl.getPath(), myUrl.getQuery(), nullFragment);

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);

        org.apache.http.HttpResponse response = client.execute(httpGet);

        String headerValue = response.getEntity().getContentType().getValue();

        int n = headerValue.indexOf(";");
        if (n != -1) {
            Matcher matcher = CHARSET_HEADER.matcher(headerValue);
            if (matcher.find()) {
                charsetName = matcher.group(1);
            }
        }

        if (response.getStatusLine().getStatusCode() == 200) {
            sourcepage = EntityUtils.toString(response.getEntity(), charsetName);
        }
        client.getConnectionManager().shutdown();

    }

    public String getSourcecode() {
        return sourcepage;
    }
}
