package com.jasmin.simpleping.services;

import com.jasmin.simpleping.model.TcpPingResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpPingerTest {
    @Mock
    private HttpPinger pinger;

    @Mock
    private HttpURLConnection connection;

    @Test
    public void httpRequestCall() throws IOException {
        when(pinger.getConnection()).thenReturn(connection);
        when(pinger.httpRequestCall()).thenCallRealMethod();

        when(connection.getResponseCode()).thenReturn(200);
        TcpPingResponse ping = pinger.httpRequestCall();
        assertThat(ping.getResponseCode(), is(200));
        assertThat(ping.isReachable(), is(true));
        assertThat(ping.getOutput(), startsWith ("TCP Ping with HTTP request to the null has been successful :200.Round trip response time"));
    }
}