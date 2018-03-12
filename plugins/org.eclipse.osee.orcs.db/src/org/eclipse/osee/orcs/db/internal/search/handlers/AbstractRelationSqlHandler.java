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
package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author John Misinco
 */
public abstract class AbstractRelationSqlHandler<T extends Criteria> extends SqlHandler<T> {
   protected T criteria;

   @Override
   public void setData(T criteria) {
      this.criteria = criteria;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATION_TYPE_EXISTS.ordinal();
   }
}