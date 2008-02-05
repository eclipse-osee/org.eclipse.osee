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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.artifact.snapshot.ArtifactSnapshotManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactSnapshotAllItems implements BlamOperation {

   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         Branch branch = variableMap.getBranch("Branch");

         monitor.setTaskName("Loading Artifact Type Descriptors");
         Collection<ArtifactSubtypeDescriptor> descriptors =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptors(branch);

         monitor.beginTask("Regenerating Artifact Preview Snapshots", descriptors.size());

         ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
         ArtifactSnapshotManager artifactSnapshotManager = ArtifactSnapshotManager.getInstance();
         for (ArtifactSubtypeDescriptor descriptor : descriptors) {
            monitor.subTask(String.format("Loading artifacts of type [%s]", descriptor.getName()));
            Collection<Artifact> artifacts = artifactManager.getArtifactsFromSubtype(descriptor);
            int count = 0;
            int total = artifacts.size();
            for (Artifact artifact : artifacts) {
               monitor.subTask(String.format("Generating Snapshot for: [%s] [%s of %s]", artifact.getDescriptiveName(),
                     ++count, total));
               artifactSnapshotManager.getDataSnapshot(artifact, true);

               if (monitor.isCanceled()) {
                  return;
               }
            }
            monitor.worked(1);
         }
      } finally {
         monitor.done();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return branchXWidgetXml;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }
}
