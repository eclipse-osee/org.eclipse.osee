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
package org.eclipse.osee.define.blam.operation;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;

/**
 * @author Jeff C. Phillips
 */
public class AddEveryoneGroupToBranches extends AbstractBlam {

   AccessControlManager accessControlManager = AccessControlManager.getInstance();
   BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
   ArtifactPersistenceManager artifactPersistenceManager = ArtifactPersistenceManager.getInstance();

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Collection<Artifact> artifacts;

      for (Branch brnch : branchPersistenceManager.getBranches()) {

         if (!accessControlManager.getAccessControlList(brnch).isEmpty()) {
            artifacts =
                  artifactPersistenceManager.getArtifactsFromAttribute("Name", "Everyone",
                        branchPersistenceManager.getCommonBranch());

            if (!artifacts.isEmpty()) {
               accessControlManager.setPermission(artifacts.iterator().next(), brnch, PermissionEnum.READ);
            }
         }
      }

   }
}