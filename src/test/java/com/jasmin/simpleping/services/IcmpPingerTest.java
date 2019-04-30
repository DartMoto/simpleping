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
public class IcmpPingerTest {

    @Mock
    private IcmpPinger pinger;

    @Mock
    private Process process;

    @Test
    public void runPing() throws IOException {
        String output = "Pinging jasmin.com [109.71.161.154] with 32 bytes of data:Reply from 109.71.161.154: bytes=32 time=44ms TTL=58" +
                "Reply from 109.71.161.154: bytes=32 time=45ms TTL=58Reply from 109.71.161.154: bytes=32 time=45ms TTL=58" +
                "Reply from 109.71.161.154: bytes=32 time=46ms TTL=58Reply from 109.71.161.154: bytes=32 time=45ms TTL=58Ping statistics for 109.71.161.154:    " +
                "Packets: Sent = 5, Received = 5, Lost = 0 (0% loss),Approximate round trip times in milli-seconds:    Minimum = 44ms, Maximum = 46ms, Average = 45ms";
        when(pinger.getStart()).thenReturn(process);
        when(pinger.runPing()).thenCallRealMethod();
        when(process.getInputStream()).thenReturn(new ByteArrayInputStream(output.getBytes()));
        when(process.getErrorStream()).thenReturn(new ByteArrayInputStream("".getBytes()));
        String ping = pinger.runPing();
        assertThat(ping, is(output));
    }
}