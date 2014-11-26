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
package org.eclipse.osee.orcs.db.internal.callable;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author John Misinco
 */
public class ChangeBranchTypeCallable extends AbstractDatastoreTxCallable<Void> {

   private static final String UPDATE_BRANCH_TYPE = "UPDATE osee_branch SET branch_type = ? WHERE branch_id = ?";

   private final IOseeBranch branch;
   private final BranchType newType;

   public ChangeBranchTypeCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, IOseeBranch branch, BranchType newType) {
      super(logger, session, jdbcClient);
      this.branch = branch;
      this.newType = newType;
   }

   @Override
   protected Void handleTxWork(JdbcConnection connection) throws OseeCoreException {
      Object[] params = new Object[] {newType.getValue(), branch.getUuid()};
      getJdbcClient().runPreparedUpdate(connection, UPDATE_BRANCH_TYPE, params);
      return null;
   }
}
