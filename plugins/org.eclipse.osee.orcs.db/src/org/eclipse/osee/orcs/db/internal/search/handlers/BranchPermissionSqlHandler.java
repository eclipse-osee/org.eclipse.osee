/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchPermission;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Audrey Denk
 */
public class BranchPermissionSqlHandler extends SqlHandler<CriteriaBranchPermission> {

   private CriteriaBranchPermission criteria;
   @Override
   public void setData(CriteriaBranchPermission criteria) {
      this.criteria = criteria;
   }

   @Override
   public void writeOrder(AbstractSqlWriter writer) {
      writer.prepend("with branches as (");
      writer.removeDanglingSeparator("and");
      writer.write("), "); //
      writer.write("permissions as ("); //
      writer.write("   select distinct b.branch_id, "); //
      writer.write("   case when acl.branch_id is null then 0 else 1 end branch_has_row_in_acl, "); //
      writer.write(
         "max(case when acl.branch_id is not null and acl.privilege_entity_id = ? then acl.permission_id else 0 end) over (partition by b.branch_id) user_permission_on_branch, "); //
      writer.write(
         "max(case when acl.branch_id is not null and acl.privilege_entity_id = ? then acl.permission_id  else 0 end) over (partition by b.branch_id) everyone_permission_on_branch "); //
      writer.write("from branches b left join osee_branch_acl acl on b.branch_id = acl.branch_id) "); //
      writer.write("select b.*, case when user_permission_on_branch > 0 then user_permission_on_branch "); //
      writer.write("when everyone_permission_on_branch > 0 then everyone_permission_on_branch "); //
      writer.write("when branch_has_row_in_acl > 0 then " + PermissionEnum.DENY.getPermId() + " "); //
      writer.write("else -1 end current_user_permission from branches b, permissions p "); //
      writer.write("where b.branch_id = p.branch_id "); //
      writer.addParameter(criteria.getUserArtId().getId());
      writer.addParameter(CoreUserGroups.Everyone.getId());
   }

   @Override
   public boolean hasPredicates() {
      return false;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_PERMISSION.ordinal();
   }
}
