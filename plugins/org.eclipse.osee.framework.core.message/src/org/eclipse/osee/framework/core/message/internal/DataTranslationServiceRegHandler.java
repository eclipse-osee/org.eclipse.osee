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
package org.eclipse.osee.framework.core.message.internal;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class DataTranslationServiceRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] DEPENDENCIES = new Class<?>[] {//
      IOseeCachingService.class, //
         IOseeModelFactoryService.class //
      };

   private ServiceRegistration registration;

   @Override
   public Class<?>[] getDependencies() {
      return DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IOseeCachingService cachingService = getService(IOseeCachingService.class, services);
      IOseeModelFactoryService factoryService = getService(IOseeModelFactoryService.class, services);

      TransactionRecordFactory txFactory = factoryService.getTransactionFactory();
      AttributeTypeFactory attributeTypeFactory = factoryService.getAttributeTypeFactory();

      DataTranslationServiceFactory factory = new DataTranslationServiceFactory();
      try {
         IDataTranslationService service = factory.createService(cachingService, txFactory, attributeTypeFactory);
         registration = context.registerService(IDataTranslationService.class.getName(), service, null);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void onDeActivate() {
      if (registration != null) {
         registration.unregister();
      }
   }

}
