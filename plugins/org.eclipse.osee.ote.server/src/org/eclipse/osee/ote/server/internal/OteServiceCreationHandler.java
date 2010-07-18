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

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.connection.jini.JiniServiceSideConnector;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentServiceConfig;
import org.eclipse.osee.ote.server.OteServiceStarter;
import org.eclipse.osee.ote.server.PropertyParamter;
import org.osgi.framework.BundleContext;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteServiceCreationHandler extends AbstractTrackingHandler {

   private final static Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {OteServiceStarter.class};

   private final ITestEnvironmentServiceConfig config;
   private final PropertyParamter propertyParameter;
   private final String envFactoryClass;
   private OteServiceStarter oteServiceStarter;

   public OteServiceCreationHandler(ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, String envFactoryClass) {
      this.config = config;
      this.propertyParameter = propertyParameter;
      this.envFactoryClass = envFactoryClass;
   }

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      oteServiceStarter = getService(OteServiceStarter.class, services);
      try {
         oteServiceStarter.start(new JiniServiceSideConnector(), config, propertyParameter, envFactoryClass);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void onDeActivate() {
      try {
         oteServiceStarter.stop();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }
}
