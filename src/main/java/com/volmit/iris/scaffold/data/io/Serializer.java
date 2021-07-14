package com.volmit.iris.scaffold.data.io;

import java.io.*;

public interface Serializer<T> {

    void toStream(T object, OutputStream out) throws IOException;

    default void toFile(T object, File file) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            toStream(object, bos);
        }
    }

    default byte[] toBytes(T object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        toStream(object, bos);
        bos.close();
        return bos.toByteArray();
    }
}
