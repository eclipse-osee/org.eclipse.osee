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

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class ArtifactAccessFilter implements IAccessFilter {

   private final PermissionEnum artifactPermission;
   private final IBasicArtifact<?> artifact;

   public ArtifactAccessFilter(IBasicArtifact<?> artifact, PermissionEnum artifactPermission) {
      super();
      this.artifactPermission = artifactPermission;
      this.artifact = artifact;
   }

   @Override
   public int getPriority() {
      return 30;
   }

   @Override
   public PermissionEnum filter(IBasicArtifact<?> artifact, Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain) {
      PermissionEnum toReturn = null;

      if (this.artifact.equals(artifact)) {
         if (agrPermission != PermissionEnum.DENY && artifactPermission != null) {
            agrPermission = artifactPermission;
         }
         toReturn = agrPermission;
      }
      return toReturn;
   }

   @Override
   public boolean acceptToObject(Object object) {
      //Return false if the object to be checked is a branch.
      return !(object instanceof IOseeBranch);
   }
}
