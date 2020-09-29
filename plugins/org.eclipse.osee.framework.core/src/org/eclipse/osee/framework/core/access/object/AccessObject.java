/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.core.access.object;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Jeff C. Phillips
 */
public abstract class AccessObject implements HasBranch {

   private final NamedId id;

   public abstract void removeFromCache();

   public AccessObject(NamedId id) {
      this.id = id;
   }

   public boolean isArtifact() {
      return id instanceof ArtifactId;
   }

   public boolean isBranch() {
      return id instanceof BranchId;
   }

   public abstract Long getId();

   public String toStringWithId() {
      return id.toStringWithId();
   }

   public static AccessObject valueOf(Object object) {
      if (object instanceof AccessObject) {
         return (AccessObject) object;
      } else if (object instanceof ArtifactToken) {
         return ArtifactAccessObject.valueOf((ArtifactToken) object);
      } else if (object instanceof BranchToken) {
         return BranchAccessObject.valueOf((BranchToken) object);
      } else {
         throw new OseeArgumentException("object must be ArtifactToken or BranchToken");
      }
   }

}
