package com.jasmin.simpleping.services;

import com.jasmin.simpleping.exeption.APIException;
import com.jasmin.simpleping.model.TcpPingResponse;
import com.jasmin.simpleping.reporting.Report;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.apache.http.HttpStatus.SC_OK;

@Slf4j
public class HttpPinger implements Runnable {

    private final String host;
    private final int responseTime;
    private final Report report;


    public HttpPinger(String host, int responseTime, Report report) {
        this.host = "http://www." + host;
        this.responseTime = responseTime;
        this.report = report;
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TcpPingResponse> ping = executor.submit(this::httpRequestCall);
        ExecutorService setterExecutor = Executors.newSingleThreadExecutor();
        setterExecutor.submit(() -> {
            try {
                while (!ping.isDone()) {
                    TcpPingResponse pingResult = ping.get();
                    report.setTcpPingData(pingResult.getOutput());
                    if (!pingResult.isReachable() || pingResult.getResponseCode() != SC_OK || pingResult.getResponseTime() > this.responseTime)
                        report.sendReport();
                }
            } catch (Exception e) {
                log.error("TCP/IP ping consumer got an error", e);
                throw new APIException(e);
            }
        });
    }

    public TcpPingResponse httpRequestCall() {
        TcpPingResponse response = new TcpPingResponse();
        String currentReply = "";
        try {
            HttpURLConnection urlConnection = getConnection();

            long start = System.currentTimeMillis();
            urlConnection.connect();
            long end = System.currentTimeMillis();

            currentReply = "TCP Ping with HTTP request to the " + host + " has been successful :" + urlConnection.getResponseCode() + ".Round trip response time = " + (end - start) + " millis";
            response.setResponseCode(urlConnection.getResponseCode());
            response.setResponseTime(end - start);
        } catch (IOException e) {
            currentReply = "TCP Ping to " + host + " was unsuccessful. The host is unreachable:" + e.getMessage();
            response.setReachable(false);
        }
        response.setOutput(currentReply);
        response.setReachable(true);
        return response;
    }

    public HttpURLConnection getConnection() throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(host).openConnection();
        urlConnection.setConnectTimeout(this.responseTime + 1000);
        urlConnection.setReadTimeout(this.responseTime + 1000);
        urlConnection.setRequestMethod("HEAD");
        return urlConnection;
    }
}
