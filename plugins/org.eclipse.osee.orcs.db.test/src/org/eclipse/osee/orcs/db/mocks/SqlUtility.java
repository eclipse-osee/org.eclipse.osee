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
package org.eclipse.osee.orcs.db.mocks;

import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.db.internal.search.SqlHandler;
import org.eclipse.osee.orcs.db.internal.search.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public final class SqlUtility {

   private SqlUtility() {
      //Utility Class 
   }

   public static CriteriaSet createCriteria(IOseeBranch branch, Criteria... criteria) {
      CriteriaSet set = new CriteriaSet(branch);
      for (Criteria crit : criteria) {
         set.add(crit);
      }
      return set;
   }

   public static List<SqlHandler> createHandlers(CriteriaSet criteriaSet) throws OseeCoreException {
      IdentityService service = new MockIdentityService() {

         @Override
         public int getLocalId(Identity<Long> identity) {
            return identity.getGuid().intValue();
         }

      };
      SqlHandlerFactory factory = new SqlHandlerFactoryImpl(service);
      return factory.createHandlers(criteriaSet);
   }
}
