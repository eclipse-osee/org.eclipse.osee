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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PrintBranchInfo implements BlamOperation {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(BlamVariableMap variableMap, Branch branch, IProgressMonitor monitor) throws Exception {
      ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
      BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

      for (Branch branch1 : branchManager.getBranches()) {
         System.out.print(branch1.getBranchName());
         System.out.print(", ");
         System.out.print(branch1.getBranchId());
         System.out.print(", ");
         System.out.println(artifactManager.getArtifactFromId(branch1.getAuthorId(), branchManager.getCommonBranch()).getDescriptiveName());
      }
   }
}
