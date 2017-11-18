package com.szewczyk.microservices.utils.db;

public interface NoSqlDBUtils extends AutoCloseable{

    boolean putUnderDirectory(String key, String value);
}
