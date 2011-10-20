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
package org.eclipse.osee.orcs.db.internal.search;

import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;

/**
 * @author Roberto E. Escobar
 */
public abstract class SqlHandler {

   private Log logger;
   private IdentityService idService;
   private TaggingEngine taggingEngine;
   private DataStoreTypeCache caches;

   public DataStoreTypeCache getTypeCaches() {
      return caches;
   }

   public void setTypeCaches(DataStoreTypeCache caches) {
      this.caches = caches;
   }

   public void setIdentityService(IdentityService idService) {
      this.idService = idService;
   }

   protected int toLocalId(Identity<Long> identity) throws OseeCoreException {
      return idService.getLocalId(identity);
   }

   public void setTaggingEngine(TaggingEngine taggingEngine) {
      this.taggingEngine = taggingEngine;
   }

   protected TaggingEngine getTaggingEngine() {
      return taggingEngine;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   protected Log getLogger() {
      return logger;
   }

   public abstract int getPriority();

   public abstract void setData(Criteria criteria);

   public abstract void addTables(SqlWriter writer) throws OseeCoreException;

   public abstract void addPredicates(SqlWriter writer) throws OseeCoreException;

   @SuppressWarnings("unused")
   public void addOrderBy(SqlWriter sqlWriter) throws OseeCoreException {
      // Do nothing
   }

}
