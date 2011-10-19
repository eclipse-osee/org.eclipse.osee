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
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;

public abstract class SqlHandler {

   private IdentityService idService;
   private TagProcessor tagProcessor;

   public void setIdentityService(IdentityService idService) {
      this.idService = idService;
   }

   protected int toLocalId(Identity<Long> identity) throws OseeCoreException {
      return idService.getLocalId(identity);
   }

   public abstract int getPriority();

   public abstract void setData(Criteria criteria);

   public abstract void addTables(SqlWriter writer) throws OseeCoreException;

   public abstract void addPredicates(SqlWriter writer) throws OseeCoreException;

   @SuppressWarnings("unused")
   public void addOrderBy(SqlWriter sqlWriter) throws OseeCoreException {
      // Do nothing
   }

   public void setTagProcessor(TagProcessor tagProcessor) {
      this.tagProcessor = tagProcessor;
   }

   protected TagProcessor getTagProcessor() {
      return tagProcessor;
   }
}
