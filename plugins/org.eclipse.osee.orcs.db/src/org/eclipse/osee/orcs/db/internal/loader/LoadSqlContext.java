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
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.LoadContext;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.data.HasBranch;
import org.eclipse.osee.orcs.db.internal.sql.SqlContextImpl;

/**
 * @author Roberto E. Escobar
 */
public class LoadSqlContext extends SqlContextImpl implements LoadContext, HasBranch {

   private final IOseeBranch branch;

   public LoadSqlContext(OrcsSession session, Options options, IOseeBranch branch) {
      super(session, options);
      this.branch = branch;
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
   }

}
