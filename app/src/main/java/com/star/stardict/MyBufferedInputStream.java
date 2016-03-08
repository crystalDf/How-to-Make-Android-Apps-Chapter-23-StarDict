package com.star.stardict;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyBufferedInputStream extends BufferedInputStream {

    private long mCurrentPosition;

    public MyBufferedInputStream(InputStream in) {
        super(in);
        mCurrentPosition = 0;
    }

    public long getCurrentPosition() {
        return mCurrentPosition;
    }

    @Override
    public synchronized long skip(long byteCount) throws IOException {
        int SKIP_BUFFER_SIZE = 2048;

        byte[] skipBuffer = new byte[SKIP_BUFFER_SIZE];

        long remaining = byteCount;

        int nr = 0;

        if (byteCount <= 0) {
            return 0;
        }

        while (remaining > 0) {
            try {
                nr = read(skipBuffer, 0, (int) Math.min(
                        SKIP_BUFFER_SIZE, remaining));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (nr < 0) {
                break;
            }

            remaining -= nr;
        }

        return byteCount - remaining;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        mCurrentPosition = 0;
    }

    @Override
    public synchronized int read() throws IOException {
        int result = super.read();
        if (result != -1) {
            mCurrentPosition++;
        }
        return result;
    }
}
