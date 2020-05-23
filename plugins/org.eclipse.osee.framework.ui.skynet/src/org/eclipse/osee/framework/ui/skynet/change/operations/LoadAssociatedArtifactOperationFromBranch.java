/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

public class LoadAssociatedArtifactOperationFromBranch extends AbstractOperation {

   private final IBranchProvider branchProvider;

   public LoadAssociatedArtifactOperationFromBranch(IBranchProvider branchProvider) {
      super("Load Associated Artifact", Activator.PLUGIN_ID);
      this.branchProvider = branchProvider;

   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {

      monitor.worked(calculateWork(0.80));
      if (branchProvider.getBranch().isValid()) {
         BranchManager.getAssociatedArtifact(branchProvider.getBranch());
      }

      monitor.worked(calculateWork(0.20));
   }
}