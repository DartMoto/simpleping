package com.jasmin.simpleping.services;

import com.jasmin.simpleping.exeption.APIException;
import com.jasmin.simpleping.reporting.Report;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TraceRouteService implements Runnable {

    private final Report report;
    private final ProcessBuilder processBuilder;

    public TraceRouteService(String host, Report report) {
        this.report = report;
        this.processBuilder = new ProcessBuilder("tracert", host);
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> tracert = executor.submit(this::runTracert);
        ExecutorService setterExecutor = Executors.newSingleThreadExecutor();
        setterExecutor.submit(() -> {
            try {
                while (!tracert.isDone()) {
                    String tracertResult = tracert.get();
                    report.setTraceRouteData(tracertResult);
                }
            } catch (Exception e) {
                log.error("tracert consumer got an error", e);
                throw new APIException(e);
            }
        });

    }

    public String runTracert() {
        StringBuilder result = new StringBuilder();
        try {
            Process process = getStart();
            String line = "";
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                while ((line = stdInput.readLine()) != null) {
                    result.append(line);
                }
                while ((line = stdError.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (IOException e) {
            log.error("tracert got error.", e);
            throw new APIException(e);
        }
        return result.toString();
    }

    public Process getStart() throws IOException {
        return processBuilder.start();
    }
}