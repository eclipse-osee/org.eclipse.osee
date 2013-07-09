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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.EditState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManagerImpl;
import org.eclipse.osee.orcs.core.internal.relation.HasRelationContainer;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainer;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.ArtifactWriteable;

public class Artifact extends AttributeManagerImpl implements ArtifactWriteable, HasRelationContainer, ArtifactVisitable {

   private final ArtifactTypes artifactTypeCache;
   private final RelationContainer relationContainer;
   private EditState objectEditState;
   private ArtifactData artifactData;
   private final ValueProvider<Branch, ArtifactData> branchProvider;

   public Artifact(ArtifactTypes artifactTypeCache, ArtifactData artifactData, AttributeFactory attributeFactory, RelationContainer relationContainer, ValueProvider<Branch, ArtifactData> branchProvider) {
      super(attributeFactory);
      this.artifactTypeCache = artifactTypeCache;
      this.artifactData = artifactData;
      this.branchProvider = branchProvider;
      this.relationContainer = relationContainer;
      this.objectEditState = EditState.NO_CHANGE;
   }

   @Override
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
      branchProvider.setOrcsData(data);
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
   public int getTransaction() {
      return getOrcsData().getVersion().getTransactionId();
   }

   @Override
   public Branch getBranch() throws OseeCoreException {
      return branchProvider.get();
   }

   @Override
   public IArtifactType getArtifactType() throws OseeCoreException {
      return artifactTypeCache.getByUuid(getOrcsData().getTypeUuid());
   }

   @Override
   public void setName(String name) throws OseeCoreException {
      setSoleAttributeFromString(CoreAttributeTypes.Name, name);
   }

   @Override
   public void setArtifactType(IArtifactType artifactType) throws OseeCoreException {
      if (!getArtifactType().equals(artifactType)) {
         getOrcsData().setTypeUuid(artifactType.getGuid());

         objectEditState = EditState.ARTIFACT_TYPE_MODIFIED;
         if (getOrcsData().getVersion().isInStorage()) {
            //            lastValidModType = modType;
            getOrcsData().setModType(ModificationType.MODIFIED);
         }
      }
   }

   @Override
   public boolean isOfType(IArtifactType... otherTypes) throws OseeCoreException {
      return artifactTypeCache.inheritsFrom(getArtifactType(), otherTypes);
   }

   @Override
   public boolean isDirty() {
      return areAttributesDirty() || hasDirtyRelations() || hasDirtyArtifactType() || isReplaceWithVersion();
   }

   private boolean isReplaceWithVersion() {
      return getModificationType() == ModificationType.REPLACED_WITH_VERSION;
   }

   private boolean hasDirtyArtifactType() {
      return objectEditState.isArtifactTypeChange();
   }

   @Override
   public boolean isDeleted() {
      return getModificationType().isDeleted();
   }

   @Override
   public boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException {
      return artifactTypeCache.isValidAttributeType(getArtifactType(), getBranch(), attributeType);
   }

   @Override
   public Collection<? extends IAttributeType> getValidAttributeTypes() throws OseeCoreException {
      return artifactTypeCache.getAttributeTypes(getArtifactType(), getBranch());
   }

   @Override
   public String getExceptionString() {
      try {
         return String.format("artifact type [%s] guid[%s] on branch[%s]", getArtifactType(), getGuid(), getBranch());
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   public boolean hasDirtyRelations() {
      //TX_TODO: Implement this
      return false;
   }

   @Override
   public void accept(ArtifactVisitor visitor) throws OseeCoreException {
      visitor.visit(this);
      for (Attribute<?> attribute : getAllAttributes()) {
         visitor.visit(attribute);
      }
      // TX_TODO loop through relations

   }

   @Override
   public void delete() throws OseeCoreException {
      getOrcsData().setModType(ModificationType.DELETED);
      deleteAttributesByArtifact();
      //TX_TODO Delete artifact and relation stuff
      //      public static void deleteArtifact(SkynetTransaction transaction, boolean overrideDeleteCheck, final Artifact... artifacts) throws OseeCoreException {
      //         deleteArtifactCollection(transaction, overrideDeleteCheck, Arrays.asList(artifacts));
      //      }
      //
      //      public static void deleteArtifactCollection(SkynetTransaction transaction, boolean overrideDeleteCheck, final Collection<Artifact> artifacts) throws OseeCoreException {
      //         if (artifacts.isEmpty()) {
      //            return;
      //         }
      //
      //         if (!overrideDeleteCheck) {
      //            performDeleteChecks(artifacts);
      //         }
      //
      //         bulkLoadRelatives(artifacts);
      //
      //         boolean reorderRelations = true;
      //         for (Artifact artifact : artifacts) {
      //            deleteTrace(artifact, transaction, reorderRelations);
      //         }
      //      }
      //      private static void deleteTrace(Artifact artifact, SkynetTransaction transaction, boolean reorderRelations) throws OseeCoreException {
      //         if (!artifact.isDeleted()) {
      //            // This must be done first since the the actual deletion of an
      //            // artifact clears out the link manager
      //            for (Artifact childArtifact : artifact.getChildren()) {
      //               deleteTrace(childArtifact, transaction, false);
      //            }
      //            try {
      //               // calling deCache here creates a race condition when the handleRelationModifiedEvent listeners fire - RS
      //               //          ArtifactCache.deCache(artifact);
      //               artifact.internalSetDeleted();
      //               RelationManager.deleteRelationsAll(artifact, reorderRelations, transaction);
      //               if (transaction != null) {
      //                  artifact.persist(transaction);
      //               }
      //            } catch (OseeCoreException ex) {
      //               artifact.resetToPreviousModType();
      //               throw ex;
      //            }
      //         }
      //      }
   }
}
