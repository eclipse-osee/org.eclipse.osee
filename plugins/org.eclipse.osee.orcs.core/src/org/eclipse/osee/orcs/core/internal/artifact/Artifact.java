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
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeContainerImpl;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainerImpl;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;

public class Artifact extends NamedIdentity<String> implements ReadableArtifact {

   private final AttributeContainer attributeContainer;
   private final RelationContainer relationContainer;
   private final int artId;
   private final String humandReadableId;
   private final ArtifactType artifactType;
   private final int gammaId;
   private final Branch branch;
   private final int transactionId;
   private final ModificationType modType;
   private final boolean historical;

   public Artifact(int artId, String guid, String humandReadableId, ArtifactType artifactType, int gammaId, Branch branch, int transactionId, ModificationType modType, boolean historical, RelationTypeCache relationTypeCache) {
      super(guid, "");
      this.artId = artId;
      this.humandReadableId = humandReadableId;
      this.artifactType = artifactType;
      this.gammaId = gammaId;
      this.branch = branch;
      this.transactionId = transactionId;
      this.modType = modType;
      this.historical = historical;
      attributeContainer = new AttributeContainerImpl(this);
      relationContainer = new RelationContainerImpl(artId, relationTypeCache);

   }

   @Override
   public long getGammaId() {
      return gammaId;
   }

   @Override
   public ModificationType getModificationType() {
      return modType;
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
   public int getId() {
      return artId;
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
   }

   @Override
   public String getHumanReadableId() {
      return humandReadableId;
   }

   @Override
   public int getTransactionId() {
      return transactionId;
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
   public Collection<IAttributeType> getAttributeTypes() throws OseeCoreException {
      return attributeContainer.getAttributeTypes();
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return attributeContainer.getAttributes(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) throws OseeCoreException {
      if (attributeContainer.getAttributes(attributeType).isEmpty()) {
         return defaultValue;
      } else {
         return String.valueOf(attributeContainer.getAttributes(attributeType).iterator().next().getValue());
      }
   }

   public AttributeContainer getAttributeContainer() {
      return attributeContainer;
   }

   public RelationContainer getRelationContainer() {
      return relationContainer;
   }

   public Collection<IRelationTypeSide> getExistingRelationTypes() {
      return relationContainer.getAvailableRelationTypes();
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes() throws OseeCoreException {
      return getAttributeContainer().getAttributes();
   }

   public void getRelatedArtifacts(IRelationTypeSide relationTypeSide, Collection<Integer> results) {
      relationContainer.getArtifactIds(results, relationTypeSide);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException {
      return String.valueOf(attributeContainer.getAttributes(attributeType).iterator().next().getValue());
   }

}
