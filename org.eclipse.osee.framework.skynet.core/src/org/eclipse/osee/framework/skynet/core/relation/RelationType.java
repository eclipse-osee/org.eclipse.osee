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

/**
 * @author Robert A. Fisher
 */
public class RelationType implements Comparable<RelationType> {
   private final int relationTypeId;
   private final String typeName;
   private final String sideAName;
   private final String sideBName;
   private final RelationTypeMultiplicity multiplicity;
   private final ArtifactType artifactTypeSideA;
   private final ArtifactType artifactTypeSideB;

   private final String ordered;
   private final String defaultOrderTypeGuid;

   public RelationType(int linkTypeId, String typeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, String ordered, String defaultOrderTypeGuid) {
      super();
      this.relationTypeId = linkTypeId;
      this.typeName = typeName;
      this.sideAName = sideAName;
      this.sideBName = sideBName;
      this.artifactTypeSideA = artifactTypeSideA;
      this.artifactTypeSideB = artifactTypeSideB;
      this.multiplicity = multiplicity;

      this.ordered = ordered;
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

   public String getTypeName() {
      return typeName;
   }

   public String getSideName(RelationSide relationSide) {
      return relationSide == RelationSide.SIDE_A ? getSideAName() : getSideBName();
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

   public int compareTo(RelationType descriptor) {
      return typeName.compareTo(descriptor.getTypeName());
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationType) {
         return relationTypeId == ((RelationType) obj).relationTypeId;
      }
      return false;
   }

   @Override
   public int hashCode() {
      return 17 * relationTypeId;
   }

   @Override
   public String toString() {
      return String.format("[%s]: [%s] <--> [%s]", getTypeName(), getSideAName(), getSideBName());
   }

   public int getRelationTypeId() {
      return relationTypeId;
   }

   public boolean isOrdered() {
      return ordered.equalsIgnoreCase("Yes");
   }

   public String getDefaultOrderTypeGuid() {
      return defaultOrderTypeGuid;
   }
}