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
package org.eclipse.osee.ote.message.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private ServiceTracker testEnvTracker;
   private static Activator me;
   private MessageWatchActivator messageWatchActivator;
   private BundleContext context;
   
   
   public Activator() {
   }

   @Override
   public void start(BundleContext context) throws Exception {
      me = this;
      this.context = context;
      testEnvTracker = new ServiceTracker(context, TestEnvironmentInterface.class.getName(), null);
      testEnvTracker.open(true);
      
      messageWatchActivator = new MessageWatchActivator(context);
      messageWatchActivator.open(true);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      testEnvTracker.close();
      messageWatchActivator.close();
      context = null;
   }
   
   public static TestEnvironmentInterface getTestEnvironment(){
      try {
         return (TestEnvironmentInterface)me.testEnvTracker.waitForService(20000);
      } catch (InterruptedException e) {
         OseeLog.log(Activator.class, Level.SEVERE, e);
      }
      return null;
   }
   
   public static Activator getDefault() {
      return me;
   }
   
   BundleContext getBundleContext() {
      return context;
   }
}
