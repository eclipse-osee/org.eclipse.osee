/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.relation;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeManager {

   private static IOseeCachingService getCacheService() {
      return ServiceUtil.getOseeCacheService();
   }

   public static AbstractOseeCache<RelationType> getCache() {
      return getCacheService().getRelationTypeCache();
   }

   /**
    * @return all Relation types
    */
   public static Collection<RelationType> getAllTypes() {
      return getCache().getAll();
   }

   public static RelationType getType(RelationTypeToken relationType) {
      if (relationType instanceof RelationType) {
         return (RelationType) relationType;
      }

      return getCache().get(relationType);
   }
}