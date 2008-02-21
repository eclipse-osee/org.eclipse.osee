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
import org.eclipse.osee.framework.skynet.core.tagging.TagManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class TagAllItems extends AbstractBlam {

   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         Branch branch = variableMap.getBranch("Branch");

         monitor.setTaskName("Loading Artifact Type Descriptors");
         Collection<ArtifactSubtypeDescriptor> descriptors =
               ConfigurationPersistenceManager.getInstance().getValidArtifactTypes(branch);

         monitor.beginTask("Tagging Artifacts", descriptors.size());

         ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
         TagManager tagManager = TagManager.getInstance();

         for (ArtifactSubtypeDescriptor descriptor : descriptors) {
            monitor.subTask("Loading artifact of type " + descriptor.getName());
            Collection<Artifact> artifacts = artifactManager.getArtifactsFromSubtype(branch, descriptor);

            int count = 0;
            int total = artifacts.size();
            for (Artifact artifact : artifacts) {
               monitor.subTask("Tagging " + descriptor.getName() + " artifact " + ++count + " of " + total);
               tagManager.autoTag(true, artifact);
               monitor.worked(1);
               if (monitor.isCanceled()) {
                  return;
               }
            }
         }
      } finally {
         monitor.done();
      }
   }
}