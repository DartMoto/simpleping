package com.jasmin.simpleping.services;

import com.jasmin.simpleping.exeption.APIException;
import com.jasmin.simpleping.parsers.IcmpOutputParser;
import com.jasmin.simpleping.reporting.Report;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class IcmpPinger implements Runnable {

    private ProcessBuilder processBuilder;
    private final IcmpOutputParser parser;
    private final Report report;


    public IcmpPinger(String host, Report report) {
        this.processBuilder = new ProcessBuilder("ping", "-n", "5", host);
        this.report = report;
        this.parser = new IcmpOutputParser();
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> ping = executor.submit(this::runPing);
        ExecutorService setterExecutor = Executors.newSingleThreadExecutor();
        setterExecutor.submit(() -> {
            try {
                while (!ping.isDone()) {
                    String pingResult = ping.get();
                    report.setIcmpPingData(pingResult);
                    if (parser.containsError(pingResult))
                        report.sendReport();
                }
            } catch (Exception e) {
                log.error("TCP/IP ping consumer got an error", e);
                throw new APIException(e);
            }
        });
    }

    public String runPing() {
        StringBuilder lastReply = new StringBuilder();
        try {
            Process process = getStart();
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String reply = "";
                while ((reply = stdInput.readLine()) != null) {
                    lastReply.append(reply);
                }
                while ((reply = stdError.readLine()) != null) {
                    lastReply.append(reply);
                }
            }
        } catch (IOException e) {
            log.error("icmp ping got error.", e);
            throw new APIException(e);
        }
        return lastReply.toString();
    }

    public Process getStart() throws IOException {
        return processBuilder.start();
    }
}
