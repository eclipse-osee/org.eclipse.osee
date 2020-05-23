/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.LoadContext;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.db.internal.sql.SqlContextImpl;

/**
 * @author Roberto E. Escobar
 */
public class LoadSqlContext extends SqlContextImpl implements LoadContext, HasBranch {

   private final BranchId branch;

   public LoadSqlContext(OrcsSession session, Options options, BranchId branch) {
      super(session, options);
      this.branch = branch;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }
}
