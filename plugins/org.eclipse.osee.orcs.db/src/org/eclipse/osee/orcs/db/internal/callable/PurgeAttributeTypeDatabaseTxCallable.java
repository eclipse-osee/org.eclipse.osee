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
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.database.schema.DatabaseTxCallable;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
public final class PurgeAttributeTypeDatabaseTxCallable extends DatabaseTxCallable<Void> {
   private static final String RETRIEVE_GAMMAS_OF_ATTR_TYPE_TXS =
      "SELECT gamma_id FROM osee_attribute WHERE attr_type_id = ?";

   private static final String DELETE_BY_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";
   private static final String DELETE_FROM_CONFLICT_TABLE_SOURCE_SIDE =
      "DELETE FROM osee_conflict WHERE source_gamma_id = ?";
   private static final String DELETE_FROM_CONFLICT_TABLE_DEST_SIDE =
      "DELETE FROM osee_conflict WHERE dest_gamma_id = ?";

   private final Collection<? extends IAttributeType> typesToPurge;
   private final IdentityService identityService;

   public PurgeAttributeTypeDatabaseTxCallable(Log logger, IOseeDatabaseService databaseService, IdentityService identityService, Collection<? extends IAttributeType> typesToPurge) {
      super(logger, databaseService, "Purge Attribute Type");
      this.identityService = identityService;
      this.typesToPurge = typesToPurge;
   }

   @Override
   protected Void handleTxWork(OseeConnection connection) throws OseeCoreException {
      List<Integer[]> gammaIds = retrieveGammaIds(connection, typesToPurge);
      processDeletes(connection, gammaIds);
      return null;
   }

   private List<Integer[]> retrieveGammaIds(OseeConnection connection, Collection<? extends IAttributeType> types) throws OseeCoreException {
      List<Integer[]> gammas = new ArrayList<Integer[]>(50000);
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         for (IAttributeType type : types) {
            chStmt.runPreparedQuery(RETRIEVE_GAMMAS_OF_ATTR_TYPE_TXS, identityService.getLocalId(type));
            while (chStmt.next()) {
               gammas.add(new Integer[] {chStmt.getInt("gamma_id")});
            }
         }
      } finally {
         chStmt.close();
      }

      return gammas;
   }

   private void processDeletes(OseeConnection connection, List<Integer[]> gammas) throws OseeCoreException {
      getDatabaseService().runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS, "osee_txs"), gammas);
      getDatabaseService().runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS, "osee_txs_archived"), gammas);
      getDatabaseService().runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS, "osee_attribute"), gammas);
      getDatabaseService().runBatchUpdate(connection, String.format(DELETE_FROM_CONFLICT_TABLE_SOURCE_SIDE), gammas);
      getDatabaseService().runBatchUpdate(connection, String.format(DELETE_FROM_CONFLICT_TABLE_DEST_SIDE), gammas);
   }
}