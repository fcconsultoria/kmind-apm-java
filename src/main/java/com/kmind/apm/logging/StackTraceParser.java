package com.kmind.apm.logging;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StackTraceParser {
    
    private static final Pattern STACK_TRACE_PATTERN = Pattern.compile(
        "^at\\s+(.+)\\.([^.]+)\\(([^:]+)(:(\\d+))?\\)$");

    public static List<StackFrame> parse(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
            .map(StackTraceParser::parseFrame)
            .collect(Collectors.toList());
    }

    private static StackFrame parseFrame(StackTraceElement element) {
        return new StackFrame(
            element.getClassName(),
            element.getMethodName(),
            element.getFileName(),
            element.getLineNumber()
        );
    }

    public static List<StackFrame> parse(String stackTraceStr) {
        return Arrays.stream(stackTraceStr.split("\n"))
            .map(String::trim)
            .map(StackTraceParser::parseLine)
            .filter(frame -> frame != null)
            .collect(Collectors.toList());
    }

    private static StackFrame parseLine(String line) {
        Matcher matcher = STACK_TRACE_PATTERN.matcher(line);
        if (matcher.matches()) {
            return new StackFrame(
                matcher.group(1),
                matcher.group(2),
                matcher.group(3),
                matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : -1
            );
        }
        return null;
    }

    public static class StackFrame {
        private final String className;
        private final String methodName;
        private final String file;
        private final int lineNumber;

        public StackFrame(String className, String methodName, String file, int lineNumber) {
            this.className = className;
            this.methodName = methodName;
            this.file = file;
            this.lineNumber = lineNumber;
        }

        // Getters
        public String getClassName() { return className; }
        public String getMethodName() { return methodName; }
        public String getFile() { return file; }
        public int getLineNumber() { return lineNumber; }
    }
}