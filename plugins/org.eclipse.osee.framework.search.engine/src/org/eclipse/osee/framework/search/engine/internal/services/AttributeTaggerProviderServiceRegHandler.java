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

import java.util.Map;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.tagger.DefaultAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.tagger.XmlAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTaggerProviderServiceRegHandler extends AbstractTrackingHandler {

   //@formatter:off
   private static final Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {
      IResourceLocatorManager.class, 
      IResourceManager.class
   };
   //@formatter:on

   private final TagProcessor tagProcess;
   private ServiceRegistration serviceRegistration1;
   private ServiceRegistration serviceRegistration2;

   public AttributeTaggerProviderServiceRegHandler(TagProcessor tagProcess) {
      super();
      this.tagProcess = tagProcess;
   }

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IResourceLocatorManager locatorManager = getService(IResourceLocatorManager.class, services);
      IResourceManager resourceManager = getService(IResourceManager.class, services);

      IAttributeTaggerProvider tagger1 =
         new DefaultAttributeTaggerProvider(tagProcess, locatorManager, resourceManager);
      IAttributeTaggerProvider tagger2 = new XmlAttributeTaggerProvider(tagProcess, locatorManager, resourceManager);

      serviceRegistration1 = context.registerService(IAttributeTaggerProvider.class.getName(), tagger1, null);
      serviceRegistration2 = context.registerService(IAttributeTaggerProvider.class.getName(), tagger2, null);
   }

   @Override
   public void onDeActivate() {
      OsgiUtil.close(serviceRegistration1);
      OsgiUtil.close(serviceRegistration2);
   }
}
