package com.jasmin.simpleping.parsers;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IcmpOutputParserTest {

    private final IcmpOutputParser parser = new IcmpOutputParser();

    @Test
    public void testEmptyString() {
        boolean containsError = parser.containsError("");
        assertThat(containsError, is(false));
    }

    @Test
    public void testZeroLossString() {
        String zeroLoss= "Pinging jasmin.com [109.71.161.154] with 32 bytes of data:" +
                "Reply from 109.71.161.154: bytes=32 time=44ms TTL=58Reply from 109.71.161.154: bytes=32 time=46ms TTL=58" +
                "Reply from 109.71.161.154: bytes=32 time=44ms TTL=58Reply from 109.71.161.154: bytes=32 time=44ms TTL=58" +
                "Reply from 109.71.161.154: bytes=32 time=44ms TTL=58Ping statistics for 109.71.161.154:    " +
                "Packets: Sent = 5, Received = 5, Lost = 0 (0% loss),Approximate round trip times in milli-seconds:    Minimum = 44ms, Maximum = 46ms, Average = 44ms";
        boolean containsError = parser.containsError(zeroLoss);
        assertThat(containsError, is(false));
    }

    @Test
    public void testNonZeroLossString() {
        String nonZeroLoss= "Pinging google.com [172.217.20.174] with 32 bytes of data:\n" +
                "Request timed out.\n" +
                "Request timed out.\n" +
                "Request timed out.\n" +
                "Request timed out.\n" +
                "Reply from 172.217.20.174: bytes=32 time=24ms TTL=55\n" +
                "\n" +
                "Ping statistics for 172.217.20.174:\n" +
                "    Packets: Sent = 5, Received = 1, Lost = 4 (80% loss),\n" +
                "Approximate round trip times in milli-seconds:\n" +
                "    Minimum = 24ms, Maximum = 24ms, Average = 24ms";
        boolean containsError = parser.containsError(nonZeroLoss);
        assertThat(containsError, is(true));
    }

}