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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Jeff C. Phillips
 */
public class AddEveryoneGroupToBranches extends AbstractBlam {

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      for (Branch brnch : BranchManager.getNormalBranches()) {

         if (!AccessControlManager.getAccessControlList(brnch).isEmpty()) {
            Artifact everyone =
                  ArtifactQuery.getArtifactFromAttribute("Name", "Everyone", BranchManager.getCommonBranch());
            AccessControlManager.setPermission(everyone, brnch, PermissionEnum.READ);
         }
      }
   }

   @Override
   public String getName() {
      return "Add Everone Group to Branches";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Define");
   }
}