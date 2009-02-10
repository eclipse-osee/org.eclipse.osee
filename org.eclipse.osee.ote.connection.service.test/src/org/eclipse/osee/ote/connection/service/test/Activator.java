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
package org.eclipse.osee.ote.connection.service.test;

import java.rmi.Remote;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.connection.service.IConnectorListener;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.ote.connection.jini.JiniServiceSideConnector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
   private ServiceTracker connectionServiceTracker;
   private static Activator instance = null;
   private JiniServiceSideConnector testConnector;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;
      connectionServiceTracker = new ServiceTracker(context, IConnectionService.class.getName(), null);
      connectionServiceTracker.open();
      final IConnectionService service = getConnectionService();
//      Entry[] entries = new Entry[] {new ServiceInfo("Dummy Test Service", "boeing", "ken", "1.0.0", "test model", "")};
      EnhancedProperties properties = new EnhancedProperties();
      properties.setProperty("name", "Dummy Test Service");
      testConnector = new JiniServiceSideConnector(new TestJiniService(), properties);
      service.addConnector(testConnector);

      service.addListener(new IConnectorListener() {

         @Override
         public void onConnectionServiceStopped() {
         }

         @Override
         public void onConnectorsAdded(Collection<IServiceConnector> connectors) {
            for (IServiceConnector connector : connectors) {
               if (connector.getService() instanceof Remote) {
                  System.out.printf("found remote service %s. connector type=%s\n", connector.getProperty("name",
                        "N.A."), connector.getConnectorType());
               } else {
                  System.out.println("found a non-remote service!?. connector type=" + connector.getConnectorType());
               }
            }
         }

         @Override
         public void onConnectorRemoved(IServiceConnector connector) {
         }

      });

      final Timer timer = new Timer();
      timer.schedule(new TimerTask() {

         @Override
         public void run() {
            System.out.println("connectors:");
            for (IServiceConnector connector : service.getAllConnectors()) {
               System.out.printf("\ttype=%s\n", connector.getConnectorType());
            }
            timer.cancel();
         }

      }, 10000);
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      instance = null;
      getConnectionService().removeConnector(testConnector);
      connectionServiceTracker.close();
   }

   public Activator getDefault() {
      return instance;
   }

   public IConnectionService getConnectionService() {
      return (IConnectionService) connectionServiceTracker.getService();
   }
}
