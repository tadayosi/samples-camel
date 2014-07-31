package com.redhat.samples.camel.ftp

import java.io.File

import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory

class FTPServer(port: Int) {

  private val ftpServer: FtpServer = createServer

  private def createServer: FtpServer = {
    val serverFactory = new FtpServerFactory
    val listenerFactory = new ListenerFactory
    listenerFactory.setPort(port)
    serverFactory.addListener("default", listenerFactory.createListener)
    val managerFactory = new PropertiesUserManagerFactory
    managerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor)
    managerFactory.setFile(new File("src/test/resources/ftp/users.properties"))
    val createUserManager = managerFactory.createUserManager
    serverFactory.setUserManager(createUserManager)

    val fileSystemFactory = new NativeFileSystemFactory
    fileSystemFactory.setCreateHome(true)
    serverFactory.setFileSystem(fileSystemFactory)

    serverFactory.createServer
  }

  def start: FTPServer = {
    ftpServer.start
    this
  }

  def stop: FTPServer = {
    ftpServer.stop
    this
  }

}
