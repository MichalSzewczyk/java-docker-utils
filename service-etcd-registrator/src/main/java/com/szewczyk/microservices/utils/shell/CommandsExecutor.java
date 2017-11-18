package com.szewczyk.microservices.utils.shell;

import java.util.Optional;

public interface CommandsExecutor {
    Optional<String> execute(String... commands);
}