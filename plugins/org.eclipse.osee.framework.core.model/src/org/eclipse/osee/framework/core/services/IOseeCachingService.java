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
package org.eclipse.osee.framework.core.services;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeCachingService {

   ArtifactTypeCache getArtifactTypeCache();

   AttributeTypeCache getAttributeTypeCache();

   RelationTypeCache getRelationTypeCache();

   OseeEnumTypeCache getEnumTypeCache();

   BranchCache getBranchCache();

   Collection<?> getCaches();

   IOseeCache<?> getCache(OseeCacheEnum cacheId);

   void reloadTypes();

   void reloadAll();

   void clearAll();

   OrcsTokenService getTokenService();
}
