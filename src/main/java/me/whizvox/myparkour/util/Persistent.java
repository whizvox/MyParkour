package me.whizvox.myparkour.util;

import me.whizvox.myparkour.MyParkour;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Persistent<T> {

    T writePersistent();

    void readPersistent(T obj);

    Type getPersistentType();

    default void save(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            T obj = writePersistent();
            MyParkour.inst().getGson().toJson(obj, writer);
        }
    }

    default void load(Path path) throws IOException {
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                T obj = MyParkour.inst().getGson().fromJson(reader, getPersistentType());
                readPersistent(obj);
            }
        }
    }

}
