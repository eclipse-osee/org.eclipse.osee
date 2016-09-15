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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public interface ArtifactToken extends ArtifactId, HasArtifactType, HasBranch, Named {
   default ArtifactTypeId getArtifactTypeId() {
      return null;
   }

   public static ArtifactToken valueOf(long id, String name, BranchId branch, IArtifactType artifactType) {
      return valueOf(id, null, name, branch, artifactType);
   }

   public static ArtifactToken valueOf(long id, String guid, String name, BranchId branch, IArtifactType artifactType) {
      final class ArtifactTokenImpl extends NamedId implements ArtifactToken {
         private final BranchId branch;
         private final IArtifactType artifactType;
         private final String guid;

         public ArtifactTokenImpl(Long id, String guid, String name, BranchId branch, IArtifactType artifactType) {
            super(id, name);
            this.branch = branch;
            this.artifactType = artifactType;
            this.guid = guid;
         }

         @Override
         public IArtifactType getArtifactType() {
            return artifactType;
         }

         @Override
         public BranchId getBranch() {
            return branch;
         }

         @Override
         public String getGuid() {
            return guid;
         }

         @Override
         public Long getUuid() {
            return getId();
         }
      }
      return new ArtifactTokenImpl(id, guid, name, branch, artifactType);
   }

   default String toStringWithId() {
      return String.format("[%s][%s]", getName(), getId());
   }
}