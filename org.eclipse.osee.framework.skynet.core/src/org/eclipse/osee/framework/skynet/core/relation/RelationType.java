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

   public RelationType(String guid, String relationTypeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isOrdered, String defaultOrderTypeGuid) {
      super(guid, relationTypeName);
      setFields(relationTypeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity, isOrdered,
            defaultOrderTypeGuid);
   }

   public void setFields(String relationTypeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isOrdered, String defaultOrderTypeGuid) {
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
   public boolean equals(Object obj) {
      if (obj instanceof RelationType) {
         return super.equals(obj);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public String toString() {
      return String.format("[%s]: [%s] <--> [%s]", getName(), getSideAName(), getSideBName());
   }

   public boolean isOrdered() {
      return isOrdered;
   }

   public String getDefaultOrderTypeGuid() {
      return defaultOrderTypeGuid;
   }
}