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
package org.eclipse.osee.framework.search.engine.internal.services;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.ServiceBindType;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager;
import org.eclipse.osee.framework.search.engine.internal.AttributeTaggerProviderManagerImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTaggingManagerServiceRegHandler extends AbstractTrackingHandler {

   private final Map<Class<?>, ServiceBindType> serviceDependencies = new HashMap<Class<?>, ServiceBindType>();

   private IAttributeTaggerProviderManager attributeTaggerManager;
   private ServiceRegistration serviceRegistration;

   public AttributeTaggingManagerServiceRegHandler() {
      super();
      this.serviceDependencies.put(IOseeCachingService.class, ServiceBindType.SINGLETON);
      this.serviceDependencies.put(IAttributeTaggerProvider.class, ServiceBindType.MANY);
   }

   @Override
   public Class<?>[] getDependencies() {
      return null;
   }

   @Override
   public Map<Class<?>, ServiceBindType> getConfiguredDependencies() {
      return serviceDependencies;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IOseeCachingService cachingService = getService(IOseeCachingService.class, services);
      IAttributeTaggerProvider attributeTaggerProvider = getService(IAttributeTaggerProvider.class, services);

      AttributeTypeCache attributeTypeCache = cachingService.getAttributeTypeCache();

      attributeTaggerManager = new AttributeTaggerProviderManagerImpl(attributeTypeCache);
      attributeTaggerManager.addAttributeTaggerProvider(attributeTaggerProvider);
      serviceRegistration =
         context.registerService(IAttributeTaggerProviderManager.class.getName(), attributeTaggerManager, null);
   }

   @Override
   public void onServiceAdded(BundleContext context, Class<?> clazz, Object services) {
      attributeTaggerManager.addAttributeTaggerProvider((IAttributeTaggerProvider) services);
   }

   @Override
   public void onServiceRemoved(BundleContext context, Class<?> clazz, Object services) {
      attributeTaggerManager.removeAttributeTaggerProvider((IAttributeTaggerProvider) services);
   }

   @Override
   public void onDeActivate() {
      OsgiUtil.close(serviceRegistration);
   }
}
