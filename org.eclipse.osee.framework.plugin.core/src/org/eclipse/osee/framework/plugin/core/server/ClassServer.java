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
package org.eclipse.osee.framework.plugin.core.server;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

public class ClassServer extends Thread {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ClassServer.class);
   private ServerSocket server;
   private URL hostName;
   private List<ResourceFinder> resourceFinders;

   /**
    * Construct a server
    * 
    * @param port the port to use
    * @param address
    * @throws IOException if the server socket cannot be created
    */
   public ClassServer(int port, InetAddress address) throws IOException {
      server = new ServerSocket(port, 50, address);

      hostName = new URL("http://" + address.getHostAddress() + ":" + port + "/");

      this.setName("OSEE ClassServer");
      this.resourceFinders = new ArrayList<ResourceFinder>();
   }

   public void addResourceFinder(ResourceFinder finder) {
      this.resourceFinders.add(finder);
   }

   /**
    * Spawn a thread for each connection requesting service
    */
   public void run() {

      String msg = "ClassServer started [";

      msg += "\n\tport ";
      msg += Integer.toString(getPort());
      msg += "]";

      logger.log(Level.INFO, msg, new Object[0]);
      try {
         while (true) {
            new Task(server.accept()).start();
         }
      } catch (IOException e) {
         synchronized (this) {
            if (!server.isClosed()) {
               logger.log(Level.SEVERE, "accepting connection", e);
               terminate();
               logger.log(Level.WARNING, "ClassServer Terminated");
            }
         }
      }
   }

   /**
    * Terminate Server - perform cleanup
    */
   public synchronized void terminate() {
      try {
         server.close();
      } catch (IOException e) {
         // do nothing
      }
      final Iterator<ResourceFinder> iter = resourceFinders.iterator();
      while (iter.hasNext()) {
         final ResourceFinder resFinder = iter.next();
         resFinder.dispose();
      }

   }

   /** Returns the port on which this server is listening. */
   public int getPort() {
      return server.getLocalPort();
   }

   /** Read up to CRLF, return false if EOF */
   private static boolean readLine(InputStream in, StringBuffer buf) throws IOException {
      while (true) {
         int c = in.read();
         if (c < 0) return buf.length() > 0;
         if (c == '\r') {
            in.mark(1);
            c = in.read();
            if (c != '\n') in.reset();
            return true;
         }
         if (c == '\n') return true;
         buf.append((char) c);
      }
   }

   /** Read the request/response and return the initial line. */
   private static String getInput(Socket sock, boolean isRequest) throws IOException {
      BufferedInputStream in = new BufferedInputStream(sock.getInputStream(), 256);
      StringBuffer buf = new StringBuffer(80);
      do {
         if (!readLine(in, buf)) return null;
      } while (isRequest && buf.length() == 0);
      String initial = buf.toString();
      do {
         buf.setLength(0);
      } while (readLine(in, buf) && buf.length() > 0);
      return initial;
   }

   /**
    * This method provides a way for subclasses to be notified when a file has been completely downloaded.
    * 
    * @param fp The path to the file that was downloaded.
    */
   protected void fileDownloaded(String fp, InetAddress addr) {
   }

   public URL getHostName() {
      return hostName;
   }

   private class Task extends Thread {

      private Socket sock;

      public Task(Socket sock) {
         this.sock = sock;
         setDaemon(true);
      }

      /**
       * Get bytes from path Returns the bytes of the requested file, or null if not found.
       */
      private byte[] getBytes(String path) throws IOException {
         byte[] bytes = null;
         for (int i = 0; i < resourceFinders.size(); i++) {
            ResourceFinder finder = resourceFinders.get(i);
            bytes = finder.find(path);
            if (bytes != null) {
               return bytes;
            }
         }
         logger.log(Level.WARNING, "requested file: '" + path + "' was not found.");
         return null;
      }

      /** Parse % HEX HEX from s starting at i */
      private char decode(String s, int i) {
         return (char) Integer.parseInt(s.substring(i + 1, i + 3), 16);
      }

      private String getCanonicalizedPath(String path) {
         try {
            if (path.regionMatches(true, 0, "http://", 0, 7)) {
               int i = path.indexOf('/', 7);
               if (i < 0)
                  path = "/";
               else
                  path = path.substring(i);
            }
            for (int i = path.indexOf('%'); i >= 0; i = path.indexOf('%', i + 1)) {
               char c = decode(path, i);
               int n = 3;
               if ((c & 0x80) != 0) {
                  switch (c >> 4) {
                     case 0xC:
                     case 0xD:
                        n = 6;
                        c = (char) (((c & 0x1F) << 6) | (decode(path, i + 3) & 0x3F));
                        break;
                     case 0xE:
                        n = 9;
                        c =
                              (char) (((c & 0x0f) << 12) | ((decode(path, i + 3) & 0x3F) << 6) | (decode(path, i + 6) & 0x3F));
                        break;
                     default:
                        return null;
                  }
               }
               path = path.substring(0, i) + c + path.substring(i + n);
            }
         } catch (Exception e) {
            return null;
         }
         if (path.length() == 0 || path.charAt(0) != '/') return null;
         return path.substring(1);
      }

      public void run() {
         try {
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            String req;
            try {
               req = getInput(sock, true);
            } catch (Exception e) {
               logger.log(Level.INFO, "reading request", e);
               return;
            }
            if (req == null) return;
            if (req.startsWith("SHUTDOWN *")) {
               out.writeBytes("HTTP/1.0 403 Forbidden\r\n\r\n");
               out.flush();
               return;
            }
            String[] args = null;
            boolean get = req.startsWith("GET ");
            if (!get && !req.startsWith("HEAD ")) {
               logger.log(Level.FINE, "bad request \"{0}\" from {1}:{2}", args);
               out.writeBytes("HTTP/1.0 400 Bad Request\r\n\r\n");
               out.flush();
               return;
            }
            String path = req.substring(get ? 4 : 5);
            int i = path.indexOf(' ');
            if (i > 0) path = path.substring(0, i);
            path = getCanonicalizedPath(path);
            if (path == null) {
               logger.log(Level.FINE, "bad request \"{0}\" from {1}:{2}", args);
               out.writeBytes("HTTP/1.0 400 Bad Request\r\n\r\n");
               out.flush();
               return;
            }
            if (args != null) args[0] = path;
            logger.log(Level.FINER, get ? "{0} requested from {1}:{2}" : "{0} probed from {1}:{2}", args);
            byte[] bytes;
            try {
               bytes = getBytes(path);
            } catch (Exception e) {
               logger.log(Level.WARNING, "getting bytes", e);
               out.writeBytes("HTTP/1.0 500 Internal Error\r\n\r\n");
               out.flush();
               return;
            }
            if (bytes == null) {
               logger.log(Level.FINE, "{0} not found", path);
               out.writeBytes("HTTP/1.0 404 Not Found\r\n\r\n");
               out.flush();
               return;
            }
            out.writeBytes("HTTP/1.0 200 OK\r\n");
            out.writeBytes("Content-Length: " + bytes.length + "\r\n");
            out.writeBytes("Content-Type: application/java\r\n\r\n");
            if (get) out.write(bytes);
            out.flush();
            if (get) fileDownloaded(path, sock.getInetAddress());
         } catch (Exception e) {
            logger.log(Level.INFO, "writing response", e);
         } finally {
            try {
               sock.close();
            } catch (IOException e) {
            }
         }
      }
   }
}
