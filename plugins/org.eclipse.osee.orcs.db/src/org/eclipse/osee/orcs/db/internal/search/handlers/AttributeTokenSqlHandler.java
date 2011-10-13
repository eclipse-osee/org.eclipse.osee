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
package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeyword;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.CriteriaPriority;
import org.eclipse.osee.orcs.db.internal.search.SqlHandler;
import org.eclipse.osee.orcs.db.internal.search.SqlWriter;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("unused")
public class AttributeTokenSqlHandler extends SqlHandler {

   private CriteriaAttributeKeyword criteria;

   // TODO Attach Quick Search Here

   @Override
   public void setData(Criteria criteria) {
      this.criteria = (CriteriaAttributeKeyword) criteria;
   }

   @Override
   public void addTables(SqlWriter writer) throws OseeCoreException {
      // TODO
   }

   @Override
   public void addPredicates(SqlWriter writer) throws OseeCoreException {
      // TODO
   }

   @Override
   public int getPriority() {
      return CriteriaPriority.ATTRIBUTE_TOKEN.ordinal();
   }
}
