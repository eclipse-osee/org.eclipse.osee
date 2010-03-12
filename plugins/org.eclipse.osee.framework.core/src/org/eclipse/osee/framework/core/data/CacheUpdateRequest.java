/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class CacheUpdateRequest {

   private final OseeCacheEnum cacheId;
   private final Collection<Integer> itemsIds;

   public CacheUpdateRequest(OseeCacheEnum cacheId) {
      this(cacheId, Collections.<Integer> emptyList());
   }

   public CacheUpdateRequest(OseeCacheEnum cacheId, Collection<Integer> itemsIds) {
      this.cacheId = cacheId;
      this.itemsIds = itemsIds;
   }

   public OseeCacheEnum getCacheId() {
      return cacheId;
   }

   public Collection<Integer> getItemsIds() {
      return itemsIds;
   }
}
