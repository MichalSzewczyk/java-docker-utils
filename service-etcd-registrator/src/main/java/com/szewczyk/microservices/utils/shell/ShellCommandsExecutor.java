package com.szewczyk.microservices.utils.shell;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

@Slf4j
public class ShellCommandsExecutor implements CommandsExecutor {
    public Optional<String> execute(String... commands) {
        try {
            log.info("Started commands execution: {}", commands);
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(commands);
            String commandErrorResult = processInputFrom(proc.getErrorStream());
            log.error("Command execution returned error: {}", commandErrorResult);
            String result = processInputFrom(proc.getInputStream());
            log.info("Success.");
            return Optional.of(result);
        } catch (IOException ioException) {
            log.error("Execution of commands {} failed.", commands, ioException);
            return Optional.empty();
        }
    }

    private String processInputFrom(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(inputStream))) {
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                stringBuilder.append(s);
            }
        }
        return stringBuilder.toString();
    }
}