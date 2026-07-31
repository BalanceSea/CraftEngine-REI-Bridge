package net.mountainseal.cereibridge.client.net;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

public final class ChunkAssembler {
    private int expectedTotal = -1;
    private byte[][] chunks;
    private int received;

    public synchronized Optional<byte[]> accept(ChunkPayload payload) {
        if (payload.total() <= 0 || payload.total() > 65_536) {
            reset();
            return Optional.empty();
        }
        if (payload.total() != expectedTotal) {
            expectedTotal = payload.total();
            chunks = new byte[expectedTotal][];
            received = 0;
        }
        if (payload.index() < 0 || payload.index() >= chunks.length || chunks[payload.index()] != null) {
            return Optional.empty();
        }
        chunks[payload.index()] = payload.data();
        received++;
        if (received < expectedTotal) {
            return Optional.empty();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] chunk : chunks) {
            output.write(chunk, 0, chunk.length);
        }
        byte[] assembled = output.toByteArray();
        reset();
        return Optional.of(assembled);
    }

    public synchronized void reset() {
        expectedTotal = -1;
        chunks = null;
        received = 0;
    }
}
