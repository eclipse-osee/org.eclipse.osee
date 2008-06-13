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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.tagging.TagManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class TagChangedArtifacts extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Branch> branches = BranchPersistenceManager.getInstance().getChangeManagedBranches();
      monitor.beginTask("Tag change managed branches", branches.size());

      for (Branch branch : branches) {
         int count = 0;
         Collection<Change> changes = RevisionManager.getInstance().getChangesPerBranch(branch);
         int total = changes.size();

         for (Change change : changes) {
            if (monitor.isCanceled()) {
               return;
            }

            monitor.subTask("Tagging change on branch " + branch.getBranchShortestName() + " for artifact " + ++count + " of " + total);
            Artifact artifact = change.getArtifact();
            if (!artifact.isDeleted()) {
               TagManager.autoTag(true, artifact);
            }
         }
         monitor.worked(1);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return emptyXWidgetsXml;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }
}