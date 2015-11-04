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
package org.eclipse.osee.orcs.db.internal.search.engines;

import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactQuerySqlContext extends QuerySqlContext {

   private final Long branchId;

   public ArtifactQuerySqlContext(OrcsSession session, Long branchId, Options options) {
      super(session, options, ObjectQueryType.ARTIFACT);
      this.branchId = branchId;
   }

   public long getBranchId() {
      return branchId;
   }

   @Override
   public String toString() {
      return "ArtifactQuerySqlContext [branch=" + branchId + "(" + super.toString() + ")]";
   }
}
