/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Audrey Denk
 */
public class ChildrenFollowRelationSqlHandler extends SqlHandler<Criteria> {

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      writer.write(", 0 as top_rel_type, 0 as top_rel_order");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.FOLLOW_RELATION_TYPES.ordinal();
   }
}