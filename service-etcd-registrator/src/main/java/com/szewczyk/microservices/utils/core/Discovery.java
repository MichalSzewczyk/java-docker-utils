package com.szewczyk.microservices.utils.core;

import com.szewczyk.microservices.utils.model.ServiceDiscoveryData;

import java.util.List;

public interface Discovery {
    List<ServiceDiscoveryData> discoveryServicesFrom(String directory);
}