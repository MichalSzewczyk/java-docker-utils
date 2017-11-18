package com.szewczyk.microservices.utils.model;

public class ServiceDiscoveryData {
    private String serviceName;
    private String hostname;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}