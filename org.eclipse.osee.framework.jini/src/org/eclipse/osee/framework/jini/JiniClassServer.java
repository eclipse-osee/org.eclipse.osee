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
package org.eclipse.osee.framework.jini;

import java.net.InetAddress;
import org.eclipse.osee.framework.plugin.core.server.ClassServer;
import org.eclipse.osee.framework.plugin.core.server.ResourceFinder;

/**
 * @author David Diepenbrock
 */
public class JiniClassServer {

   private static JiniClassServer myself;
   private final ClassServer classServer;
   private String hostName;
   private String url;

   private JiniClassServer() throws Exception {
      hostName = InetAddress.getLocalHost().getHostAddress();
      classServer = new ClassServer(0, InetAddress.getLocalHost());
      classServer.start();

      url = "http://" + hostName + ":" + classServer.getPort() + "/";

      System.setProperty("java.rmi.server.hostname", hostName);
      System.setProperty("java.rmi.server.codebase", url);

      System.out.println("hostname:" + System.getProperty("java.rmi.server.hostname"));
      System.out.println("url:" + System.getProperty("java.rmi.server.codebase"));

   }

   public void addResourceFinder(ResourceFinder finder) {
      classServer.addResourceFinder(finder);
   }

   /**
    * @return Return singleton JiniClassServer object reference.
    * @throws Exception If there was an error creating a socket on localhost.
    */
   public static JiniClassServer getInstance() throws Exception {
      if (myself == null || myself.classServer == null) myself = new JiniClassServer();
      return myself;
   }

   //   public void addPaths(String[] paths) {
   //	   
   //      System.out.println("Adding Paths:");
   //      for(int i = 0; i < paths.length; i++){
   //         System.out.println("\t" + paths[i]);
   //      }
   //      classServer.addPaths(paths);
   //   }

   //   public void addPath(String path) {
   //      addPaths(new String[]{path});
   //   }

   //   public void addClass(Class classObj) {
   //System.out.println("Adding Class: " + classObj.getCanonicalName());
   //      addPath(Lib.getClassLoadPath(classObj));
   //   }

   /**
    * Stops the class server, if one was started
    */
   public static void stopServer() {
      if (myself != null && myself.classServer != null) myself.classServer.terminate();
   }

   /**
    * @return Returns the url of the class server.
    */
   public String getUrl() {
      return url;
   }
}
