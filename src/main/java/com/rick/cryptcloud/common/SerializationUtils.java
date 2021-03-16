package com.rick.cryptcloud.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class SerializationUtils<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String fileName;

    private ObjectInputStream ois;

    private ObjectOutputStream oos;

    public SerializationUtils(String fileName) {
        this.fileName = fileName;
    }

    public SerializationUtils() {
    }

    public void saveObjToFile(T obj) {
        try {
            oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(obj);
            oos.close();
            log.info("Object Serialize Success!");
        } catch (IOException e) {
            log.error("Object Serialize Failed: {}", e.getMessage());
        }
    }

    public T getObjToFile() {
        try {
            ois = new ObjectInputStream(new FileInputStream(fileName));
            return (T) ois.readObject();
        } catch (Exception e) {
            log.error("Object UnSerialize Failed: {}", e.getMessage());
        }
        return null;
    }
}
