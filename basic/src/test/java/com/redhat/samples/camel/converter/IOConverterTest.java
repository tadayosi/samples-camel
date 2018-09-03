package com.redhat.samples.camel.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.camel.converter.IOConverter;
import org.junit.Before;
import org.junit.Test;

/**
 * https://issues.apache.org/jira/browse/CAMEL-12769
 */
public class IOConverterTest {

    private static final String TEST_DIR = "/tmp/samples-camel/" + IOConverterTest.class.getSimpleName();

    private static final String INPUT_XML = String.format("%s-input.xml", IOConverterTest.class.getSimpleName());

    private final Path inputXml = Paths.get(TEST_DIR, INPUT_XML);

    @Before
    public void clearInputFiles() throws IOException {
        Files.deleteIfExists(inputXml);
    }

    @Test
    public void fileToInputStream() throws IOException {
        System.out.println(Charset.defaultCharset());
        Files.createDirectories(inputXml.getParent());
        Files.copy(getClass().getResourceAsStream("/converter/" + INPUT_XML), inputXml);
        InputStream in = IOConverter.toInputStream(inputXml.toFile(), "Shift_JIS");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
        System.out.println(new String(out.toByteArray(), StandardCharsets.UTF_8));
    }
}
