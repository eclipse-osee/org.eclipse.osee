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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;

/**
 * @author Jeff C. Phillips
 */
public class PurgeAttribute extends DbTransaction {
   private static final String DELETE_TXS =
      "DELETE FROM osee_txs txs1 WHERE EXISTS (select 1 from osee_join_id jt1 WHERE jt1.query_id = ? AND jt1.id = txs1.gamma_id)";
   private static final String DELETE_ATTR =
      "DELETE FROM osee_attribute attr1 WHERE EXISTS (select 1 from osee_join_id jt1 WHERE jt1.query_id = ? AND jt1.id = attr1.gamma_id)";
   private static final String SELECT_ATTR_GAMMAS =
      "select gamma_id from osee_attribute t1, osee_join_id t2 where t1.attr_id = t2.id and t2.query_id = ?";

   private final Collection<Attribute<?>> attributesToPurge;
   private boolean success;

   public PurgeAttribute(Collection<Attribute<?>> attributesToPurge) {
      this.attributesToPurge = attributesToPurge;
   }

   @Override
   protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
      IdJoinQuery txsJoin = populateTxsJoinTable();

      try {
         ConnectionHandler.runPreparedUpdate(connection, DELETE_TXS, txsJoin.getQueryId());
         ConnectionHandler.runPreparedUpdate(connection, DELETE_ATTR, txsJoin.getQueryId());
         success = true;
      } finally {
         txsJoin.delete();
      }
   }

   private IdJoinQuery populateTxsJoinTable() throws OseeDataStoreException, OseeCoreException {
      IdJoinQuery attributeJoin = JoinUtility.createIdJoinQuery();

      for (Attribute<?> attribute : attributesToPurge) {
         attributeJoin.add(attribute.getId());
      }

      IdJoinQuery txsJoin = JoinUtility.createIdJoinQuery();
      try {
         attributeJoin.store();
         IOseeStatement chStmt = ConnectionHandler.getStatement();

         try {
            chStmt.runPreparedQuery(10000, SELECT_ATTR_GAMMAS, attributeJoin.getQueryId());
            while (chStmt.next()) {
               txsJoin.add(chStmt.getInt("gamma_id"));
            }
            txsJoin.store();
         } finally {
            chStmt.close();
         }
      } finally {
         attributeJoin.delete();
      }
      return txsJoin;
   }

   @Override
   protected void handleTxFinally() throws OseeCoreException {
      if (success) {
         Set<EventBasicGuidArtifact> artifactChanges = new HashSet<EventBasicGuidArtifact>();
         for (Attribute<?> attribute : attributesToPurge) {
            artifactChanges.add(new EventBasicGuidArtifact(EventModType.Purged, attribute.getArtifact()));
         }
         // Kick Local and Remote Events
         ArtifactEvent artifactEvent = new ArtifactEvent(attributesToPurge.iterator().next().getArtifact().getBranch());
         for (EventBasicGuidArtifact guidArt : artifactChanges) {
            artifactEvent.getArtifacts().add(guidArt);
         }
         OseeEventManager.kickPersistEvent(PurgeAttribute.class, artifactEvent);
      }
   }
}