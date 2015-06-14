/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Buffer {

    public Buffer(int initialCapacity) {
        if (initialCapacity > 0) {
            byteBuffer = ByteBuffer.allocate(initialCapacity);
        }
    }

    public Buffer(byte[] bytes) {
        byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.position(bytes.length);
    }

    public void concat(Buffer other) {
        ByteBuffer newBuffer = ByteBuffer.allocate(length() + other.length());
        if (byteBuffer != null) {
            byteBuffer.flip();
            newBuffer.put(byteBuffer);
        }
        if (other.byteBuffer != null) {
            other.byteBuffer.flip();
            newBuffer.put(other.byteBuffer);
        }
        byteBuffer = newBuffer;
    }

    public int length() {
        return byteBuffer == null ? 0 : byteBuffer.position();
    }

    public String toString(String charsetName) {
        Charset charset = Charset.forName(charsetName);
        if (byteBuffer != null) {
            byte[] bytes = new byte[length()];
            byteBuffer.flip();
            byteBuffer.get(bytes);
            byteBuffer.flip();
            return new String(bytes, charset);
        }
        return "";
    }

    private ByteBuffer byteBuffer;
}
