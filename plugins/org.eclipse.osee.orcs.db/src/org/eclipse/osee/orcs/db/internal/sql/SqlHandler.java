/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.sql;

import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.jdk.core.type.HasPriority;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public abstract class SqlHandler<T extends Criteria<?>, O extends Options> implements HasPriority {

   private Log logger;
   private IdentityService idService;

   public void setIdentityService(IdentityService idService) {
      this.idService = idService;
   }

   public IdentityService getIdentityService() {
      return idService;
   }

   public int toLocalId(Identity<Long> identity) throws OseeCoreException {
      return idService.getLocalId(identity);
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   @Override
   public abstract int getPriority();

   public abstract void setData(T criteria);

   public abstract void addTables(AbstractSqlWriter<O> writer) throws OseeCoreException;

   public abstract boolean addPredicates(AbstractSqlWriter<O> writer) throws OseeCoreException;

   @SuppressWarnings("unused")
   public void addWithTables(AbstractSqlWriter<O> writer) throws OseeCoreException {
      // Do Nothing
   }

   @SuppressWarnings("unused")
   public void addSelect(AbstractSqlWriter<O> sqlWriter) throws OseeCoreException {
      // Do Nothing
   }

}
