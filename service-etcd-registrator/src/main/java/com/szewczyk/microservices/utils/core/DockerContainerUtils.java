package com.szewczyk.microservices.utils.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szewczyk.microservices.utils.model.ServiceDiscoveryData;
import com.szewczyk.microservices.utils.shell.CommandsExecutor;
import com.szewczyk.microservices.utils.db.NoSqlDBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DockerContainerUtils implements Registration, Discovery, AutoCloseable {
    private final Logger log = LoggerFactory.getLogger(DockerContainerUtils.class);
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
        log.info("Registration of service {} with name");
        return commandsExecutor
                .execute(GET_CONTAINER_ID_COMMAND)
                .filter(s -> databaseConnectionUtils.putUnderDirectory(key + "/" + serviceName, s))
                .isPresent();
    }

    @Override
    public List<ServiceDiscoveryData> discoveryServicesFrom(String directory) {
        Optional<List<String>> discoveryEntries = databaseConnectionUtils.getAllEntriesFrom(directory);
        if (!discoveryEntries.isPresent()) {
            return Collections.emptyList();
        }
        return discoveryEntries.get()
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
