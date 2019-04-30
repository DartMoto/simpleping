package com.jasmin.simpleping.reporting;

import com.jasmin.simpleping.model.CommandOutputHolder;
import com.jasmin.simpleping.reporting.http.HttpSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Report {

    private final String host;
    private final HttpSender sender;
    private final CommandOutputHolder<String> icmpPing = new CommandOutputHolder<>();
    private final CommandOutputHolder<String> tcpPing = new CommandOutputHolder<>();
    private final CommandOutputHolder<String> tracert = new CommandOutputHolder<>();


    public Report(String host, String reportUrl) {
        this.sender = new HttpSender(reportUrl);
        this.host = host;
    }

    public void setIcmpPingData(String icmpPing) {
        log.debug(icmpPing);
        this.icmpPing.produce(icmpPing);
    }

    public void setTcpPingData(String tcpPing) {
        log.debug(tcpPing);
        this.tcpPing.produce(tcpPing);
    }

    public void setTraceRouteData(String tracert) {
        log.debug(tracert);
        this.tracert.produce(tracert);
    }

    public String getTraceRouteData() {
        try {
            return this.tracert.consume();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getIcmpPingData() {
        try {
            return this.icmpPing.consume();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTcpPingData() {
        try {
            return this.tcpPing.consume();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateReport() {
        String jsonReport = String.format("{\"host\":\"%s\", \"icmp_ping\":\"%s\", \"tcp_ping\":\"%s\", \"trace\":\"%s\"}",
                host, getIcmpPingData(), getTcpPingData(), getTraceRouteData());
        log.warn(jsonReport);
        return jsonReport;
    }

    public synchronized void sendReport() {
        sender.sendPOST(generateReport());
    }
}
