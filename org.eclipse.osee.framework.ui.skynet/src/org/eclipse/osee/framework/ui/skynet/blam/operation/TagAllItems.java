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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactHasStaleTags;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.tagging.TagManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Robert A. Fisher
 */
public class TagAllItems implements BlamOperation {

   public void runOperation(BlamVariableMap variableMap, Branch blamOperationBranch, IProgressMonitor monitor) throws Exception {
      try {
         Branch branch = (Branch) variableMap.getValue("Branch");

         monitor.setTaskName("Loading Artifact Type Descriptors");
         Collection<ArtifactSubtypeDescriptor> descriptors =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptors(branch);

         monitor.beginTask("Tagging Artifacts", descriptors.size());

         ArrayList<ISearchPrimitive> criteria = new ArrayList<ISearchPrimitive>(2);
         criteria.add(new ArtifactHasStaleTags());

         Collection<Artifact> staleArtifacts;
         ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
         TagManager tagManager = TagManager.getInstance();
         int count, total;

         for (ArtifactSubtypeDescriptor descriptor : descriptors) {
            monitor.subTask("Loading stale " + descriptor.getName() + " artifacts");
            criteria.set(1, new ArtifactTypeSearch(descriptor.getName(), Operator.EQUAL));
            staleArtifacts = artifactManager.getArtifacts(criteria, true, branch);

            count = 0;
            total = staleArtifacts.size();
            for (Artifact artifact : staleArtifacts) {
               monitor.subTask("Tagging " + descriptor.getName() + " artifact " + ++count + " of " + total);
               tagManager.autoTag(true, artifact);
               monitor.worked(1);
               if (monitor.isCanceled()) return;
            }
         }
      } finally {
         monitor.done();
      }
   }
}
