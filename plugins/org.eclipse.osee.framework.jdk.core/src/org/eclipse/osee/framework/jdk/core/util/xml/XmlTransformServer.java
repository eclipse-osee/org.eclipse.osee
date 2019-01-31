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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Roberto E. Escobar
 */
public class XmlTransformServer {

   private static int MAX_PACKET_SIZE = 65536;
   private static int FINISH_PROCESSING_FLAG = -1;
   private static int TIMEOUT = 60000 * 3;

   private InputStream inputFromNetwork;
   private OutputStream outputToNetwork;

   private Socket clientSocket;
   private ServerSocket server;
   private int portNumber;
   private final String userHome;

   public XmlTransformServer() {
      this.server = null;
      this.portNumber = 0;
      this.userHome = System.getProperty("user.home");
   }

   private void initializeServer(int portNumber) throws Exception {
      this.portNumber = portNumber;
      try {
         server = new ServerSocket(portNumber);
         clientSocket = server.accept();
         inputFromNetwork = new BufferedInputStream(clientSocket.getInputStream());
         outputToNetwork = new BufferedOutputStream(clientSocket.getOutputStream());
      } catch (Exception ex) {
         throw new Exception("Unable to launch Server and get Connection. ", ex);
      }
   }

   public void process() throws Exception {
      performRead("xml", ".xml");
      performRead("xslt", ".xsl");
      performWrite();
   }

   private void performRead(String prefix, String extension) throws Exception {
      File file = new File(userHome + File.separator + prefix + portNumber + extension);
      file.createNewFile();
      FileOutputStream fos = new FileOutputStream(file);
      try {
         receiveStream(clientSocket, inputFromNetwork, new PrintStream(fos));
      } catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         fos.close();
      }
   }

   private void performWrite() throws Exception {
      File file = new File(userHome + File.separator + "html" + portNumber + ".html");
      file.createNewFile();
      FileOutputStream fos = new FileOutputStream(file);

      File fisXml = new File(userHome + File.separator + "xml" + portNumber + ".xml");
      File fisXslt = new File(userHome + File.separator + "xslt" + portNumber + ".xsl");
      ErrorListener listener = null;
      try {
         System.gc();
         Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(fisXslt));
         listener = transformer.getErrorListener();
         transformer.transform(new StreamSource(fisXml), new StreamResult(fos));
      } catch (Exception ex) {
         PrintWriter pw = new PrintWriter(fos);
         pw.write("Error during Transform. " + (listener != null ? listener.toString() : ""));
         ex.printStackTrace(pw);
      } finally {
         fos.close();
         fisXml.delete();
         fisXslt.delete();
      }
      System.gc();

      InputStream fis = new FileInputStream(file);
      try {
         sendStream(clientSocket, fis, outputToNetwork);
         Thread.sleep(1000);
      } catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         fis.close();
         file.delete();
      }
   }

   private void clearResources() throws Exception {
      if (outputToNetwork != null) {
         outputToNetwork.close();
      }
      if (inputFromNetwork != null) {
         inputFromNetwork.close();
      }
      if (clientSocket != null) {
         clientSocket.close();
      }
      if (server != null) {
         server.close();
      }
   }

   static public void sendStream(Socket socket, InputStream in, OutputStream out) throws Exception {
      socket.setSoTimeout(TIMEOUT);
      try {
         int numberOfBytes = 0;
         byte[] buffer = new byte[MAX_PACKET_SIZE];
         while ((numberOfBytes = in.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, numberOfBytes);
         }
         out.write(FINISH_PROCESSING_FLAG);
         out.flush();
      } catch (Exception ex) {
         throw new Exception("Error during send.", ex);
      }
   }

   static public void receiveStream(Socket socket, InputStream in, OutputStream out) throws Exception {
      socket.setSoTimeout(TIMEOUT);
      try {
         int numberOfBytes = 0;
         byte[] buffer = new byte[MAX_PACKET_SIZE];
         while ((numberOfBytes = in.read(buffer, 0, buffer.length)) != -1) {
            if (buffer[numberOfBytes - 1] == -1) {
               out.write(buffer, 0, numberOfBytes - 1);
               break;
            }
            out.write(buffer, 0, numberOfBytes);
         }
         out.flush();
      } catch (Exception ex) {
         throw new Exception("Error during receive.", ex);
      }
   }

   static public void receiveStream(Socket socket, InputStream in, Writer out) throws Exception {
      socket.setSoTimeout(TIMEOUT);
      try {
         int numberOfBytes = 0;
         byte[] buffer = new byte[MAX_PACKET_SIZE];
         while ((numberOfBytes = in.read(buffer, 0, buffer.length)) != -1) {
            if (buffer[numberOfBytes - 1] == -1) {
               out.write(new String(buffer, 0, numberOfBytes - 1));
               break;
            }
            out.write(new String(buffer, 0, numberOfBytes));
         }
         out.flush();
      } catch (Exception ex) {
         throw new Exception("Error during receive.", ex);
      }
   }

   public static void main(String args[]) {
      XmlTransformServer xmlTransformServer = new XmlTransformServer();
      try {
         int port = Integer.parseInt(args[0]);
         xmlTransformServer.initializeServer(port);
         xmlTransformServer.process();
      } catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         try {
            xmlTransformServer.clearResources();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }
}
