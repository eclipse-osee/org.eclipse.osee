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
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.orcs.data.CreateBranchData;

/**
 * @author David Miller
 */
public final class BranchInheritACLCallable extends JdbcTransaction {
   private final String INSERT_INTO_BRANCH_ACL =
      "INSERT INTO OSEE_BRANCH_ACL (permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?)";
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

      int lock = PermissionEnum.USER_LOCK.getPermId();
      int deny = PermissionEnum.DENY.getPermId();

      List<Object[]> data = new ArrayList<>();
      jdbcClient.runQuery(stmt -> {
         int permissionId = stmt.getInt("permission_id");
         Long priviledgeId = stmt.getLong("privilege_entity_id");
         if (branchData.getAuthor().equals(priviledgeId) && permissionId < lock && permissionId != deny) {
            permissionId = lock;
         }
         data.add(new Object[] {permissionId, priviledgeId, branchData.getBranch()});
      }, JdbcConstants.JDBC__MAX_FETCH_SIZE, GET_BRANCH_ACCESS_CONTROL_LIST, branchData.getParentBranch());

      if (!data.isEmpty()) {
         jdbcClient.runBatchUpdate(INSERT_INTO_BRANCH_ACL, data);
      }
   }
}
