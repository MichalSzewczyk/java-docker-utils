package com.szewczyk.microservices.utils.core;

import com.szewczyk.microservices.utils.shell.CommandsExecutor;
import com.szewczyk.microservices.utils.db.NoSqlDBUtils;

public class DockerContainerRegistration implements Registration, AutoCloseable {
    private static final String GET_CONTAINER_ID_COMMAND = "cat /proc/1/cgroup | grep 'docker/' | tail -1 | sed 's/^.*\\///' | cut -c 1-12";

    private final CommandsExecutor commandsExecutor;
    private final NoSqlDBUtils databaseConnectionUtils;

    public DockerContainerRegistration(CommandsExecutor commandsExecutor, NoSqlDBUtils databaseConnectionUtils) {
        this.commandsExecutor = commandsExecutor;
        this.databaseConnectionUtils = databaseConnectionUtils;
    }

    public boolean registerMicroServiceContainerIdUnder(String key, String serviceName) {
        return commandsExecutor
                .execute(GET_CONTAINER_ID_COMMAND)
                .filter(s -> databaseConnectionUtils.putUnderDirectory(key + "/" + serviceName, s))
                .isPresent();
    }

    @Override
    public void close() throws Exception {
        databaseConnectionUtils.close();
    }
}
