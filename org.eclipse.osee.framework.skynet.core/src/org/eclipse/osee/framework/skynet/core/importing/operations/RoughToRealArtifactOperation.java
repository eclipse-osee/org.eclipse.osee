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
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughRelation;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Roberto E. Escobar
 */
public class RoughToRealArtifactOperation extends AbstractOperation {
   private final SkynetTransaction transaction;
   private final RoughArtifactCollector rawData;
   private IArtifactImportResolver artifactResolver;

   public RoughToRealArtifactOperation(String operationName, RoughArtifactCollector rawData, SkynetTransaction transaction) {
      super(operationName, Activator.PLUGIN_ID);
      this.rawData = rawData;
      this.transaction = transaction;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.setTaskName("Creating Artifacts");
      int totalItems = rawData.getRoughArtifacts().size() + rawData.getRoughRelations().size();
      int unitOfWork = calculateWork(totalItems / getTotalWorkUnits());

      for (RoughArtifact roughArtifact : rawData.getRootRoughArtifact().getChildren()) {
         // the getReal call will recursively call get real on all descendants of roughArtifact
         Artifact child = roughArtifact.createArtifact(transaction, monitor, artifactResolver);
         if (child != null) {
            rawData.getDestinationArtifact().addChild(child);
         }
         monitor.worked(unitOfWork);
      }

      monitor.setTaskName("Creating Relations");
      for (RoughRelation roughRelation : rawData.getRoughRelations()) {
         roughRelation.createRelation(transaction, monitor);
         monitor.worked(unitOfWork);
      }
   }
}
