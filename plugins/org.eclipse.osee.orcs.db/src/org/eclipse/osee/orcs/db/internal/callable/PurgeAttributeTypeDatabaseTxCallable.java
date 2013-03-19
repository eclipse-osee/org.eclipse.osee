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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.database.schema.DatabaseTxCallable;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.util.HexUtil;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
public final class PurgeAttributeTypeDatabaseTxCallable extends DatabaseTxCallable<Object> {
   private static final String RETRIEVE_GAMMAS_OF_ATTR_TYPE_TXS =
      "SELECT gamma_id FROM osee_attribute WHERE attr_type_id = ?";

   private static final String DELETE_BY_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";
   private static final String DELETE_FROM_CONFLICT_TABLE_SOURCE_SIDE =
      "DELETE FROM osee_conflict WHERE source_gamma_id = ?";
   private static final String DELETE_FROM_CONFLICT_TABLE_DEST_SIDE =
      "DELETE FROM osee_conflict WHERE dest_gamma_id = ?";

   private final AttributeTypeCache attributeCache;
   private final String[] typesToPurge;
   private final boolean forcePurge;
   private final IdentityService identityService;
   private final Console console;

   public PurgeAttributeTypeDatabaseTxCallable(Log logger, IOseeDatabaseService databaseService, IdentityService identityService, AttributeTypeCache attributeCache, Console console, boolean force, String[] typesToPurge) {
      super(logger, databaseService, "Purge Attribute Type");
      this.identityService = identityService;
      this.attributeCache = attributeCache;
      this.console = console;
      this.forcePurge = force;
      this.typesToPurge = typesToPurge;
   }

   @Override
   protected Object handleTxWork(OseeConnection connection) throws OseeCoreException {
      console.writeln();
      console.writeln(!forcePurge ? "Attribute Types:" : "Purging attribute types:");

      Set<Long> types = convertTypeNamesToUuids();
      boolean found = !types.isEmpty();
      if (forcePurge && found) {
         console.writeln("Removing from osee_* tables...");
         processDeletes(connection, retrieveGammaIds(connection, types));
      }

      console.writeln((found && !forcePurge) ? "To >DELETE Attribute DATA!< add --force to confirm." : "Operation finished.");
      return null;
   }

   private Set<Long> convertTypeNamesToUuids() throws OseeCoreException {
      Set<Long> uuids = new HashSet<Long>();
      for (String uuid : typesToPurge) {
         try {
            Long converted = HexUtil.toLong(uuid);
            AttributeType type = attributeCache.getByGuid(converted);
            console.writeln("Type [%s] found. Guid: [0x%X]", type.getName(), type.getGuid());
            uuids.add(type.getGuid());
         } catch (OseeArgumentException ex) {
            console.writeln("Type [0x%X] NOT found.", uuid);
            console.writeln(ex);
         }
      }
      return uuids;
   }

   private List<Integer[]> retrieveGammaIds(OseeConnection connection, Set<Long> types) throws OseeCoreException {
      List<Integer[]> gammas = new ArrayList<Integer[]>(50000);
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         for (Long attributeTypeId : types) {
            chStmt.runPreparedQuery(RETRIEVE_GAMMAS_OF_ATTR_TYPE_TXS, identityService.getLocalId(attributeTypeId));
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