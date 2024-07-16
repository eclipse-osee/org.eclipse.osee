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

package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Objects;
import java.util.logging.Level;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
@JsonSerialize(using = ArtifactTokenSerializer.class)
@JsonDeserialize(using = ArtifactTokenDeserializer.class)
public interface ArtifactToken extends ArtifactId, HasBranch, NamedId, HasArtifactType, Identity<String> {
   public static final ArtifactToken SENTINEL = valueOf(ArtifactId.SENTINEL, BranchToken.SENTINEL);
   public static final String USE_LONG_IDS_KEY = "use.long.ids";
   public static boolean USE_LONG_IDS = Boolean.valueOf(System.getProperty(USE_LONG_IDS_KEY, "false"));

   @Override
   default String getGuid() {
      throw new UnsupportedOperationException("getGuid() is not supported");
   }

   default boolean isAttributeTypeValid(AttributeTypeId attributeType) {
      return getArtifactType().isValidAttributeType(attributeType);
   }

   public static ArtifactToken valueOf(ArtifactId id, String name) {
      return valueOf(id.getId(), GUID.create(), name, BranchToken.SENTINEL, ArtifactTypeToken.SENTINEL);
   }

   public static ArtifactToken valueOf(ArtifactId id, BranchToken branch) {
      if (id instanceof ArtifactToken) {
         return valueOf((ArtifactToken) id, branch);
      }
      return valueOf(id.getId(), GUID.create(), "", branch, ArtifactTypeToken.SENTINEL);
   }

   public static ArtifactToken valueOf(ArtifactToken token, BranchToken branch) {
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

   public static ArtifactToken valueOf(long id, String name, BranchToken branch) {
      return valueOf(id, GUID.create(), name, branch, ArtifactTypeToken.SENTINEL);
   }

   public static @NonNull ArtifactToken valueOf(long id, String name) {
      return valueOf(id, GUID.create(), name, BranchToken.SENTINEL, ArtifactTypeToken.SENTINEL);
   }

   public static ArtifactToken valueOf(long id, String name, ArtifactTypeToken artifactType) {
      return valueOf(id, GUID.create(), name, BranchToken.SENTINEL, artifactType);
   }

   public static ArtifactToken valueOf(long id, String name, BranchToken branch, ArtifactTypeToken artifactType) {
      return valueOf(id, GUID.create(), name, branch, artifactType);
   }

   public static @NonNull ArtifactToken valueOf(long id, String guid, String name, BranchId branch,
      ArtifactTypeToken artifactType) {
      return new ArtifactTokenImpl(id, guid, name, branch, artifactType);
   }

   public static @NonNull ArtifactToken valueOf(@NonNull ArtifactReadable artifactReadable) {
      //@formatter:off
      assert
           artifactReadable != null
         : Conditions.assertMessageNonNull( "artifactReadable" );

      var branch = artifactReadable.getBranch();
      var viewId = branch.getViewId();
      viewId = ( Objects.nonNull(viewId) && viewId.isValid() )
                  ? ArtifactId.valueOf( viewId.getId() )
                  : ArtifactId.SENTINEL;
      var branchId = BranchId.create(branch.getId(),viewId);

      return
         new ArtifactTokenImpl
                (
                   artifactReadable.getId(),
                   artifactReadable.getGuid(),
                   artifactReadable.getName(),
                   branchId,
                   artifactReadable.getArtifactType()
                );
      //@formatter:on
   }

   public static class ArtifactTokenImpl extends NamedIdBase implements ArtifactToken {
      private final BranchId branch;
      private final ArtifactTypeToken artifactType;
      private final String guid;

      public ArtifactTokenImpl(Long id, String guid, String name, BranchId branch, ArtifactTypeToken artifactType) {
         super(id, name);
         if (!USE_LONG_IDS) {
            // Until artifacts can have long in db, ensure that tokens aren't negative when turned to int
            int idInt = Long.valueOf(id).intValue();
            if (idInt <= 0 && idInt != -1) {
               String msg = String.format(
                  "Token id (as int) must be > 0 or SENTINAL, not int [%s] for long id [%s] name [%s] and type [%s])",
                  idInt, id, name, artifactType.getName());
               // Log to console which shows the id, name and type on construction where exception doesn't show till loading
               OseeLog.log(ArtifactToken.class, Level.SEVERE, msg);
               throw new OseeArgumentException(msg);
            }
         }
         this.branch = branch;
         this.artifactType = artifactType;
         this.guid = guid;
      }

      public ArtifactTokenImpl(Long id, String name, BranchToken common, ArtifactTypeToken artifactType) {
         this(id, GUID.create(), name, common, artifactType);
      }

      @Override
      public ArtifactTypeToken getArtifactType() {
         return artifactType;
      }

      @Override
      public BranchToken getBranch() {
         return BranchToken.valueOf(branch);
      }

      @Override
      public String getGuid() {
         return guid;
      }

      @Override
      public boolean equals(Object obj) {
         boolean equal = super.equals(obj);
         if (equal && obj instanceof HasBranch) {
            return isOnSameBranch((HasBranch) obj);
         }
         return equal;
      }

      @Override
      public ArtifactToken getToken() {
         return ArtifactToken.valueOf(getId(), getName(), getBranch());
      }

   }

   public default ArtifactToken getToken() {
      return ArtifactToken.valueOf(getId(), getName(), getBranch());
   }

}