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
package org.eclipse.osee.framework.skynet.core.relation;

import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.BaseOseeType;

/**
 * @author Robert A. Fisher
 */
public class RelationType extends BaseOseeType implements Comparable<RelationType> {
   private String sideAName;
   private String sideBName;
   private RelationTypeMultiplicity multiplicity;
   private ArtifactType artifactTypeSideA;
   private ArtifactType artifactTypeSideB;
   private boolean isOrdered;
   private String defaultOrderTypeGuid;
   private final RelationTypeDirtyDetails dirtyDetails;

   public RelationType(String guid, String relationTypeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isOrdered, String defaultOrderTypeGuid) {
      super(guid, relationTypeName);
      this.dirtyDetails = new RelationTypeDirtyDetails();
      setFields(relationTypeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity, isOrdered,
            defaultOrderTypeGuid);
   }

   public RelationTypeDirtyDetails getDirtyDetails() {
      return dirtyDetails;
   }

   public void setFields(String relationTypeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isOrdered, String defaultOrderTypeGuid) {
      getDirtyDetails().update(relationTypeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB,
            multiplicity, isOrdered, defaultOrderTypeGuid);
      this.sideAName = sideAName;
      this.sideBName = sideBName;
      this.artifactTypeSideA = artifactTypeSideA;
      this.artifactTypeSideB = artifactTypeSideB;
      this.multiplicity = multiplicity;
      this.isOrdered = isOrdered;
      this.defaultOrderTypeGuid = defaultOrderTypeGuid;
   }

   public RelationTypeMultiplicity getMultiplicity() {
      return multiplicity;
   }

   public ArtifactType getArtifactTypeSideA() {
      return artifactTypeSideA;
   }

   public ArtifactType getArtifactTypeSideB() {
      return artifactTypeSideB;
   }

   public ArtifactType getArtifactType(RelationSide relationSide) {
      return relationSide == RelationSide.SIDE_A ? getArtifactTypeSideA() : getArtifactTypeSideB();
   }

   public String getSideName(RelationSide relationSide) {
      return relationSide == RelationSide.SIDE_A ? getSideAName() : getSideBName();
   }

   public boolean isArtifactTypeAllowed(RelationSide relationSide, ArtifactType artifactType) {
      ArtifactType allowedType = getArtifactType(relationSide);
      return artifactType.inheritsFrom(allowedType);
   }

   /**
    * @return Returns the sideAName.
    */
   public String getSideAName() {
      return sideAName;
   }

   /**
    * @return Returns the sideBName.
    */
   public String getSideBName() {
      return sideBName;
   }

   public boolean isSideAName(String sideName) throws OseeArgumentException {
      if (!getSideAName().equals(sideName) && !getSideBName().equals(sideName)) {
         throw new OseeArgumentException("sideName does not match either of the available side names");
      }

      return getSideAName().equals(sideName);
   }

   public int compareTo(RelationType other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }

   @Override
   public String toString() {
      return String.format("[%s] <- [%s] -> [%s]", getSideAName(), getName(), getSideBName());
   }

   public boolean isOrdered() {
      return isOrdered;
   }

   public String getDefaultOrderTypeGuid() {
      return defaultOrderTypeGuid;
   }

   @Override
   public void clearDirty() {
      getDirtyDetails().clearDirty();
   }

   @Override
   public boolean isDirty() {
      return getDirtyDetails().isDirty();
   }

   public final class RelationTypeDirtyDetails {
      private boolean isSideANameDirty;
      private boolean isSideBNameDirty;
      private boolean isArtifactTypeSideADirty;
      private boolean isArtifactTypeSideBDirty;
      private boolean isMultiplicityDirty;
      private boolean isOrderedDirty;
      private boolean isDefaultOrderTypeGuidDirty;

      private RelationTypeDirtyDetails() {
         clearDirty();
      }

      private void update(String relationTypeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isOrdered, String defaultOrderTypeGuid) {
         isSideANameDirty |= isDifferent(getSideAName(), sideAName);
         isSideBNameDirty |= isDifferent(getSideBName(), sideBName);
         isArtifactTypeSideADirty |= isDifferent(getArtifactTypeSideA(), artifactTypeSideA);
         isArtifactTypeSideBDirty |= isDifferent(getArtifactTypeSideB(), artifactTypeSideB);
         isMultiplicityDirty |= isDifferent(getMultiplicity(), multiplicity);
         isOrderedDirty |= isDifferent(isOrdered(), isOrdered);
         isDefaultOrderTypeGuidDirty |= isDifferent(getDefaultOrderTypeGuid(), defaultOrderTypeGuid);
      }

      public boolean isNameDirty() {
         return RelationType.super.isDirty();
      }

      public boolean isSideANameDirty() {
         return isSideANameDirty;
      }

      public boolean isSideBNameDirty() {
         return isSideBNameDirty;
      }

      public boolean isArtifactTypeSideADirty() {
         return isArtifactTypeSideADirty;
      }

      public boolean isArtifactTypeSideBDirty() {
         return isArtifactTypeSideBDirty;
      }

      public boolean isMultiplicityDirty() {
         return isMultiplicityDirty;
      }

      public boolean isOrderedDirty() {
         return isOrderedDirty;
      }

      public boolean isDefaultOrderTypeGuidDirty() {
         return isDefaultOrderTypeGuidDirty;
      }

      public boolean isDirty() {
         return isNameDirty() || //
         isSideANameDirty() || //
         isSideBNameDirty() || //
         isArtifactTypeSideADirty() || //
         isArtifactTypeSideBDirty() || //
         isMultiplicityDirty() || //
         isOrderedDirty() || //
         isDefaultOrderTypeGuidDirty();
      }

      public void clearDirty() {
         RelationType.super.clearDirty();
         isSideANameDirty = false;
         isSideBNameDirty = false;
         isArtifactTypeSideADirty = false;
         isArtifactTypeSideBDirty = false;
         isMultiplicityDirty = false;
         isOrderedDirty = false;
         isDefaultOrderTypeGuidDirty = false;
      }
   }
}