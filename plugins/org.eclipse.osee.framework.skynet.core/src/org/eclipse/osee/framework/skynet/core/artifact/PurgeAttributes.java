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

/**
 * @author Jeff C. Phillips
 */
public class PurgeAttributes extends AbstractDbTxOperation {

   private static final String DELETE_TXS =
      "DELETE FROM osee_txs WHERE EXISTS (select 1 from osee_join_id WHERE query_id = ? AND id = gamma_id)";

   private static final String DELETE_ATTR =
      "DELETE FROM osee_attribute WHERE EXISTS (select 1 from osee_join_id WHERE query_id = ? AND id = gamma_id)";

   private static final String SELECT_ATTR_GAMMAS =
      "SELECT gamma_id FROM osee_attribute, osee_join_id where attr_id = id and query_id = ?";

   private final Collection<Attribute<?>> attributesToPurge;
   private boolean success;

   public PurgeAttributes(Collection<Attribute<?>> attributesToPurge) throws OseeCoreException {
      super(ConnectionHandler.getJdbcClient(), "Purge Attributes", Activator.PLUGIN_ID);
      this.attributesToPurge = attributesToPurge;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, JdbcConnection connection) throws OseeCoreException {
      try (IdJoinQuery idJoin = populateIdJoin(connection)) {
         getJdbcClient().runPreparedUpdate(connection, DELETE_TXS, idJoin.getQueryId());
         getJdbcClient().runPreparedUpdate(connection, DELETE_ATTR, idJoin.getQueryId());
         attributesToPurge.forEach(Attribute::purge);
         success = true;
      }
   }

   private IdJoinQuery populateIdJoin(JdbcConnection connection) throws OseeDataStoreException, OseeCoreException {
      IdJoinQuery idJoin = JoinUtility.createIdJoinQuery(getJdbcClient(), connection);

      try (IdJoinQuery attributeJoin = JoinUtility.createIdJoinQuery(getJdbcClient(), connection)) {
         attributeJoin.addAndStore(attributesToPurge);
         getJdbcClient().runQuery(connection, stmt -> idJoin.add(stmt.getLong("gamma_id")),
            JdbcConstants.JDBC__MAX_FETCH_SIZE, SELECT_ATTR_GAMMAS, attributeJoin.getQueryId());
         idJoin.store();
      }
      return idJoin;
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      super.handleTxFinally(monitor);
      if (success) {
         Set<EventBasicGuidArtifact> artifactChanges = new HashSet<>();
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