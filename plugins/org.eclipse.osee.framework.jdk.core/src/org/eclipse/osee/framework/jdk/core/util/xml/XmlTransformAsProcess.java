/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;

/**
 * @author Roberto E. Escobar
 */
public class XmlTransformAsProcess {

   private Socket clientSocket;
   private InputStream inputFromNetwork;
   private OutputStream outputFromNetwork;
   private Process process;

   private XmlTransformAsProcess() {
      process = null;
      clientSocket = null;
      inputFromNetwork = null;
      outputFromNetwork = null;
   }

   private URL getClassLocation(final Class<XmlTransformServer> classToFind) {
      URL result = null;
      if (classToFind == null) {
         throw new IllegalArgumentException("Class is null");
      }
      final String classAsResource = classToFind.getName().replace('.', '/').concat(".class");
      final ProtectionDomain pd = classToFind.getProtectionDomain();
      if (pd != null) {
         final CodeSource cs = pd.getCodeSource();
         if (cs != null) {
            result = cs.getLocation();
         }
         if (result != null) {
            // Convert a code source location into a full class file location
            if (result.getProtocol().equals("file")) {
               try {
                  if (result.toExternalForm().endsWith(".jar") || result.toExternalForm().endsWith(".zip")) {
                     result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(classAsResource));
                  } else if (new File(result.getFile()).isDirectory()) {
                     result = new URL(result, classAsResource);
                  }
               } catch (MalformedURLException ignore) {
                  // do nothing
               }
            }
         }
      }
      if (result == null) {
         // Try to find class definition as a resource
         final ClassLoader classLoader = classToFind.getClassLoader();
         result = classLoader != null ? classLoader.getResource(classAsResource) : ClassLoader.getSystemResource(
            classAsResource);
      }
      return result;
   }

   private void connectToServer(InetAddress address, int port) throws Exception {
      try {
         for (int i = 0; i < 10; i++) {
            try {
               clientSocket = new Socket(address, port);
               break;
            } catch (Throwable th) {
               Thread.sleep(1000);
            }
         }
         System.out.println("Connected");
         inputFromNetwork = new BufferedInputStream(clientSocket.getInputStream());
         outputFromNetwork = new BufferedOutputStream(clientSocket.getOutputStream());
      } catch (Exception ex) {
         throw new Exception("Unable to Connect to Transform Server. ", ex);
      }
   }

   private void launchServer(int port) throws Exception {
      List<String> commands = new ArrayList<>();
      try {
         URL url = getClassLocation(XmlTransformServer.class);
         String path = new File(url.toURI()).getAbsolutePath();
         int indexOf = path.indexOf("bin");
         path = path.substring(0, indexOf + 4);
         File classFileLocation = new File(path);

         String className = XmlTransformServer.class.getName();

         commands.add("java");
         commands.add("Xmx1024M");
         commands.add(className);
         commands.add(Integer.toString(port));

         ProcessBuilder builder = new ProcessBuilder();
         builder.directory(classFileLocation);
         builder.command(commands);
         process = builder.start();
         Thread.sleep(800);
      } catch (URISyntaxException ex) {
         throw new Exception("Unable to find XmlTransformServer class in File System. ", ex);
      } catch (Exception ex) {
         throw new Exception("Unable to launch TransformServer. ", ex);
      }
   }

   public void processXml(InputStream xmlSource, InputStream xsltSource, Writer result) {
      try {
         XmlTransformServer.sendStream(clientSocket, xmlSource, outputFromNetwork);
         Thread.sleep(2000);
         System.gc();
         System.out.println("Sent Xml");
         XmlTransformServer.sendStream(clientSocket, xsltSource, outputFromNetwork);
         System.gc();
         Thread.sleep(2000);
         System.out.println("Sent Xslt");
         XmlTransformServer.receiveStream(clientSocket, inputFromNetwork, result);
         System.gc();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private void clearResources() throws Exception {
      if (inputFromNetwork != null) {
         inputFromNetwork.close();
      }
      if (outputFromNetwork != null) {
         outputFromNetwork.close();
      }
      if (clientSocket != null) {
         clientSocket.close();
      }
      if (process != null) {
         process.destroy();
      }
   }

   public static void getHtmlFromXml(InputStream xmlSource, InputStream xsltSource, Writer results) throws Exception {
      XmlTransformAsProcess xmlTransformProcess = new XmlTransformAsProcess();
      try {
         InetAddress address = InetAddress.getLocalHost();
         int port = PortUtil.getInstance().getValidPort();
         System.out.println("Transform Server at port: " + port);
         System.gc();
         xmlTransformProcess.launchServer(port);
         xmlTransformProcess.connectToServer(address, port);
         xmlTransformProcess.processXml(xmlSource, xsltSource, results);

      } finally {
         xmlTransformProcess.clearResources();
         System.gc();
      }
   }

   public static void main(String[] args) throws Exception {
      InputStream xmlInput = new FileInputStream(args[0]);
      InputStream xsltInput = new FileInputStream(args[1]);
      getHtmlFromXml(xmlInput, xsltInput, new PrintWriter(System.out));
   }
}
