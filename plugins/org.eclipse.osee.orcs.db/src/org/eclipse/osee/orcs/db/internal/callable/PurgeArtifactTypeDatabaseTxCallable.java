/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public final class PurgeArtifactTypeDatabaseTxCallable extends AbstractDatastoreTxCallable<Void> {
   public PurgeArtifactTypeDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, Collection<? extends IArtifactType> typesToPurge) {
      super(logger, session, jdbcClient);
   }

   @Override
   protected Void handleTxWork(JdbcConnection connection) {
      throw new UnsupportedOperationException("operation is not currently supported");
   }
}