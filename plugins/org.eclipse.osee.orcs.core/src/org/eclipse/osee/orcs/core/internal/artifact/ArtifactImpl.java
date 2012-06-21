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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.EditState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.HasOrcsData;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManagerImpl;
import org.eclipse.osee.orcs.data.ArtifactWriteable;

public class ArtifactImpl extends AttributeManagerImpl implements HasOrcsData<ArtifactData>, ArtifactWriteable, Cloneable {

   private final RelationContainer relationContainer;
   private final ArtifactType artifactType;
   private final Branch branch;
   private EditState objectEditState;
   private final ArtifactData artifactData;

   public ArtifactImpl(ArtifactType artifactType, Branch branch, ArtifactData artifactData, AttributeFactory attributeFactory, RelationContainer relationContainer) {
      super(artifactData, attributeFactory);
      this.artifactData = artifactData;
      this.artifactType = artifactType;
      this.branch = branch;
      this.relationContainer = relationContainer;
      objectEditState = EditState.NO_CHANGE;
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
   public int getLocalId() {
      return getOrcsData().getLocalId();
   }

   @Override
   public String getGuid() {
      return getOrcsData().getGuid();
   }

   @Override
   public String getHumanReadableId() {
      return getOrcsData().getHumanReadableId();
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
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
   public void setName(String name) throws OseeCoreException {
      setSoleAttributeFromString(CoreAttributeTypes.Name, name);
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
   public boolean isOfType(IArtifactType... otherTypes) {
      return artifactType.inheritsFrom(otherTypes);
   }

   @Override
   public final boolean isDirty() {
      return areAttributesDirty() || hasDirtyRelations() || hasDirtyArtifactType();
   }

   private final boolean hasDirtyArtifactType() {
      return objectEditState.isArtifactTypeChange();
   }

   public final boolean hasDirtyRelations() {
      //TX_TODO: Implement this
      return false;
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
   public boolean isDeleted() {
      return getModificationType() == ModificationType.DELETED;
   }

   @Override
   public boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException {
      return artifactType.isValidAttributeType(attributeType, branch);
   }

   @Override
   public String getExceptionString() {
      return String.format("artifact [%s] guid[%s] on branch[%s]", getName(), getGuid(), getBranch());
   }

}
