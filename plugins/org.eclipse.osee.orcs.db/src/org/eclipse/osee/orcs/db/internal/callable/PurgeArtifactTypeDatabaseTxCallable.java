/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.callable;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public final class PurgeArtifactTypeDatabaseTxCallable extends AbstractDatastoreTxCallable<Void> {
   public PurgeArtifactTypeDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, Collection<? extends ArtifactTypeToken> typesToPurge) {
      super(logger, session, jdbcClient);
   }

   @Override
   protected Void handleTxWork(JdbcConnection connection) {
      throw new UnsupportedOperationException("operation is not currently supported");
   }
}