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
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;

/**
 * @author Roberto E. Escobar
 */
public final class PurgeArtifactTypeDatabaseTxCallable extends AbstractDatastoreTxCallable<Void> {
   @SuppressWarnings("unused")
   private final Collection<? extends IArtifactType> typesToPurge;
   @SuppressWarnings("unused")
   private final IdentityLocator identityService;

   public PurgeArtifactTypeDatabaseTxCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, IdentityLocator identityService, Collection<? extends IArtifactType> typesToPurge) {
      super(logger, session, databaseService, "Purge Artifact Type");
      this.identityService = identityService;
      this.typesToPurge = typesToPurge;
   }

   @Override
   protected Void handleTxWork(OseeConnection connection) {
      throw new UnsupportedOperationException("operation is not currently supported");
   }
}