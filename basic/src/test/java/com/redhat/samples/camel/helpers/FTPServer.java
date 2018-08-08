package com.redhat.samples.camel.helpers;

import java.io.File;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

public class FTPServer {

    private final FtpServer ftpServer;

    public FTPServer(int port) {
        ftpServer = createServer(port);
    }

    private FtpServer createServer(int port) {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(port);
        serverFactory.addListener("default", listenerFactory.createListener());
        PropertiesUserManagerFactory managerFactory = new PropertiesUserManagerFactory();
        managerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        managerFactory.setFile(new File("src/test/resources/ftp/users.properties"));
        serverFactory.setUserManager(managerFactory.createUserManager());

        NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
        fileSystemFactory.setCreateHome(true);
        serverFactory.setFileSystem(fileSystemFactory);

        return serverFactory.createServer();
    }

    public FTPServer start() throws FtpException {
        ftpServer.start();
        return this;
    }

    public FTPServer stop() {
        ftpServer.stop();
        return this;
    }

}
