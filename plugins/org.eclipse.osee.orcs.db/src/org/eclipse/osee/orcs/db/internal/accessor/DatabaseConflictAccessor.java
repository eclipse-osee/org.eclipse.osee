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

package org.eclipse.osee.orcs.db.internal.accessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.db.internal.conflict.Conflict;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseConflictAccessor {

   private static final String INSERT_CONFLICT =
      "INSERT INTO osee_conflict (conflict_id, merge_branch_id, source_gamma_id, dest_gamma_id, status, conflict_type) VALUES (?,?,?,?,?,?)";
   private static final String UPDATE_CONFLICT =
      "UPDATE osee_conflict SET source_gamma_id = ?, dest_gamma_id = ?, status = ? WHERE merge_branch_id = ? AND conflict_id = ? AND conflict_type = ?";
   private static final String DELETE_CONFLICT =
      "DELETE FROM osee_conflict WHERE merge_branch_id = ? AND conflict_id = ? AND conflict_type = ?";

   private static final String SELECT_CONFLICTS = "SELECT * FROM osee_conflict WHERE merge_branch_id = ?";

   private final JdbcClient jdbcClient;

   public DatabaseConflictAccessor(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   public void load(Collection<Conflict> conflicts, MergeBranch mergeBranch) {
      Consumer<JdbcStatement> consumer = stmt -> {
         Id uniqueId = ArtifactId.valueOf(stmt.getLong("conflict_id"));
         GammaId sourceGammaId = GammaId.valueOf(stmt.getLong("source_gamma_id"));
         GammaId destGammaId = GammaId.valueOf(stmt.getLong("dest_gamma_id"));
         ConflictType conflictType = ConflictType.valueOf(stmt.getInt("conflict_type"));
         ConflictStatus status = ConflictStatus.valueOf(stmt.getInt("status"));
         conflicts.add(
            new Conflict(StorageState.LOADED, uniqueId, conflictType, mergeBranch, status, sourceGammaId, destGammaId));
      };
      getJdbcClient().runQuery(consumer, SELECT_CONFLICTS, mergeBranch);
   }

   public void store(Collection<Conflict> conflicts) {
      List<Object[]> insertData = new ArrayList<>();
      List<Object[]> updateData = new ArrayList<>();
      List<Object[]> deleteData = new ArrayList<>();
      for (Conflict conflict : conflicts) {
         if (conflict.isDirty()) {
            switch (conflict.getStorageState()) {
               case CREATED:
                  insertData.add(toInsertValues(conflict));
                  break;
               case MODIFIED:
                  updateData.add(toUpdateValues(conflict));
                  break;
               case PURGED:
                  deleteData.add(toDeleteValues(conflict));
                  break;
               default:
                  break;
            }
         }
      }
      getJdbcClient().runBatchUpdate(INSERT_CONFLICT, insertData);
      getJdbcClient().runBatchUpdate(UPDATE_CONFLICT, updateData);
      getJdbcClient().runBatchUpdate(DELETE_CONFLICT, deleteData);
      for (Conflict conflict : conflicts) {
         conflict.clearDirty();
      }
   }

   private Object[] toInsertValues(Conflict conflict) {
      return new Object[] {
         conflict.getId(),
         conflict.getMergeBranch(),
         conflict.getSourceGammaId(),
         conflict.getDestinationGammaId(),
         conflict.getStatus().getValue(),
         conflict.getType().getValue()};
   }

   private Object[] toUpdateValues(Conflict conflict) {
      return new Object[] {
         conflict.getSourceGammaId(),
         conflict.getDestinationGammaId(),
         conflict.getStatus().getValue(),
         conflict.getMergeBranch(),
         conflict.getId(),
         conflict.getType().getValue()};
   }

   private Object[] toDeleteValues(Conflict conflict) {
      return new Object[] {conflict.getMergeBranch(), conflict.getId(), conflict.getType().getValue()};
   }
}
