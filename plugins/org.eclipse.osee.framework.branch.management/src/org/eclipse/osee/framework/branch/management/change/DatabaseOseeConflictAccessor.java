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

package org.eclipse.osee.framework.branch.management.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeStatement;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseOseeConflictAccessor {

   private static final String INSERT_CONFLICT =
      "INSERT INTO osee_conflict (conflict_id, merge_branch_id, source_gamma_id, dest_gamma_id, status, conflict_type) VALUES (?,?,?,?,?,?)";
   private static final String UPDATE_CONFLICT =
      "UPDATE osee_conflict SET source_gamma_id = ?, dest_gamma_id = ?, status = ? WHERE merge_branch_id = ? AND conflict_id = ? AND conflict_type = ?";
   private static final String DELETE_CONFLICT =
      "DELETE FROM osee_conflict WHERE merge_branch_id = ? AND conflict_id = ? AND conflict_type = ?";

   private static final String SELECT_CONFLICTS = "SELECT * FROM osee_conflict WHERE merge_branch_id = ?";

   private final IOseeDatabaseServiceProvider databaseProvider;

   public DatabaseOseeConflictAccessor(IOseeDatabaseServiceProvider databaseProvider) {
      this.databaseProvider = databaseProvider;
   }

   public IOseeDatabaseService getDatabaseService() throws OseeCoreException {
      return databaseProvider.getOseeDatabaseService();
   }

   public void load(Collection<Conflict> conflicts, MergeBranch mergeBranch) throws OseeCoreException {
      IOseeStatement statement = getDatabaseService().getStatement();
      try {
         statement.runPreparedQuery(SELECT_CONFLICTS, mergeBranch.getId());
         while (statement.next()) {
            int uniqueId = statement.getInt("conflict_id");
            Long sourceGammaId = statement.getLong("source_gamma_id");
            Long destGammaId = statement.getLong("dest_gamma_id");
            ConflictType conflictType = ConflictType.valueOf(statement.getInt("conflict_type"));
            ConflictStatus status = ConflictStatus.valueOf(statement.getInt("status"));
            conflicts.add(new Conflict(StorageState.LOADED, uniqueId, conflictType, mergeBranch, status, sourceGammaId,
               destGammaId));
         }
      } finally {
         statement.close();
      }
   }

   public void store(Collection<Conflict> conflicts) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();
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
      getDatabaseService().runBatchUpdate(INSERT_CONFLICT, insertData);
      getDatabaseService().runBatchUpdate(UPDATE_CONFLICT, updateData);
      getDatabaseService().runBatchUpdate(DELETE_CONFLICT, deleteData);
      for (Conflict conflict : conflicts) {
         conflict.clearDirty();
      }
   }

   private Object[] toInsertValues(Conflict conflict) {
      return new Object[] {conflict.getId(), conflict.getMergeBranch().getId(), conflict.getSourceGammaId(),
         conflict.getDestinationGammaId(), conflict.getStatus().getValue(), conflict.getType().getValue()};
   }

   private Object[] toUpdateValues(Conflict conflict) {
      return new Object[] {conflict.getSourceGammaId(), conflict.getDestinationGammaId(),
         conflict.getStatus().getValue(), conflict.getMergeBranch().getId(), conflict.getId(),
         conflict.getType().getValue()};
   }

   private Object[] toDeleteValues(Conflict conflict) {
      return new Object[] {conflict.getMergeBranch().getId(), conflict.getId(), conflict.getType().getValue()};
   }
}
