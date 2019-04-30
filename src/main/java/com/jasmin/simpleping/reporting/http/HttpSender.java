package com.jasmin.simpleping.reporting.http;

import com.jasmin.simpleping.exeption.APIException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.apache.http.HttpStatus.SC_OK;

@Slf4j
public class HttpSender {

    private final String url;
    private CloseableHttpClient httpClient;

    public HttpSender(String url) {
        this.url = url;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new SecureRandom());
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sc, new NoopHostnameVerifier());
            ConnectionSocketFactory socketFactory = new PlainConnectionSocketFactory();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslSocketFactory).register("http", socketFactory).build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            httpClient = HttpClients.custom().setConnectionManager(cm).build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("Error while initialisation HTTP Client", e);
            throw new APIException("Error while initialisation HTTP Client", e);
        }
    }

    public void sendPOST(String report) {
        try {
            URIBuilder builder = new URIBuilder(url);
            URI httpURI = builder.build();
            HttpPost post = new HttpPost(httpURI);
            post.setEntity(new StringEntity(report));
            post.setHeader("Connection", "Keep-Alive");
            post.setHeader("Proxy-Connection", "Keep-Alive");
            post.setHeader("Content-type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Accept-Language", "en-us,en;q=0.8");
            CloseableHttpResponse httpResponse = httpClient.execute(post);

            int httpStatus = httpResponse.getStatusLine().getStatusCode();
            if (httpStatus == SC_OK) {
                log.info("Report server accepted the report request");
            } else {
                log.error("Report server didn't accept the report request");
            }
        } catch (IOException | URISyntaxException e) {
            log.error("Error while report request", e);
        }
    }
}
