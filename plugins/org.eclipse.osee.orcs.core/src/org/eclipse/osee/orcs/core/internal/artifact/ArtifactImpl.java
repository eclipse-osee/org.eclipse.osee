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

import java.util.Collection;
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
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManagerImpl;
import org.eclipse.osee.orcs.data.ArtifactWriteable;

public class ArtifactImpl extends AttributeManagerImpl implements ArtifactWriteable {

   private final RelationContainer relationContainer;
   private final ArtifactType artifactType;
   private final Branch branch;
   private EditState objectEditState;
   private ArtifactData artifactData;

   public ArtifactImpl(ArtifactType artifactType, Branch branch, ArtifactData artifactData, AttributeFactory attributeFactory, RelationContainer relationContainer) {
      super(attributeFactory);
      this.artifactData = artifactData;
      this.artifactType = artifactType;
      this.branch = branch;
      this.relationContainer = relationContainer;
      this.objectEditState = EditState.NO_CHANGE;
   }

   public RelationContainer getRelationContainer() {
      return relationContainer;
   }

   @Override
   public ArtifactData getOrcsData() {
      return artifactData;
   }

   @Override
   public void setOrcsData(ArtifactData data) {
      this.artifactData = data;
      objectEditState = EditState.NO_CHANGE;
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
   public ArtifactType getArtifactType() {
      return artifactType;
   }

   @Override
   public void setName(String name) throws OseeCoreException {
      setSoleAttributeFromString(CoreAttributeTypes.Name, name);
   }

   @Override
   public void setArtifactType(IArtifactType artifactType) {
      if (!this.artifactType.equals(artifactType)) {
         getOrcsData().setTypeUuid(artifactType.getGuid());

         objectEditState = EditState.ARTIFACT_TYPE_MODIFIED;
         if (getOrcsData().getVersion().isInStorage()) {
            //            lastValidModType = modType;
            getOrcsData().setModType(ModificationType.MODIFIED);
         }
      }
   }

   @Override
   public boolean isOfType(IArtifactType... otherTypes) {
      return getArtifactType().inheritsFrom(otherTypes);
   }

   @Override
   public final boolean isDirty() {
      return areAttributesDirty() || hasDirtyRelations() || hasDirtyArtifactType() || isReplaceWithVersion();
   }

   private final boolean isReplaceWithVersion() {
      return getModificationType() == ModificationType.REPLACED_WITH_VERSION;
   }

   private final boolean hasDirtyArtifactType() {
      return objectEditState.isArtifactTypeChange();
   }

   @Override
   public boolean isDeleted() {
      return getModificationType().isDeleted();
   }

   @Override
   public boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException {
      return getArtifactType().isValidAttributeType(attributeType, branch);
   }

   @Override
   public Collection<? extends IAttributeType> getValidAttributeTypes() throws OseeCoreException {
      return getArtifactType().getAttributeTypes(branch);
   }

   @Override
   public String getExceptionString() {
      return String.format("artifact type [%s] guid[%s] on branch[%s]", getArtifactType(), getGuid(), getBranch());
   }

   public final boolean hasDirtyRelations() {
      //TX_TODO: Implement this
      return false;
   }

}
