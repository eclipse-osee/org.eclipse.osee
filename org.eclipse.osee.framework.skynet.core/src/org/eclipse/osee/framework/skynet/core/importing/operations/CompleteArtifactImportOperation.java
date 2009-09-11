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
package org.eclipse.osee.framework.skynet.core.importing.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Roberto E. Escobar
 */
public final class CompleteArtifactImportOperation extends AbstractOperation {
   private final Artifact destinationArtifact;
   private final SkynetTransaction transaction;

   public CompleteArtifactImportOperation(SkynetTransaction transaction, Artifact destinationArtifact) {
      super("Commit Import", Activator.PLUGIN_ID);
      this.destinationArtifact = destinationArtifact;
      this.transaction = transaction;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      destinationArtifact.persist(transaction);
      transaction.execute();
      monitor.worked(calculateWork(1.0));
   }
}
