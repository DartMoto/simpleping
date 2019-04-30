package com.jasmin.simpleping.model;

import lombok.Data;

@Data
public class TcpPingResponse {
    private String output;
    private long responseTime;
    private int responseCode;
    private boolean isReachable;
}
