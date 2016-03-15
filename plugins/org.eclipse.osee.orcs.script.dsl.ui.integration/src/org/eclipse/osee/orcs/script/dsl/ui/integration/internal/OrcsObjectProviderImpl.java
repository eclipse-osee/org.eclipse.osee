/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.script.dsl.ui.integration.internal;

import java.util.Collections;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.orcs.script.dsl.ui.IOrcsObjectProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class OrcsObjectProviderImpl implements IOrcsObjectProvider {

   private IOseeCachingService getCache() {
      return getService(IOseeCachingService.class);
   }

   @Override
   public Iterable<? extends NamedId> getBranches() {
      IOseeCachingService caches = getCache();
      return caches != null ? caches.getBranchCache().getAll() : Collections.<NamedId> emptyList();
   }

   @Override
   public Iterable<? extends Identifiable<Long>> getArtifactTypes() {
      IOseeCachingService caches = getCache();
      return caches != null ? caches.getArtifactTypeCache().getAll() : Collections.<Identifiable<Long>> emptyList();
   }

   @Override
   public Iterable<? extends Identifiable<Long>> getAttributeTypes() {
      IOseeCachingService caches = getCache();
      return caches != null ? caches.getAttributeTypeCache().getAll() : Collections.<Identifiable<Long>> emptyList();
   }

   @Override
   public Iterable<? extends Identifiable<Long>> getRelationTypes() {
      IOseeCachingService caches = getCache();
      return caches != null ? caches.getRelationTypeCache().getAll() : Collections.<Identifiable<Long>> emptyList();
   }

   private <T> T getService(Class<T> clazz) {
      T service = null;
      Bundle bundle = FrameworkUtil.getBundle(getClass());
      if (bundle != null) {
         BundleContext context = bundle.getBundleContext();
         if (context != null) {
            ServiceReference<T> reference = context.getServiceReference(clazz);
            if (reference != null) {
               service = context.getService(reference);
            }
         }
      }
      return service;
   }

}