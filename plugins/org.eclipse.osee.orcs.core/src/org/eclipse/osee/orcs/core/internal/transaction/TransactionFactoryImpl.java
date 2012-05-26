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
package org.eclipse.osee.orcs.core.internal.transaction;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class TransactionFactoryImpl implements TransactionFactory {

   private final Log logger;
   private final SessionContext sessionContext;
   private final BranchDataStore branchDataStore;
   private final ArtifactFactory artifactFactory;

   public TransactionFactoryImpl(Log logger, SessionContext sessionContext, BranchDataStore branchDataStore, ArtifactFactory artifactFactory) {
      this.logger = logger;
      this.sessionContext = sessionContext;
      this.branchDataStore = branchDataStore;
      this.artifactFactory = artifactFactory;
   }

   @Override
   public OrcsTransaction createTransaction(IOseeBranch branch, ReadableArtifact userArtifact, String comment) throws OseeCoreException {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(userArtifact, "author");
      Conditions.checkNotNullOrEmpty(comment, "comment");

      OrcsTransactionImpl orcsTxn =
         new OrcsTransactionImpl(logger, sessionContext, branchDataStore, artifactFactory, branch);
      orcsTxn.setComment(comment);
      orcsTxn.setAuthor(userArtifact);
      return orcsTxn;
   }
}
