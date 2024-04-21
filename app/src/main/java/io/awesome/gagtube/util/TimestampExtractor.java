package io.awesome.gagtube.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimestampExtractor {
    public static final Pattern TIMESTAMPS_PATTERN = Pattern.compile("(?:^|(?!:)\\W)(?:([0-5]?[0-9]):)?([0-5]?[0-9]):([0-5][0-9])(?=$|(?!:)\\W)");

    private TimestampExtractor() {
    }

    public static TimestampMatchDTO getTimestampFromMatcher(final Matcher timestampMatches, final String baseText) {
        int timestampStart = timestampMatches.start(1);
        if (timestampStart == -1) {
            timestampStart = timestampMatches.start(2);
        }
        final int timestampEnd = timestampMatches.end(3);

        final String parsedTimestamp = baseText.substring(timestampStart, timestampEnd);
        final String[] timestampParts = parsedTimestamp.split(":");

        final int seconds;
        // timestamp format: XX:XX:XX
        if (timestampParts.length == 3) {
            seconds = Integer.parseInt(timestampParts[0]) * 3600 // hours
                    + Integer.parseInt(timestampParts[1]) * 60 // minutes
                    + Integer.parseInt(timestampParts[2]); // seconds
        }
        // timestamp format: XX:XX
        else if (timestampParts.length == 2) {
            seconds = Integer.parseInt(timestampParts[0]) * 60 // minutes
                    + Integer.parseInt(timestampParts[1]); // seconds
        } else {
            return null;
        }

        return new TimestampMatchDTO(timestampStart, timestampEnd, seconds);
    }

    public static class TimestampMatchDTO {
        private final int timestampStart;
        private final int timestampEnd;
        private final int seconds;

        public TimestampMatchDTO(final int timestampStart, final int timestampEnd, final int seconds) {
            this.timestampStart = timestampStart;
            this.timestampEnd = timestampEnd;
            this.seconds = seconds;
        }

        public int timestampStart() {
            return timestampStart;
        }

        public int timestampEnd() {
            return timestampEnd;
        }

        public int seconds() {
            return seconds;
        }
    }
}
