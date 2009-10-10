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
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeType;
import org.eclipse.osee.framework.skynet.core.types.field.OseeField;

/**
 * @author Robert A. Fisher
 */
public class RelationType extends AbstractOseeType implements Comparable<RelationType> {

   private static final String RELATION_SIDE_A_NAME_FIELD_KEY = "osee.relation.type.side.a.name.field";
   private static final String RELATION_SIDE_B_NAME_FIELD_KEY = "osee.relation.type.side.b.name.field";
   private static final String RELATION_SIDE_A_ART_TYPE_FIELD_KEY = "osee.relation.type.side.a.artifact.type.field";
   private static final String RELATION_SIDE_B_ART_TYPE_FIELD_KEY = "osee.relation.type.side.b.artifact.type.field";
   private static final String RELATION_DEFAULT_ORDER_TYPE_GUID_FIELD_KEY =
         "osee.relation.type.default.order.type.guid.field";
   private static final String RELATION_MULTIPLICITY_FIELD_KEY = "osee.relation.type.multiplicity.field";

   public RelationType(AbstractOseeCache<RelationType> cache, String guid, String relationTypeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, String defaultOrderTypeGuid) {
      super(cache, guid, relationTypeName);
      setFields(relationTypeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
            defaultOrderTypeGuid);
   }

   @Override
   protected void initializeFields() {
      addField(RELATION_SIDE_A_NAME_FIELD_KEY, new OseeField<String>());
      addField(RELATION_SIDE_B_NAME_FIELD_KEY, new OseeField<String>());
      addField(RELATION_SIDE_A_ART_TYPE_FIELD_KEY, new OseeField<ArtifactType>());
      addField(RELATION_SIDE_B_ART_TYPE_FIELD_KEY, new OseeField<ArtifactType>());
      addField(RELATION_DEFAULT_ORDER_TYPE_GUID_FIELD_KEY, new OseeField<String>());
      addField(RELATION_MULTIPLICITY_FIELD_KEY, new OseeField<RelationTypeMultiplicity>());
   }

   public void setFields(String relationTypeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, String defaultOrderTypeGuid) {
      setName(relationTypeName);
      setFieldLogException(RELATION_SIDE_A_NAME_FIELD_KEY, sideAName);
      setFieldLogException(RELATION_SIDE_B_NAME_FIELD_KEY, sideBName);
      setFieldLogException(RELATION_SIDE_A_ART_TYPE_FIELD_KEY, artifactTypeSideA);
      setFieldLogException(RELATION_SIDE_B_ART_TYPE_FIELD_KEY, artifactTypeSideB);
      setFieldLogException(RELATION_DEFAULT_ORDER_TYPE_GUID_FIELD_KEY, defaultOrderTypeGuid);
      setFieldLogException(RELATION_MULTIPLICITY_FIELD_KEY, multiplicity);
   }

   public RelationTypeMultiplicity getMultiplicity() {
      return getFieldValueLogException(null, RELATION_MULTIPLICITY_FIELD_KEY);
   }

   public ArtifactType getArtifactTypeSideA() {
      return getFieldValueLogException(null, RELATION_SIDE_A_ART_TYPE_FIELD_KEY);
   }

   public ArtifactType getArtifactTypeSideB() {
      return getFieldValueLogException(null, RELATION_SIDE_B_ART_TYPE_FIELD_KEY);
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
      return getFieldValueLogException("", RELATION_SIDE_A_NAME_FIELD_KEY);
   }

   /**
    * @return Returns the sideBName.
    */
   public String getSideBName() {
      return getFieldValueLogException("", RELATION_SIDE_B_NAME_FIELD_KEY);
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
      return String.format("[%s]<-[%s]->[%s]", getSideAName(), getName(), getSideBName());
   }

   public boolean isOrdered() {
      return !RelationOrderBaseTypes.UNORDERED.getGuid().equals(getDefaultOrderTypeGuid());
   }

   public String getDefaultOrderTypeGuid() {
      return getFieldValueLogException("", RELATION_DEFAULT_ORDER_TYPE_GUID_FIELD_KEY);
   }
}