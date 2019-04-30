package com.jasmin.simpleping.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IcmpOutputParser {
    private final Pattern LOSS_PERCENT_PATTERN = Pattern.compile("\\((\\d+?)% loss\\)");

    public boolean containsError(String pingResult) {
        boolean containsError = false;
        Matcher matcher = LOSS_PERCENT_PATTERN.matcher(pingResult);
        if (matcher.find()) {
            int lost = Integer.valueOf(matcher.group(1));
            containsError = lost > 0;
        }
        return containsError;
    }
}
