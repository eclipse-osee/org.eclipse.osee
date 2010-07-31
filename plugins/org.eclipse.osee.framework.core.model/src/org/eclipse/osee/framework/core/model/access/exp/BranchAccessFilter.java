/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.access.exp;

import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class BranchAccessFilter implements IAccessFilter {
   private final PermissionEnum branchPermission;
   private final IBasicArtifact<?> artifact;

   public BranchAccessFilter(IBasicArtifact<?> artifact, PermissionEnum branchPermission) {
      this.artifact = artifact;
      this.branchPermission = branchPermission;
   }

   @Override
   public int getPriority() {
      return 40;
   }

   @Override
   public PermissionEnum filter(IBasicArtifact<?> artifact, Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain) {
      PermissionEnum toReturn = agrPermission;

      if (this.artifact.equals(artifact)) {
         agrPermission = branchPermission;
      }

      return toReturn;
   }

   @Override
   public boolean acceptToObject(Object object) {
      return true;
   }
}
