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
package org.eclipse.osee.framework.jini.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jini.service.interfaces.IService;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

public class OseeJini {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(OseeJini.class);

   public static Remote getRemoteReference(Remote object) throws ExportException {
      try {
         return getRemoteReference(object, Network.getValidIP());
      } catch (UnknownHostException ex) {
         logger.log(Level.SEVERE, "OseeJini.getRemoteReference: ", ex);
         return null;
      }
   }

   public static Remote getRemoteReference(Remote object, InetAddress inetAddress) throws ExportException {
      Exporter export =
            new BasicJeriExporter(TcpServerEndpoint.getInstance(inetAddress.getHostAddress(), 0), new BasicILFactory(),
                  false, false);
      return export.export(object);
   }

   public static void printClassLoaders(Class<?> clazz) {
      ClassLoader cl = clazz.getClassLoader();
      do {
         logger.log(Level.INFO, cl.toString());
         cl = cl.getParent();
      } while (cl != null);
   }

   public static boolean isServiceAlive(Object obj) {
      try {
         Thread.sleep(5000);
      } catch (InterruptedException e1) {

         e1.printStackTrace();
      }
      boolean returnVal = true;
      if (obj instanceof IService) {
         try {
            ((IService) obj).getServiceID();
         } catch (RemoteException e) {
            returnVal = false;
         }
      }
      return returnVal;
   }
}
