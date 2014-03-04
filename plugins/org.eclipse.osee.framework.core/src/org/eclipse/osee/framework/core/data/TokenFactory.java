/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.Random;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.FullyNamedIdentity;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;
import org.eclipse.osee.framework.jdk.core.util.GUID;

public final class TokenFactory {

   private static Random random;

   private TokenFactory() {
      // Utility Class
   }

   public static IArtifactType createArtifactType(long guid, String name) {
      return new ArtifactTypeToken(guid, name);
   }

   public static IAttributeType createAttributeType(long guid, String name) {
      return new AttributeTypeToken(guid, name);
   }

   public static IAttributeType createAttributeType(long guid, String name, String description) {
      return new AttributeTypeToken(guid, name, description);
   }

   public static IRelationType createRelationType(long guid, String name) {
      return new RelationTypeToken(guid, name);
   }

   public static IRelationTypeSide createRelationTypeSide(RelationSide relationSide, long guid, String name) {
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

   public static IOseeBranch createBranch(String name) {
      return new BranchToken(GUID.create(), getNextLong(), name);
   }

   public static IOseeBranch createBranch(String guid, String name) {
      return new BranchToken(guid, getNextLong(), name);
   }

   public static IOseeBranch createBranch(String guid, long uuid, String name) {
      return new BranchToken(guid, uuid, name);
   }

   public static IRelationSorterId createSorterId(String guid, String name) {
      return new SorterIdToken(guid, name);
   }

   private final static class SorterIdToken extends NamedIdentity<String> implements IRelationSorterId {

      public SorterIdToken(String guid, String name) {
         super(guid, name);
      }

      @Override
      public String toString() {
         return String.format("[%s:%s]", getName(), getGuid());
      }
   }

   private final static class ArtifactTypeToken extends NamedIdentity<Long> implements IArtifactType {
      public ArtifactTypeToken(Long guid, String name) {
         super(guid, name);
      }
   }

   public static ITransaction createTransaction(int txId) {
      return new TransactionToken(txId);
   }

   private static final class TransactionToken extends BaseIdentity<Integer> implements ITransaction {
      public TransactionToken(Integer txId) {
         super(txId);
      }
   }

   private static final class BranchToken extends NamedIdentity<String> implements IOseeBranch {
      private final long uuid;

      public BranchToken(String guid, long uuid, String name) {
         super(guid, name);
         this.uuid = uuid;
      }

      @Override
      public long getUuid() {
         return uuid;
      }
   }

   private static long getNextLong() {
      if (random == null) {
         random = new Random();
      }
      return random.nextLong();
   }

   private final static class AttributeTypeToken extends FullyNamedIdentity<Long> implements IAttributeType {
      public AttributeTypeToken(Long guid, String name) {
         super(guid, name);
      }

      public AttributeTypeToken(Long guid, String name, String description) {
         super(guid, name, description);
      }
   }

   private final static class RelationTypeToken extends FullyNamedIdentity<Long> implements IRelationType {
      public RelationTypeToken(Long guid, String name) {
         super(guid, name);
      }
   }

   private static class ArtifactToken extends NamedIdentity<String> implements IArtifactToken {
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

   private static class UserToken extends NamedIdentity<String> implements IUserToken {

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

      @Override
      public String toString() {
         return String.format("UserToken [userId=[%s], active=[%s], admin=[%s], email=[%s], creationRequired=[%s]",
            userId, active, admin, email, creationRequired);
      }

   }

   private final static class AccessContextIdToken extends NamedIdentity<String> implements IAccessContextId {
      public AccessContextIdToken(String guid, String name) {
         super(guid, name);
      }

      @Override
      public String toString() {
         return String.format("%s - %s", getName(), getGuid());
      }
   }

   private final static class RelationTypeSideToken extends FullyNamedIdentity<Long> implements IRelationTypeSide {

      private final RelationSide relationSide;
      private RelationTypeSideToken opposite;

      private RelationTypeSideToken(Long guid, String name, RelationSide relationSide) {
         super(guid, name);
         this.relationSide = relationSide;
      }

      @Override
      public RelationSide getSide() {
         return relationSide;
      }

      @Override
      public boolean isOfType(IRelationType type) {
         return type.getGuid() == getGuid();
      }

      @Override
      public int hashCode() {
         // Do not add relation side to hash code because it will violate the hash code contract
         return super.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof IRelationTypeSide) {
            IRelationTypeSide otherSide = (IRelationTypeSide) obj;
            if (relationSide != otherSide.getSide()) {
               return false;
            }
         }
         if (obj instanceof IRelationType) {
            return super.equals(obj);
         }
         return false;
      }

      @Override
      public synchronized IRelationTypeSide getOpposite() {
         if (opposite == null) {
            opposite = new RelationTypeSideToken(getGuid(), getName(), getSide().oppositeSide());
         }
         return opposite;
      }

      @Override
      public String toString() {
         return String.format("RelationTypeSide - uuid=[%s] type=[%s] side=[%s]", getGuid(), getName(), getSide());
      }
   }

}
