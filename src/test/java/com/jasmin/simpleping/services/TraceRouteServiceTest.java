package com.jasmin.simpleping.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TraceRouteServiceTest {
    @Mock
    private TraceRouteService traceRouteService;
    @Mock
    private Process process;

    @Test
    public void runTracert() throws IOException {
        String output = "Tracing route to jasmin.com [109.71.161.154]over a maximum of 30 hops:  1    <1 ms    <1 ms    <1 ms  192.168.88.1   2     1 ms     1 ms     1 ms  " +
                "vipa15.te.net.ua [195.138.80.140]   3    16 ms    22 ms    40 ms  vgw2-vipas.te.net.ua [195.138.70.193]   4     2 ms     1 ms     1 ms  " +
                "br2-to-r2-co.te.net.ua [195.138.70.167]   5     2 ms     2 ms     2 ms  odessa1-ge-0-0-0-857.ett.ua [80.93.126.13]   6    12 ms    12 ms    13 ms  " +
                "kv-od.ett.ua [80.93.127.245]   7    36 ms    36 ms    38 ms  ix-xe-2-2-0-0.thar1.w1t-warsaw.as6453.net [195.219.188.37]   8    66 ms    62 ms    60 ms  " +
                "if-ae-17-2.tcore2.fnm-frankfurt.as6453.net [195.219.87.93]   9    53 ms    54 ms    57 ms  if-ae-12-2.tcore1.fnm-frankfurt.as6453.net [195.219.87.2]  10    45 ms    46 ms    47 ms  " +
                "if-et-21-2.hcore1.lx0-luxembourg.as6453.net [195.219.156.225]  11    46 ms    45 ms    47 ms  5.23.10.3  12    46 ms    46 ms    45 ms  109.71.161.154 Trace complete.";
        when(traceRouteService.getStart()).thenReturn(process);
        when(traceRouteService.runTracert()).thenCallRealMethod();
        when(process.getInputStream()).thenReturn(new ByteArrayInputStream(output.getBytes()));
        when(process.getErrorStream()).thenReturn(new ByteArrayInputStream("".getBytes()));
        String tracert = traceRouteService.runTracert();
        assertThat(tracert, is(output));
    }
}