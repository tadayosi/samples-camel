package com.redhat.samples.camel;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.camel.test.junit4.CamelTestSupport;

import static org.hamcrest.CoreMatchers.is;

public abstract class FileTestSupport extends CamelTestSupport {

    protected void assertOutFile(Path outFile, String expected, String charset) throws IOException {
        assertThat(Files.exists(outFile), is(true));
        assertThat(
            String.join("\n", Files.readAllLines(outFile, Charset.forName(charset))),
            is(expected));
    }

}
