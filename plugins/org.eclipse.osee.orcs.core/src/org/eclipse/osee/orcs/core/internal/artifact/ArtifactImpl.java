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
package org.eclipse.osee.orcs.core.internal.artifact;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.EditState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.HasOrcsData;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeContainerImpl;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeWriteable;

public class ArtifactImpl extends NamedIdentity<String> implements HasOrcsData<ArtifactData>, ArtifactWriteable, Cloneable {

   private final AttributeContainer attributeContainer;
   private final RelationContainer relationContainer;
   private final ArtifactType artifactType;
   private final Branch branch;
   private EditState objectEditState;
   private final ArtifactData artifactData;

   public ArtifactImpl(ArtifactType artifactType, Branch branch, RelationContainer relationContainer, ArtifactData artifactData) {
      super(artifactData.getGuid(), "");
      this.artifactData = artifactData;
      this.artifactType = artifactType;
      this.branch = branch;
      this.attributeContainer = new AttributeContainerImpl(this);
      this.relationContainer = relationContainer;
      objectEditState = EditState.NO_CHANGE;
   }

   public AttributeContainer getAttributeContainer() {
      return attributeContainer;
   }

   public RelationContainer getRelationContainer() {
      return relationContainer;
   }

   @Override
   public ArtifactData getOrcsData() {
      return artifactData;
   }

   public ModificationType getModificationType() {
      return getOrcsData().getModType();
   }

   @Override
   public String getName() {
      String name;
      try {
         name = getSoleAttributeAsString(CoreAttributeTypes.Name);
      } catch (Exception ex) {
         name = Lib.exceptionToString(ex);
      }
      return name;
   }

   @Override
   public int getLocalId() {
      return getOrcsData().getLocalId();
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
   }

   @Override
   public String getHumanReadableId() {
      return getOrcsData().getHumanReadableId();
   }

   @Override
   public int getTransactionId() {
      return getOrcsData().getVersion().getTransactionId();
   }

   @Override
   public IArtifactType getArtifactType() {
      return artifactType;
   }

   @Override
   public boolean isOfType(IArtifactType... otherTypes) {
      return artifactType.inheritsFrom(otherTypes);
   }

   @Override
   public Collection<IAttributeType> getExistingAttributeTypes() throws OseeCoreException {
      return attributeContainer.getExistingAttributeTypes();
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) throws OseeCoreException {
      if (attributeContainer.getAttributes(attributeType).isEmpty()) {
         return defaultValue;
      } else {
         return String.valueOf(attributeContainer.getAttributes(attributeType).iterator().next().getValue());
      }
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException {
      return String.valueOf(getSoleAttributeValue(attributeType));
   }

   @Override
   public ArtifactImpl clone() throws CloneNotSupportedException {
      //      ArtifactImpl otherObject = (ArtifactImpl) super.clone();
      //      otherObject.humandReadableId = this.humandReadableId;
      //      otherObject.historical = this.historical;
      //      otherObject.branch = this.branch;
      //      otherObject.artifactType = this.artifactType;

      // TODO finish copying
      //      otherObject.relationProxy = this.relationProxy;
      //      otherObject.attributeContainer = new AttributeContainerImpl(otherObject);

      //      for (AttributeReadable<?> attribute : this.attributeContainer.getAttributes()) {
      //         attributeContainer.add(attribute.getAttributeType(), attribute.);
      //      }
      throw new CloneNotSupportedException("Implementation not finished");
   }

   @Override
   public <T> List<AttributeReadable<T>> getAttributes() throws OseeCoreException {
      return attributeContainer.getAttributes();
   }

   @Override
   public <T> List<AttributeReadable<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return attributeContainer.getAttributes(attributeType);
   }

   @Override
   public void setArtifactType(IArtifactType artifactType) {
      if (!this.artifactType.equals(artifactType)) {
         objectEditState = EditState.ARTIFACT_TYPE_MODIFIED;
         //TX_TODO
         //         if (version.isInStorage()) {
         //            lastValidModType = modType;
         //            modType = ModificationType.MODIFIED;
         //         }
      }
   }

   @Override
   public <T> List<AttributeWriteable<T>> getWriteableAttributes() throws OseeCoreException {
      //TX_TODO
      return null;
   }

   @Override
   public <T> List<AttributeWriteable<T>> getWriteableAttributes(IAttributeType attributeType) throws OseeCoreException {
      //TX_TODO
      return null;
   }

   @Override
   public final <T> T getSoleAttributeValue(IAttributeType attributeType) throws OseeCoreException {
      List<AttributeReadable<T>> soleAttributes = attributeContainer.getAttributes(attributeType);
      if (soleAttributes.isEmpty()) {
         if (!isAttributeTypeValid(attributeType)) {
            throw new OseeArgumentException("The attribute type %s is not valid for artifacts of type [%s]",
               attributeType, artifactType.getName());
         }
         throw new AttributeDoesNotExist("Attribute of type [%s] could not be found on artifact [%s] on branch [%s]",
            attributeType.getName(), getGuid(), getBranch().getGuid());
      } else if (soleAttributes.size() > 1) {
         throw new MultipleAttributesExist(
            "Attribute [%s] must have exactly one instance.  It currently has %d for artifact [%s]", attributeType,
            soleAttributes.size(), getGuid());
      }
      return soleAttributes.iterator().next().getValue();
   }

   @Override
   public void setSoleAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException {
      //TX_TODO
   }

   @Override
   public void setAttributes(IAttributeType attributeType, Collection<String> values) throws OseeCoreException {
      //TX_TODO
   }

   @Override
   public void deleteSoleAttribute(IAttributeType attributeType) throws OseeCoreException {
      //TX_TODO
   }

   @Override
   public void deleteAttributes(IAttributeType attributeType) throws OseeCoreException {
      //TX_TODO
   }

   @Override
   public void deleteAttributesWithValue(IAttributeType attributeType, Object value) throws OseeCoreException {
      //TX_TODO
   }

   /**
    * @return whether this artifact has unsaved attribute changes
    */
   public final boolean hasDirtyAttributes() {
      //TX_TODO: Implement this
      //      for (AttributeReadable<?> attribute : attributeContainer.getAttributes()) {
      //         if (attribute.isDirty()) {
      //            return true;
      //         }
      //      }
      return false;
   }

   /**
    * @return whether this artifact has unsaved relation changes
    */
   public final boolean hasDirtyRelations() {
      //TX_TODO: Implement this
      return false;
   }

   /**
    * @return whether this artifact has unsaved relation changes
    */
   public final boolean isDirty() {
      return hasDirtyAttributes() || hasDirtyRelations() || hasDirtyArtifactType();
   }

   private final boolean hasDirtyArtifactType() {
      return objectEditState.isArtifactTypeChange();
   }

   @Override
   public final boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException {
      return artifactType.isValidAttributeType(attributeType, branch);
   }

   @Override
   public void createAttribute(IAttributeType attributeType) throws OseeCoreException {
      //TX_TODO
   }

   @Override
   public <T> void createAttribute(IAttributeType attributeType, T value) throws OseeCoreException {
      //TX_TODO
   }

   @Override
   public void createAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException {
      //TX_TODO
   }

   @Override
   public <T> void setSoleAttribute(IAttributeType attributeType, T value) throws OseeCoreException {
      //TX_TODO
   }

   @Override
   public void setSoleAttributeFromStream(IAttributeType attributeType, InputStream inputStream) throws OseeCoreException {
      //TX_TODO
   }
}
