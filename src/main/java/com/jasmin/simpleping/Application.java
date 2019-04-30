package com.jasmin.simpleping;

import com.jasmin.simpleping.exeption.APIException;
import com.jasmin.simpleping.reporting.Report;
import com.jasmin.simpleping.services.HttpPinger;
import com.jasmin.simpleping.services.IcmpPinger;
import com.jasmin.simpleping.services.TraceRouteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Application {

    private static final int NUMBER_OF_SERVICES = 3;

    public static void main(String[] args) {
        Application app = new Application();
        app.runSimplePing();
    }

    public void runSimplePing() {
        Configurations configs = new Configurations();
        try {
            Configuration configuration = configs.properties(new File("configuration.properties"));

            String[] hosts = configuration.getStringArray("host");
            int tcpDelay = configuration.getInt("tcp.delay", 3000);
            int icmpDelay = configuration.getInt("icmp.delay", 20000);
            int traceDelay = configuration.getInt("trace.delay", 35000);
            int responseTime = configuration.getInt("response.time.ms");

            if (hosts.length == 0) {
                log.error("Please revise config file parameter(s) are missing, corrupted or incorrectly formatted");
                throw new IllegalArgumentException("Parameter(s) are missing, corrupted or incorrectly formatted");
            }
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(hosts.length * NUMBER_OF_SERVICES);

            for (String host : hosts) {
                Report report = new Report(host, configuration.getString("report.url"));
                IcmpPinger icmpPinger = new IcmpPinger(host, report);
                TraceRouteService traceRouteService = new TraceRouteService(host, report);
                HttpPinger httpPinger = new HttpPinger(host, responseTime, report);
                executorService.scheduleWithFixedDelay(icmpPinger, 0, icmpDelay, TimeUnit.MILLISECONDS);
                executorService.scheduleWithFixedDelay(traceRouteService, 0, traceDelay, TimeUnit.MILLISECONDS);
                executorService.scheduleWithFixedDelay(httpPinger, 0, tcpDelay, TimeUnit.MILLISECONDS);
            }

        } catch (ConfigurationException e) {
            log.error("Error while configuration load:", e);
            throw new APIException(e);
        }
    }


}
