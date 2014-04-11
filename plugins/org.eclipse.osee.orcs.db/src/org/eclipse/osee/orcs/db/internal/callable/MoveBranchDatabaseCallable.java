/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsConstants;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Ryan D. Brooks
 */
public class MoveBranchDatabaseCallable extends AbstractDatastoreTxCallable<IStatus> {

   private static final String INSERT_ADDRESSING =
      "insert into %s (transaction_id, gamma_id, tx_current, mod_type, branch_id) select transaction_id, gamma_id, tx_current, mod_type, branch_id from %s where branch_id = ?";

   public static final String DELETE_ADDRESSING = "delete from %s where branch_id = ?";
   private final boolean archive;
   private final Branch branch;

   private final EventService eventService;

   public MoveBranchDatabaseCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, EventService eventService, boolean archive, Branch branch) {
      super(logger, session, databaseService, "Branch Move");
      this.eventService = eventService;
      this.archive = archive;
      this.branch = branch;
   }

   private EventService getEventService() {
      return eventService;
   }

   @Override
   protected IStatus handleTxWork(OseeConnection connection) throws OseeCoreException {
      String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
      String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";
      checkForCancelled();

      String sql = String.format(INSERT_ADDRESSING, destinationTableName, sourceTableName);
      getDatabaseService().runPreparedUpdate(connection, sql, branch.getUuid());
      checkForCancelled();

      sql = String.format(DELETE_ADDRESSING, sourceTableName);
      getDatabaseService().runPreparedUpdate(connection, sql, branch.getUuid());
      checkForCancelled();

      Map<String, Object> eventData = new HashMap<String, Object>();
      eventData.put(OrcsConstants.ORCS_BRANCH_EVENT_DATA, Collections.singleton(branch));
      getEventService().postEvent(OrcsConstants.ORCS_BRANCH_MOVE_EVENT, eventData);
      return Status.OK_STATUS;
   }
}