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

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class AttributeTypeAccessFilter implements IAccessFilter {
   private final PermissionEnum permission;
   private final IBasicArtifact<?> artifact;
   private final IAttributeType type;

   public AttributeTypeAccessFilter(PermissionEnum permission, IBasicArtifact<?> artifact, IAttributeType type) {
      super();
      this.permission = permission;
      this.artifact = artifact;
      this.type = type;
   }

   @Override
   public int getPriority() {
      return 10;
   }

   @Override
   public boolean acceptToObject(Object object) {
      //Return false if the object to be checked is a branch or artifact.
      return !(object instanceof Branch) && !(object instanceof IBasicArtifact<?>);
   }

   @Override
   public PermissionEnum filter(IBasicArtifact<?> artifact, Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain) {
      PermissionEnum toReturn = agrPermission;

      if (this.artifact.equals(artifact) && type.equals(object)) {
         if (agrPermission != PermissionEnum.DENY && permission != null) {
            agrPermission = permission;
            toReturn = agrPermission;
         }
      }
      return toReturn;
   }

}
