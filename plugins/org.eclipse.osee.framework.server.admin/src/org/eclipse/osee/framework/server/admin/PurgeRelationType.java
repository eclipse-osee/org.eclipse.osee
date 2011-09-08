/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.server.admin;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * Purges given relation types.<br/>
 * <p>
 * Tables involved:
 * <li>osee_txs</li>
 * <li>osee_txs_archived</li>
 * <li>osee_relation_link</li>
 * </p>
 * <br/>
 * 
 * @author Karol M. Wilk
 */
public final class PurgeRelationType extends AbstractDbTxOperation {
   private static final String RETRIEVE_GAMMAS_OF_REL_LINK_TXS =
      "SELECT rel_link.gamma_id FROM osee_relation_link rel_link WHERE rel_link.rel_link_type_id = ?";

   private static final String DELETE_BY_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";

   private static final String DELETE_FROM_CONFLICT_TABLE_SOURCE_SIDE =
      "DELETE FROM osee_conflict WHERE source_gamma_id = ?";
   private static final String DELETE_FROM_CONFLICT_TABLE_DEST_SIDE =
      "DELETE FROM osee_conflict WHERE dest_gamma_id = ?";

   private final RelationTypeCache cache;
   private final String[] typesToPurge;
   private final boolean forcePurge;
   private final List<Long[]> relationTypeGuids;
   private final IdentityService identityService;

   public PurgeRelationType(IOseeDatabaseService databaseService, IOseeCachingService cachingService, IdentityService identityService, OperationLogger logger, boolean force, String... typesToPurge) {
      super(databaseService, "Purge Relation Type", Activator.PLUGIN_ID, logger);
      this.cache = cachingService.getRelationTypeCache();
      this.forcePurge = force;
      this.typesToPurge = typesToPurge;
      this.relationTypeGuids = new ArrayList<Long[]>(typesToPurge.length);
      this.identityService = identityService;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {

      log();
      log(!forcePurge ? "Relation Types:" : "Purging relation types:");

      boolean found = collectInfo();

      if (forcePurge && found) {
         log("Removing from osee_* tables...");
         processDeletes(connection, retrieveGammaIds());
      }

      log((found && !forcePurge) ? "To >DELETE Relation DATA!< add --force to confirm." : "Operation finished.");
   }

   private boolean collectInfo() throws OseeCoreException {
      for (int i = 0; i < typesToPurge.length; i++) {

         Long guid;

         try {
            RelationType type = cache.getBySoleName(typesToPurge[i]);
            guid = type.getGuid();
            logf("Type [%s] found. Guid: [%s]\n", typesToPurge[i], guid);
            relationTypeGuids.add(new Long[] {guid});
         } catch (OseeArgumentException ex) {
            logf("Type [%s] NOT found. \n", typesToPurge[i]);
            log(ex);
         }

      }

      return !relationTypeGuids.isEmpty();
   }

   private List<Integer[]> retrieveGammaIds() throws OseeCoreException {
      List<Integer[]> gammas = new ArrayList<Integer[]>(50000);
      IOseeStatement chStmt = ConnectionHandler.getStatement();

      try {
         for (Long[] relationTypeId : relationTypeGuids) {
            chStmt.runPreparedQuery(RETRIEVE_GAMMAS_OF_REL_LINK_TXS, identityService.getLocalId(relationTypeId[0]));
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
      ConnectionHandler.runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS, "osee_txs"), gammas);
      ConnectionHandler.runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS, "osee_txs_archived"), gammas);
      ConnectionHandler.runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS, "osee_relation_link"), gammas);
      ConnectionHandler.runBatchUpdate(connection, String.format(DELETE_FROM_CONFLICT_TABLE_SOURCE_SIDE), gammas);
      ConnectionHandler.runBatchUpdate(connection, String.format(DELETE_FROM_CONFLICT_TABLE_DEST_SIDE), gammas);
   }
}