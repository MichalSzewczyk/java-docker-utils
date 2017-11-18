package com.szewczyk.microservices.utils.core;

public interface Registration {
    /*****
     * Based on fact that in custom network each docker microservice ip address is available in DNS under it's container id
     * we can simply add container id to etcd with for discovery purposes.
     * returns true if succeeded
     * else false
     */
    boolean registerMicroServiceContainerIdUnder(String key, String serviceName);
}
