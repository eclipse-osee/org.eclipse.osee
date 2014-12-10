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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.utility.AbstractDbTxOperation;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.IdJoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Jeff C. Phillips
 */
public class PurgeAttributes extends AbstractDbTxOperation {

   private static final String DELETE_TXS =
      "DELETE FROM osee_txs txs1 WHERE EXISTS (select 1 from osee_join_id jt1 WHERE jt1.query_id = ? AND jt1.id = txs1.gamma_id)";

   private static final String DELETE_ATTR =
      "DELETE FROM osee_attribute attr1 WHERE EXISTS (select 1 from osee_join_id jt1 WHERE jt1.query_id = ? AND jt1.id = attr1.gamma_id)";

   private static final String SELECT_ATTR_GAMMAS =
      "SELECT gamma_id FROM osee_attribute t1, osee_join_id t2 where t1.attr_id = t2.id and t2.query_id = ?";

   private final Collection<Attribute<?>> attributesToPurge;
   private boolean success;

   public PurgeAttributes(Collection<Attribute<?>> attributesToPurge) throws OseeCoreException {
      super(ConnectionHandler.getJdbcClient(), "Purge Attributes", Activator.PLUGIN_ID);
      this.attributesToPurge = attributesToPurge;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, JdbcConnection connection) throws OseeCoreException {
      IdJoinQuery txsJoin = populateTxsJoinTable();
      try {
         getJdbcClient().runPreparedUpdate(connection, DELETE_TXS, txsJoin.getQueryId());
         getJdbcClient().runPreparedUpdate(connection, DELETE_ATTR, txsJoin.getQueryId());

         for (Attribute<?> attribute : attributesToPurge) {
            attribute.purge();
         }
         success = true;
      } finally {
         txsJoin.delete();
      }

   }

   private IdJoinQuery populateTxsJoinTable() throws OseeDataStoreException, OseeCoreException {
      IdJoinQuery attributeJoin = JoinUtility.createIdJoinQuery(getJdbcClient());

      for (Attribute<?> attribute : attributesToPurge) {
         attributeJoin.add(attribute.getId());
      }

      IdJoinQuery txsJoin = JoinUtility.createIdJoinQuery(getJdbcClient());
      try {
         attributeJoin.store();
         JdbcStatement chStmt = getJdbcClient().getStatement();

         try {
            chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, SELECT_ATTR_GAMMAS, attributeJoin.getQueryId());
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
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      super.handleTxFinally(monitor);
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
         OseeEventManager.kickPersistEvent(this, artifactEvent);
      }
   }

}