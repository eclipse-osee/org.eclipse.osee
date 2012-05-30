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
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.RelationGraph;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Andrew M. Finkbeiner
 */
public class GraphImpl implements RelationGraph {

   private final SessionContext sessionContext;
   private final OrcsObjectLoader objectLoader;
   private final DataStoreTypeCache dataStoreTypeCache;

   public GraphImpl(SessionContext sessionContext, OrcsObjectLoader objectLoader, DataStoreTypeCache dataStoreTypeCache) {
      this.sessionContext = sessionContext;
      this.objectLoader = objectLoader;
      this.dataStoreTypeCache = dataStoreTypeCache;
   }

   @Override
   public Collection<IRelationTypeSide> getExistingRelationTypes(ReadableArtifact art) {
      return ((Artifact) art).getRelationContainer().getAvailableRelationTypes();
   }

   private List<ReadableArtifact> loadRelated(IOseeBranch branch, Collection<Integer> artIds) throws OseeCoreException {
      LoadOptions loadOptions = new LoadOptions(false, false, LoadLevel.FULL);
      HasCancellation cancellation = null;
      return objectLoader.load(cancellation, branch, artIds, loadOptions, sessionContext);
   }

   @Override
   public List<ReadableArtifact> getRelatedArtifacts(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      List<Integer> artIds = new ArrayList<Integer>();
      ((Artifact) art).getRelatedArtifacts(relationTypeSide, artIds);
      List<ReadableArtifact> toReturn;
      if (artIds.isEmpty()) {
         toReturn = Collections.emptyList();
      } else {
         toReturn = loadRelated(art.getBranch(), artIds);
      }
      return toReturn;
   }

   @Override
   public ReadableArtifact getRelatedArtifact(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      List<Integer> artIds = new ArrayList<Integer>();
      ((Artifact) art).getRelatedArtifacts(relationTypeSide, artIds);
      ReadableArtifact toReturn = null;
      if (!artIds.isEmpty()) {
         List<ReadableArtifact> artifacts = loadRelated(art.getBranch(), artIds);
         if (!artifacts.isEmpty()) {
            toReturn = artifacts.iterator().next();
         }
      }
      return toReturn;
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
