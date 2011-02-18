/*
 * Created on Feb 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

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

   public static AccessContextId createAccessContextId(String guid, String name) {
      return new AccessContextIdToken(guid, name);
   }

   public static IArtifactToken createArtifactToken(String guid, String name, IArtifactType artifactType) {
      return new ArtifactToken(guid, name, artifactType);
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

   private final static class AccessContextIdToken extends NamedIdentity implements AccessContextId {
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
