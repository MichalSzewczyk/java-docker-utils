package com.szewczyk.microservices.utils.db.etcd;

import com.szewczyk.microservices.utils.db.NoSqlDBUtils;
import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.EtcdClient;

import java.net.URI;

import static java.util.Objects.isNull;

@Slf4j
public class EtcdDBUtils implements NoSqlDBUtils {
    private EtcdClient etcd;
    private final String etcdUri;

    public EtcdDBUtils(String etcdUri) {
        this.etcdUri = etcdUri;
    }

    @Override
    public boolean putUnderDirectory(String key, String value) {
        if (isNull(etcd)) {
            synchronized (this) {
                etcd = initializeEtcdClient(etcdUri);
            }
        }
        try {
            etcd.put(key, value);
            log.info("Value {} successfully added to ETCD under key {}.", value, key);
            return true;
        } catch (Throwable throwable) {
            log.error("Value {} not set under key {} due to exception.", throwable);
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        if (!isNull(etcd)) {
            etcd.close();
        }
    }

    private EtcdClient initializeEtcdClient(String etcdUri) {
        return new EtcdClient(URI.create(etcdUri));
    }
}
