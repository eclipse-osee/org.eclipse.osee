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
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.orcs.script.dsl.ui.IOrcsObjectProvider;

/**
 * @author Roberto E. Escobar
 */
public class OrcsObjectProviderImpl implements IOrcsObjectProvider {

   private IOseeCachingService getCache() {
      return OsgiUtil.getService(getClass(), IOseeCachingService.class);
   }

   @Override
   public Iterable<? extends NamedId> getBranches() {
      IOseeCachingService caches = getCache();
      return caches != null ? caches.getBranchCache().getAll() : Collections.<NamedIdBase> emptyList();
   }

   @Override
   public Iterable<? extends NamedId> getArtifactTypes() {
      IOseeCachingService caches = getCache();
      return caches != null ? caches.getArtifactTypeCache().getAll() : Collections.<NamedIdBase> emptyList();
   }

   @Override
   public Iterable<? extends NamedId> getAttributeTypes() {
      IOseeCachingService caches = getCache();
      return caches != null ? caches.getAttributeTypeCache().getAll() : Collections.<NamedIdBase> emptyList();
   }

   @Override
   public Iterable<? extends NamedId> getRelationTypes() {
      IOseeCachingService caches = getCache();
      return caches != null ? caches.getRelationTypeCache().getAll() : Collections.<NamedIdBase> emptyList();
   }
}