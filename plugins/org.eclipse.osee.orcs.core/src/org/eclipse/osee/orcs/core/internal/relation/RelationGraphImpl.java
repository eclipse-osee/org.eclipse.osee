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
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.ArtifactLoaderFactory;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.util.ResultSets;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationGraphImpl implements GraphReadable {

   private final OrcsSession session;
   private final ArtifactLoaderFactory loader;
   private final RelationTypes relationTypeCache;
   private final ExternalArtifactManager proxyManager;

   public RelationGraphImpl(OrcsSession session, ArtifactLoaderFactory loader, RelationTypes relationTypeCache, ExternalArtifactManager proxyManager) {
      super();
      this.session = session;
      this.loader = loader;
      this.relationTypeCache = relationTypeCache;
      this.proxyManager = proxyManager;
   }

   private RelationContainer getRelationContainer(ArtifactReadable readable) throws OseeCoreException {
      RelationContainer toReturn = null;
      Object object = proxyManager.asInternalArtifact(readable);
      if (object instanceof HasRelationContainer) {
         HasRelationContainer proxy = (HasRelationContainer) object;
         toReturn = proxy.getRelationContainer();
      }
      return toReturn;
   }

   private List<ArtifactReadable> loadRelated(IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException {
      return loader.fromBranchAndArtifactIds(session, branch, artifactIds).setLoadLevel(LoadLevel.FULL).load();
   }

   private void loadRelatedArtifactIds(ArtifactReadable art, IRelationTypeSide relationTypeSide, Collection<Integer> results) throws OseeCoreException {
      RelationContainer container = getRelationContainer(art);
      container.getArtifactIds(results, relationTypeSide);
   }

   @Override
   public ArtifactReadable getParent(ArtifactReadable art) throws OseeCoreException {
      return getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent, art).getExactlyOne();
   }

   @Override
   public ResultSet<ArtifactReadable> getChildren(ArtifactReadable art) throws OseeCoreException {
      return getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child, art);
   }

   @Override
   public Collection<IRelationTypeSide> getExistingRelationTypes(ArtifactReadable art) throws OseeCoreException {
      RelationContainer container = getRelationContainer(art);
      return container.getExistingRelationTypes();
   }

   @Override
   public ResultSet<ArtifactReadable> getRelatedArtifacts(IRelationTypeSide relationTypeSide, ArtifactReadable art) throws OseeCoreException {
      List<Integer> artIds = new ArrayList<Integer>();
      loadRelatedArtifactIds(art, relationTypeSide, artIds);
      List<ArtifactReadable> toReturn;
      if (artIds.isEmpty()) {
         toReturn = Collections.emptyList();
      } else {
         toReturn = loadRelated(art.getBranch(), artIds);
      }
      return ResultSets.newResultSet(toReturn);
   }

   @Override
   public List<IRelationType> getValidRelationTypes(ArtifactReadable art) throws OseeCoreException {
      IArtifactType artifactType = art.getArtifactType();

      Collection<? extends IRelationType> relationTypes = relationTypeCache.getAll();
      List<IRelationType> validRelationTypes = new LinkedList<IRelationType>();
      for (IRelationType relationType : relationTypes) {
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
   public int getRelationSideMax(IRelationType relationType, IArtifactType artifactType, RelationSide relationSide) throws OseeCoreException {
      int toReturn = 0;
      if (relationTypeCache.isArtifactTypeAllowed(relationType, relationSide, artifactType)) {
         toReturn = relationTypeCache.getMultiplicity(relationType).getLimit(relationSide);
      }
      return toReturn;
   }

   @Override
   public RelationTypes getTypes() {
      return relationTypeCache;
   }
}
