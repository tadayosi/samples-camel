package com.redhat.samples.camel.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.apache.camel.Exchange;

public class StreamHelper {

    /** 100 * 10 = 1 KB */
    public static final String DEFAULT_LINE =
        String.join("", Collections.nCopies(100, "0123456789"));

    /** 100 MB */
    public static final long DEFAULT_COUNT = 1000 * 100;

    private final String line;
    private final long count;

    public StreamHelper() {
        this(DEFAULT_LINE, DEFAULT_COUNT);
    }

    public StreamHelper(long count) {
        this(DEFAULT_LINE, count);
    }

    public StreamHelper(String line, long count) {
        this.line = line;
        this.count = count;
    }

    public long length() {
        return line.length() * count;
    }

    public void produce(Exchange exchange) throws IOException {
        PipedOutputStream out = new PipedOutputStream();
        exchange.getIn().setBody(new PipedInputStream(out));
        new Thread(() -> {
            try (OutputStreamWriter osw = new OutputStreamWriter(out);
                 BufferedWriter writer = new BufferedWriter(osw)) {
                LongStream.range(0, count).forEach(i -> {
                    try {
                        writer.write(line);
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void consume(Exchange exchange) throws IOException {
        try (InputStream in = exchange.getIn().getBody(InputStream.class);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            long length = reader.lines()
                .collect(Collectors.summingLong(String::length));
            exchange.getIn().setBody(length);
        }
    }
}
