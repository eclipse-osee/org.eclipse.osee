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
package org.eclipse.osee.orcs.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.Graph;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Andrew M. Finkbeiner
 */
public class GraphImpl implements Graph {

   private final QueryFactory queryFactory;
   private final DataStoreTypeCache dataStoreTypeCache;

   public GraphImpl(QueryFactory queryFactory, DataStoreTypeCache dataStoreTypeCache) {
      this.queryFactory = queryFactory;
      this.dataStoreTypeCache = dataStoreTypeCache;
   }

   @Override
   public Collection<IRelationTypeSide> getExistingRelationTypes(ReadableArtifact art) {
      return ((Artifact) art).getRelationContainer().getAvailableRelationTypes();
   }

   @Override
   public List<ReadableArtifact> getRelatedArtifacts(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      List<Integer> results = new ArrayList<Integer>();
      ((Artifact) art).getRelatedArtifacts(relationTypeSide, results);
      List<ReadableArtifact> toReturn;
      if (results.size() == 0) {
         toReturn = Collections.emptyList();
      } else {
         QueryBuilder builder = queryFactory.fromBranch(art.getBranch()).andLocalIds(results);
         ResultSet<ReadableArtifact> resultSet = builder.getResults();
         toReturn = resultSet.getList();
      }
      return toReturn;
   }

   @Override
   public ReadableArtifact getRelatedArtifact(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      List<Integer> results = new ArrayList<Integer>();
      ((Artifact) art).getRelatedArtifacts(relationTypeSide, results);
      if (results.isEmpty()) {
         return null;
      } else {
         QueryBuilder builder = queryFactory.fromBranch(art.getBranch()).andLocalIds(results);
         ResultSet<ReadableArtifact> resultSet = builder.getResults();
         return resultSet.getOneOrNull();
      }
   }

   @Override
   public ReadableArtifact getParent(ReadableArtifact art) throws OseeCoreException {
      return getRelatedArtifact(art, CoreRelationTypes.Default_Hierarchical__Parent);
   }

   @Override
   public List<ReadableArtifact> getChildren(ReadableArtifact art) throws OseeCoreException {
      return getRelatedArtifacts(art, CoreRelationTypes.Default_Hierarchical__Child);
   }

   @Override
   public List<RelationType> getValidRelationTypes(ReadableArtifact art) throws OseeCoreException {
      IArtifactType artifactType = art.getArtifactType();
      Collection<RelationType> relationTypes = dataStoreTypeCache.getRelationTypeCache().getAll();
      List<RelationType> validRelationTypes = new ArrayList<RelationType>();
      for (RelationType relationType : relationTypes) {
         int sideAMax = getRelationSideMax(relationType, artifactType, RelationSide.SIDE_A);
         int sideBMax = getRelationSideMax(relationType, artifactType, RelationSide.SIDE_B);
         boolean onSideA = sideBMax > 0;
         boolean onSideB = sideAMax > 0;
         if (onSideA || onSideB) {
            validRelationTypes.add(relationType);
         }
      }
      return validRelationTypes;
   }

   @Override
   public int getRelationSideMax(RelationType relationType, IArtifactType artifactType, RelationSide relationSide) throws OseeCoreException {
      int toReturn = 0;
      ArtifactType type = dataStoreTypeCache.getArtifactTypeCache().get(artifactType);
      if (relationType.isArtifactTypeAllowed(relationSide, type)) {
         toReturn = relationType.getMultiplicity().getLimit(relationSide);
      }
      return toReturn;
   }

   @Override
   public RelationType getFullRelationType(IRelationTypeSide relationTypeSide) throws OseeCoreException {
      return dataStoreTypeCache.getRelationTypeCache().get(relationTypeSide);
   }
}
