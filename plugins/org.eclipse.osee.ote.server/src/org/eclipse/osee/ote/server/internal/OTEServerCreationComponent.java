/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.server.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.connection.jini.JiniServiceSideConnector;
import org.eclipse.osee.ote.server.OteServiceStarter;
import org.eclipse.osee.ote.server.PropertyParamter;
import org.eclipse.osee.ote.server.TestEnvironmentServiceConfigImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class OTEServerCreationComponent {

   private OteServiceStarter oteServiceStart;
   
   public void bindOteServiceStarter(OteServiceStarter oteServiceStart){
      this.oteServiceStart = oteServiceStart;
   }
   
   public void unbindOteServiceStarter(OteServiceStarter oteServiceStart){
      this.oteServiceStart = null;
   }
   
   public void start() {
      final String oteServerFactoryClass = System.getProperty("osee.ote.server.factory.class");
      if (oteServerFactoryClass != null) {
         try{
            String outfileLocation = System.getProperty("osee.ote.outfiles");
            if (outfileLocation == null) {
               outfileLocation = System.getProperty("java.io.tmpdir");
            }
            String title = System.getProperty("osee.ote.server.title");
            String name = System.getProperty("user.name");
            String keepEnvAliveWithNoUsersStr = System.getProperty("osee.ote.server.keepAlive");
            boolean keepEnvAliveWithNoUsers = true;
            if (keepEnvAliveWithNoUsersStr != null) {
               keepEnvAliveWithNoUsers = Boolean.parseBoolean(keepEnvAliveWithNoUsersStr);
            }
            final TestEnvironmentServiceConfigImpl config =
                  new TestEnvironmentServiceConfigImpl(keepEnvAliveWithNoUsers, title, name, outfileLocation, null);

            String version = "unknown";
            String comment = "";
            Bundle bundle = FrameworkUtil.getBundle(OTEServerCreationComponent.class);
            if(bundle != null){
               BundleContext context = bundle.getBundleContext();
               if(context != null){
                  version = context.getBundle().getHeaders().get("Bundle-Version").toString();
                  comment = context.getBundle().getHeaders().get("Bundle-Description").toString();
               }
            }
            String station = "unknown";
            try {
               station = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
               OseeLog.log(OTEServerCreationComponent.class, Level.SEVERE, ex);
            }
            boolean useJiniLookup = System.getProperty("osee.ote.use.lookup") != null;
            boolean isLocalConnector = false;

            int index = oteServerFactoryClass.indexOf('.');
            String type = oteServerFactoryClass.substring(index > 0 ? index + 1 : 0);
            final PropertyParamter propertyParameter =
                  new PropertyParamter(version, comment, station, type, useJiniLookup, isLocalConnector);

            Thread th = new Thread(new Runnable(){
               public void run(){
                  try {
                     oteServiceStart.start(new JiniServiceSideConnector(), config, propertyParameter, oteServerFactoryClass);
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            });
            th.start();
         } catch (Exception ex){
            OseeLog.log(getClass(), Level.SEVERE, ex);
         }
      }
   }

   public void stop() {
      try {
         oteServiceStart.stop();
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }

}
