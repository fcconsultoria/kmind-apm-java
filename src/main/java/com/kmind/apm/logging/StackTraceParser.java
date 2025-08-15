package com.kmind.apm.logging;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StackTraceParser {
    
    /**
     * Converte um array de StackTraceElement para a estrutura simplificada
     * que corresponde ao formato Node.js especificado
     */
    public static List<StackFrame> parse(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
            .map(StackTraceParser::parseFrame)
            .collect(Collectors.toList());
    }

    /**
     * Converte um StackTraceElement individual para o formato simplificado
     */
    private static StackFrame parseFrame(StackTraceElement element) {
        return new StackFrame(
            element.getFileName(),  // Nome do arquivo apenas (sem path)
            element.getMethodName(), // Nome do método
            element.getLineNumber(), // Número da linha
            -1                      // Coluna (não disponível em Java)
        );
    }

    /**
     * Classe interna que representa um frame de stack trace no formato Node.js
     */
    public static class StackFrame {
        private final String file;        // Nome do arquivo (ex: "UserController.java")
        private final String methodName;  // Nome do método (ex: "getUser")
        private final int lineNumber;     // Número da linha
        private final int column;         // Coluna (sempre -1 em Java)

        public StackFrame(String file, String methodName, int lineNumber, int column) {
            this.file = file;
            this.methodName = methodName;
            this.lineNumber = lineNumber;
            this.column = column;
        }

        // Getters (necessários para serialização JSON)
        public String getFile() { return file; }
        public String getMethodName() { return methodName; }
        public int getLineNumber() { return lineNumber; }
        public int getColumn() { return column; }
    }
}