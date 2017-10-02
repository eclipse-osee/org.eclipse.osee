/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public class ChangeBranchFieldCallable extends AbstractDatastoreTxCallable<Void> {

   private static final String UPDATE_BRANCH_FIELD = "UPDATE osee_branch SET %s = ? WHERE branch_id = ?";

   private final BranchId branch;
   private final String field;
   private final Object value;

   private ChangeBranchFieldCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, BranchId branch, String field, Object value) {
      super(logger, session, jdbcClient);
      this.branch = branch;
      this.field = field;
      this.value = value;
   }

   @Override
   protected Void handleTxWork(JdbcConnection connection)  {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNullOrEmpty(field, "column");
      String query = String.format(UPDATE_BRANCH_FIELD, field);
      getJdbcClient().runPreparedUpdate(connection, query, value, branch);
      return null;
   }

   public static Callable<Void> newBranchState(Log logger, OrcsSession session, JdbcClient jdbcClient, BranchId branch, BranchState branchState) {
      Conditions.checkNotNull(branchState, "branchState");
      return new ChangeBranchFieldCallable(logger, session, jdbcClient, branch, "branch_state", branchState.getValue());
   }

   public static Callable<Void> newBranchType(Log logger, OrcsSession session, JdbcClient jdbcClient, BranchId branch, BranchType branchType) {
      Conditions.checkNotNull(branchType, "branchType");
      return new ChangeBranchFieldCallable(logger, session, jdbcClient, branch, "branch_type", branchType.getValue());
   }

   public static Callable<Void> newBranchName(Log logger, OrcsSession session, JdbcClient jdbcClient, BranchId branch, String branchName) {
      Conditions.checkNotNullOrEmpty(branchName, "branchName");
      return new ChangeBranchFieldCallable(logger, session, jdbcClient, branch, "branch_name", branchName);
   }

   public static Callable<Void> newAssocArtId(Log logger, OrcsSession session, JdbcClient jdbcClient, BranchId branch, ArtifactId associatedArt) {
      return new ChangeBranchFieldCallable(logger, session, jdbcClient, branch, "associated_art_id", associatedArt);
   }
}
