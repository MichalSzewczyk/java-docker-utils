package com.szewczyk.microservices.utils.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szewczyk.microservices.utils.model.ServiceDiscoveryData;
import com.szewczyk.microservices.utils.shell.CommandsExecutor;
import com.szewczyk.microservices.utils.db.NoSqlDBUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DockerContainerUtils implements Registration, Discovery, AutoCloseable {
    private static final String GET_CONTAINER_ID_COMMAND = "cat /proc/1/cgroup | grep 'docker/' | tail -1 | sed 's/^.*\\///' | cut -c 1-12";

    private final CommandsExecutor commandsExecutor;
    private final NoSqlDBUtils databaseConnectionUtils;
    private final ObjectMapper jsonObjectMapper;

    public DockerContainerUtils(CommandsExecutor commandsExecutor, NoSqlDBUtils databaseConnectionUtils, ObjectMapper jsonObjectMapper) {
        this.commandsExecutor = commandsExecutor;
        this.databaseConnectionUtils = databaseConnectionUtils;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    public boolean registerMicroServiceContainerIdUnder(String key, String serviceName) {
        return commandsExecutor
                .execute(GET_CONTAINER_ID_COMMAND)
                .filter(s -> databaseConnectionUtils.putUnderDirectory(key + "/" + serviceName, s))
                .isPresent();
    }

    @Override
    public List<ServiceDiscoveryData> discoveryServicesFrom(String directory) {
        List<String> discoveryEntries = databaseConnectionUtils.getAllEntriesFrom(directory);
        return discoveryEntries
                .stream()
                .map(this::deserializeToServiceData)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void close() throws Exception {
        databaseConnectionUtils.close();
    }

    private Optional<ServiceDiscoveryData> deserializeToServiceData(String from) {
        try {
            return Optional.of(jsonObjectMapper.readValue(from, ServiceDiscoveryData.class));
        } catch (IOException e) {
            log.error("Unable to deserialize discovery data: {}", from, e);
            return Optional.empty();
        }
    }
}
