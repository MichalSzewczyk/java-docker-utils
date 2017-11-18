package com.szewczyk.microservices.utils.db.etcd;

import com.szewczyk.microservices.utils.db.NoSqlDBUtils;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.isNull;

public class EtcdDBUtils implements NoSqlDBUtils {
    private final Logger log = LoggerFactory.getLogger(EtcdDBUtils.class);

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
            etcd.put(key, value).send().get();
            log.info("Value {} successfully added to ETCD under key {}.", value, key);
            return true;
        } catch (Throwable throwable) {
            log.error("Value {} not set under key {} due to exception.", throwable);
            return false;
        }
    }

    @Override
    public Optional<List<String>> getAllEntriesFrom(String directory) {
        try {
            EtcdResponsePromise<EtcdKeysResponse> responsePromise = etcd.getDir(directory).recursive().send();
            EtcdKeysResponse keysResponse = responsePromise.get();
            return Optional.of(List.of(keysResponse.node.value));
        } catch (IOException | EtcdException | TimeoutException | EtcdAuthenticationException e) {
            log.error("Unable to get value from etcd.", e);
            return Optional.empty();
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
