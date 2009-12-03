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
import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeCachingService {

   BranchCache getBranchCache();

   TransactionCache getTransactionCache();

   ArtifactTypeCache getArtifactTypeCache();

   AttributeTypeCache getAttributeTypeCache();

   RelationTypeCache getRelationTypeCache();

   OseeEnumTypeCache getEnumTypeCache();

   Collection<?> getCaches();

   IOseeCache<?> getCache(OseeCacheEnum cacheId) throws OseeCoreException;

   void reloadAll() throws OseeCoreException;

   void clearAll();
}
