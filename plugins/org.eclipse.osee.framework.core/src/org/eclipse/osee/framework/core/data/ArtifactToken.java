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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.NamedIdSerializer;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
@JsonSerialize(using = NamedIdSerializer.class)
public interface ArtifactToken extends ArtifactId, HasBranch, NamedId, Identity<String> {
   public static final ArtifactToken SENTINEL = valueOf(ArtifactId.SENTINEL, BranchId.SENTINEL);

   @Override
   default String getGuid() {
      throw new UnsupportedOperationException("getGuid() is not supported");
   }

   ArtifactTypeToken getArtifactType();

   default boolean isTypeEqual(ArtifactTypeId artifactType) {
      return artifactType.equals(getArtifactType());
   }

   public static ArtifactToken valueOf(Id id, BranchId branch) {
      return valueOf(id.getId(), GUID.create(), null, branch, ArtifactTypeToken.SENTINEL);
   }

   public static ArtifactToken valueOf(ArtifactId id, String name) {
      return valueOf(id.getId(), GUID.create(), name, BranchId.SENTINEL, ArtifactTypeToken.SENTINEL);
   }

   public static ArtifactToken valueOf(ArtifactId id, BranchId branch) {
      if (id instanceof ArtifactToken) {
         return valueOf((ArtifactToken) id, branch);
      }
      return valueOf(id.getId(), GUID.create(), "", branch, ArtifactTypeToken.SENTINEL);
   }

   public static ArtifactToken valueOf(ArtifactToken token, BranchId branch) {
      String useGuid = null;
      try {
         useGuid = token.getGuid();
      } catch (UnsupportedOperationException ex) {
         // do nothing
      }
      return valueOf(token.getId(), useGuid, token.getName(), branch, token.getArtifactType());
   }

   public static ArtifactToken valueOf(long id, BranchId branch) {
      return valueOf(id, GUID.create(), "", branch, ArtifactTypeToken.SENTINEL);
   }

   public static ArtifactToken valueOf(long id, String name, BranchId branch) {
      return valueOf(id, GUID.create(), name, branch, ArtifactTypeToken.SENTINEL);
   }

   public static ArtifactToken valueOf(long id, String name) {
      return valueOf(id, GUID.create(), name, BranchId.SENTINEL, ArtifactTypeToken.SENTINEL);
   }

   public static ArtifactToken valueOf(long id, String name, ArtifactTypeToken artifactType) {
      return valueOf(id, GUID.create(), name, BranchId.SENTINEL, artifactType);
   }

   public static ArtifactToken valueOf(long id, String name, BranchId branch, ArtifactTypeToken artifactType) {
      return valueOf(id, GUID.create(), name, branch, artifactType);
   }

   public static ArtifactToken valueOf(long id, String guid, String name, BranchId branch, ArtifactTypeToken artifactType) {
      return new ArtifactTokenImpl(id, guid, name, branch, artifactType);
   }

   public static class ArtifactTokenImpl extends NamedIdBase implements ArtifactToken {
      private final BranchId branch;
      private final ArtifactTypeToken artifactType;
      private final String guid;

      public ArtifactTokenImpl(Long id, String guid, String name, BranchId branch, ArtifactTypeToken artifactType) {
         super(id, name);
         this.branch = branch;
         this.artifactType = artifactType;
         this.guid = guid;
      }

      @Override
      public ArtifactTypeToken getArtifactType() {
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

      @Override
      public boolean equals(Object obj) {
         boolean equal = super.equals(obj);
         if (equal && obj instanceof HasBranch) {
            return isOnSameBranch((HasBranch) obj);
         }
         return equal;
      }
   }
}