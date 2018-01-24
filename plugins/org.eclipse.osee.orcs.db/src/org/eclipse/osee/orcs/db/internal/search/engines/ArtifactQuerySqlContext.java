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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactQuerySqlContext extends QuerySqlContext {

   private final BranchId branch;

   public ArtifactQuerySqlContext(OrcsSession session, QueryData queryData) {
      super(session, queryData.getOptions(), ObjectQueryType.ARTIFACT);
      this.branch = queryData.getBranch();
   }

   public BranchId getBranch() {
      return branch;
   }

   @Override
   public String toString() {
      return "ArtifactQuerySqlContext [branch=" + branch + "(" + super.toString() + ")]";
   }
}
