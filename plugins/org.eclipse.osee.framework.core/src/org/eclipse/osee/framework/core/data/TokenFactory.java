/*
 * Created on Feb 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;

public final class TokenFactory {

   private TokenFactory() {
      // Utility Class
   }

   public static IArtifactType createArtifactType(String guid, String name) {
      return new ArtifactTypeToken(guid, name);
   }

   public static IAttributeType createAttributeType(String guid, String name) {
      return new AttributeTypeToken(guid, name);
   }

   public static IAttributeType createAttributeType(String guid, String name, String description) {
      return new AttributeTypeToken(guid, name, description);
   }

   public static IRelationType createRelationType(String guid, String name) {
      return new RelationTypeToken(guid, name);
   }

   public static IRelationTypeSide createRelationTypeSide(RelationSide relationSide, String guid, String name) {
      return new RelationTypeSideToken(guid, name, relationSide);
   }

   public static IAccessContextId createAccessContextId(String guid, String name) {
      return new AccessContextIdToken(guid, name);
   }

   public static IArtifactToken createArtifactToken(String guid, String name, IArtifactType artifactType) {
      return new ArtifactToken(guid, name, artifactType);
   }

   public static IUserToken createUserToken(String guid, String name, String email, String userId, boolean active, boolean admin, boolean creationRequired) {
      return new UserToken(guid, name, userId, active, admin, email, creationRequired);
   }

   public static IOseeBranch createBranch(String guid, String name) {
      return new BranchToken(guid, name);
   }

   private final static class ArtifactTypeToken extends NamedIdentity implements IArtifactType {
      public ArtifactTypeToken(String guid, String name) {
         super(guid, name);
      }
   }

   private static final class BranchToken extends NamedIdentity implements IOseeBranch {
      public BranchToken(String guid, String name) {
         super(guid, name);
      }
   }

   private final static class AttributeTypeToken extends NamedIdentity implements IAttributeType {
      public AttributeTypeToken(String guid, String name) {
         super(guid, name);
      }

      public AttributeTypeToken(String guid, String name, String description) {
         super(guid, name, description);
      }
   }

   private final static class RelationTypeToken extends NamedIdentity implements IRelationType {
      public RelationTypeToken(String guid, String name) {
         super(guid, name);
      }
   }

   private static class ArtifactToken extends NamedIdentity implements IArtifactToken {
      private final IArtifactType artifactType;

      public ArtifactToken(String guid, String name, IArtifactType artifactType) {
         super(guid, name);
         this.artifactType = artifactType;
      }

      @Override
      public IArtifactType getArtifactType() {
         return artifactType;
      }
   }

   private static class UserToken extends NamedIdentity implements IUserToken {

      private final String userId;
      private final boolean active;
      private final boolean admin;
      private final String email;
      private final boolean creationRequired;

      public UserToken(String guid, String name, String userId, boolean active, boolean admin, String email, boolean creationRequired) {
         super(guid, name);
         this.userId = userId;
         this.active = active;
         this.admin = admin;
         this.email = email;
         this.creationRequired = creationRequired;
      }

      @Override
      public IArtifactType getArtifactType() {
         return CoreArtifactTypes.User;
      }

      @Override
      public String getUserId() {
         return userId;
      }

      @Override
      public boolean isActive() {
         return active;
      }

      @Override
      public boolean isAdmin() {
         return admin;
      }

      @Override
      public String getEmail() {
         return email;
      }

      @Override
      public boolean isCreationRequired() {
         return creationRequired;
      }

   }

   private final static class AccessContextIdToken extends NamedIdentity implements IAccessContextId {
      public AccessContextIdToken(String guid, String name) {
         super(guid, name);
      }

      @Override
      public String toString() {
         return String.format("%s - %s", getName(), getGuid());
      }
   }

   private final static class RelationTypeSideToken extends NamedIdentity implements IRelationTypeSide {
      private final RelationSide relationSide;

      private RelationTypeSideToken(String guid, String name, RelationSide relationSide) {
         super(guid, name);
         this.relationSide = relationSide;
      }

      @Override
      public RelationSide getSide() {
         return relationSide;
      }
   }

}
