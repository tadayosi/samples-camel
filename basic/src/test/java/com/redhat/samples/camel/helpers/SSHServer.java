package com.redhat.samples.camel.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

public class SSHServer {

    private static final String USERNAME = "sample";
    private static final String PASSWORD = "password";
    private static final Path DIR_HOME = Paths.get("target/sftp").toAbsolutePath();
    private static final Path DIR_IN = Paths.get("target/sftp/in").toAbsolutePath();

    private final SshServer sshServer;

    public SSHServer(int port) throws IOException {
        sshServer = createServer(port);
    }

    private SshServer createServer(int port) throws IOException {
        Files.createDirectories(DIR_IN);

        SshServer sshServer = SshServer.setUpDefaultServer();
        sshServer.setPort(port);
        sshServer.setKeyPairProvider(
            new SimpleGeneratorHostKeyProvider(Paths.get("target/hostkey.ser")));
        sshServer.setSubsystemFactories(Arrays.asList(
            new SftpSubsystemFactory()
        ));
        sshServer.setPasswordAuthenticator((username, password, session) -> {
            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                sshServer.setFileSystemFactory(
                    new VirtualFileSystemFactory(DIR_HOME));
                return true;
            }
            return false;
        });
        return sshServer;
    }

    public SSHServer start() throws IOException {
        sshServer.start();
        return this;
    }

    public SSHServer stop() throws IOException {
        sshServer.stop(true);
        return this;
    }

}
