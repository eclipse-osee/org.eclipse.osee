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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeContainerImpl;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainerImpl;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.ResultSet;

public class Artifact implements ReadableArtifact {

   private final AttributeContainer attributeContainer;
   private final RelationContainer relationContainer;
   private final int artId;
   private final String guid;
   private final String humandReadableId;
   private final IArtifactType artifactType;
   private final int gammaId;
   private final Branch branch;
   private final int transactionId;
   private final ModificationType modType;
   private final boolean historical;

   public Artifact(int artId, String guid, String humandReadableId, IArtifactType artifactType, int gammaId, Branch branch, int transactionId, ModificationType modType, boolean historical, RelationTypeCache relationTypeCache) {

      this.artId = artId;
      this.guid = guid;
      this.humandReadableId = humandReadableId;
      this.artifactType = artifactType;
      this.gammaId = gammaId;
      this.branch = branch;
      this.transactionId = transactionId;
      this.modType = modType;
      this.historical = historical;
      NamedIdentity<String> namedIdentity = new NamedIdentity<String>(guid, humandReadableId);
      attributeContainer = new AttributeContainerImpl(namedIdentity);
      relationContainer = new RelationContainerImpl(artId, relationTypeCache);

   }

   @Override
   public int getGammaId() {
      return gammaId;
   }

   @Override
   public ModificationType getModificationType() {
      return modType;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      return false;
   }

   @Override
   public String getName() {
      return null;
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
   public Collection<IAttributeType> getAttributeTypes() throws OseeCoreException {
      return attributeContainer.getAttributeTypes();
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return attributeContainer.getAttributes(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) {
      return null;
   }

   @Override
   public boolean hasParent() {
      return false;
   }

   @Override
   public ReadableArtifact getParent() {
      return null;
   }

   public AttributeContainer getAttributeContainer() {
      return attributeContainer;
   }

   public RelationContainer getRelationContainer() {
      return relationContainer;
   }

   @Override
   public List<ReadableArtifact> getRelatedArtifacts(IRelationTypeSide relationTypeSide, QueryFactory queryFactory) throws OseeCoreException {
      List<Integer> results = new ArrayList<Integer>();
      relationContainer.getArtifactIds(results, relationTypeSide);
      if (results.size() > 0) {
         QueryBuilder builder = queryFactory.fromUuids(getBranch(), results);
         ResultSet<ReadableArtifact> resultSet = builder.build(LoadLevel.FULL);
         return resultSet.getList();
      } else {
         return Collections.EMPTY_LIST;//TODO
      }
   }

   @Override
   public ReadableArtifact getRelatedArtifact(IRelationTypeSide relationTypeSide, QueryFactory queryFactory) throws OseeCoreException {
      return null;
   }

   @Override
   public Collection<IRelationType> getValidRelationTypes() throws OseeCoreException {
      return null;
   }

   @Override
   public Collection<IRelationTypeSide> getAvailableRelationTypes() throws OseeCoreException {
      return relationContainer.getAvailableRelationTypes();
   }
}
