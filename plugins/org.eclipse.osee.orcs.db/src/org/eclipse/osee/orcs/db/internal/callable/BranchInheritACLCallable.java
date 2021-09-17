/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.orcs.db.internal.callable;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.data.CreateBranchData;

/**
 * @author David Miller
 */
public final class BranchInheritACLCallable extends JdbcTransaction {
   private final String GET_BRANCH_ACCESS_CONTROL_LIST =
      "SELECT permission_id, privilege_entity_id FROM osee_branch_acl WHERE branch_id= ?";
   private final CreateBranchData branchData;
   private final JdbcClient jdbcClient;

   public BranchInheritACLCallable(JdbcClient jdbcClient, CreateBranchData branchData) {
      this.jdbcClient = jdbcClient;
      this.branchData = branchData;
   }

   @Override
   public void handleTxWork(JdbcConnection connection) {

      int read = PermissionEnum.READ.getPermId();
      int write = PermissionEnum.WRITE.getPermId();

      List<Object[]> data = new ArrayList<>();
      jdbcClient.runQueryWithMaxFetchSize(stmt -> {
         int permissionId = stmt.getInt("permission_id");
         Long priviledgeId = stmt.getLong("privilege_entity_id");
         if (permissionId == read) {
            permissionId = write;
         }
         data.add(new Object[] {branchData.getBranch(), priviledgeId, permissionId});
      }, GET_BRANCH_ACCESS_CONTROL_LIST, branchData.getParentBranch());

      if (!data.isEmpty()) {
         jdbcClient.runBatchUpdate(OseeDb.OSEE_BRANCH_ACL_TABLE.getInsertSql(), data);
      }
   }
}