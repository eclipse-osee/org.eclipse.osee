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
package org.eclipse.osee.framework.derby;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class Activator implements BundleActivator {

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      try {
         String derbyAddress = System.getProperty(OseeServerProperties.OSEE_DERBY_SERVER);
         if (Strings.isValid(derbyAddress)) {
            String[] hostPort = derbyAddress.split(":");
            DerbyDbServer.startServer(hostPort[0], Integer.parseInt(hostPort[1]));
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
   }

}
