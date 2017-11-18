package com.szewczyk.microservices.utils.db;

import java.util.List;
import java.util.Optional;

public interface NoSqlDBUtils extends AutoCloseable {

    boolean putUnderDirectory(String key, String value);

    Optional<List<String>> getAllEntriesFrom(String directory);
}
