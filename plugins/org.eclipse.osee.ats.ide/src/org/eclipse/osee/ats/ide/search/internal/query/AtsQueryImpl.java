/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.search.internal.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryImpl;
import org.eclipse.osee.ats.core.query.AtsAttributeQuery;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryImpl extends AbstractAtsQueryImpl {

   private QueryBuilderArtifact query;

   public AtsQueryImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public Collection<ArtifactId> runQuery() {
      List<ArtifactId> results = new ArrayList<>();
      Iterator<Artifact> iterator = query.getResults().iterator();
      while (iterator.hasNext()) {
         results.add(iterator.next());
      }
      return results;
   }

   @Override
   public void createQueryBuilder() {
      if (query == null) {
         query = ArtifactQuery.createQueryBuilder(AtsClientService.get().getAtsBranch());
      }
   }

   @Override
   public void queryAnd(AttributeTypeId attrType, Collection<String> values) {
      query.and(attrType, values);
   }

   @Override
   public void queryAndIsOfType(IArtifactType artifactType) {
      query.andIsOfType(artifactType);
   }

   @Override
   public List<ArtifactId> queryGetIds() {
      return query.getIds();
   }

   @Override
   public void queryAndIsOfType(Collection<IArtifactType> artTypes) {
      query.andIsOfType(artTypes);
   }

   @Override
   public void queryAnd(AttributeTypeId attrType, String value) {
      query.and(attrType, value);
   }

   @Override
   public void queryAndRelatedToLocalIds(RelationTypeSide relationTypeSide, ArtifactId artId) {
      query.andRelatedTo(relationTypeSide, artId);
   }

   @Override
   public void queryAndRelatedTo(RelationTypeSide relationTypeSide, List<ArtifactId> artIds) {
      query.andRelatedTo(relationTypeSide, artIds);
   }

   @Override
   public void queryAnd(AttributeTypeId attrType, Collection<String> values, QueryOption[] queryOption) {
      query.and(attrType, values, queryOption);
   }

   @Override
   public void queryAnd(AttributeTypeId attrType, String value, QueryOption[] queryOption) {
      query.and(attrType, value, queryOption);
   }

   @Override
   public void queryAndIds(Collection<? extends ArtifactId> artIds) {
      query.andIds(artIds);
   }

   @Override
   public void queryAndNotExists(RelationTypeSide relationTypeSide) {
      query.andNotExists(relationTypeSide);
   }

   @Override
   public void queryAndExists(RelationTypeSide relationTypeSide) {
      query.andExists(relationTypeSide);
   }

   @Override
   public List<ArtifactId> getWorkPackagesForColorTeam(String colorTeam) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromTypeAndAttribute(AtsArtifactTypes.WorkPackage,
         AtsAttributeTypes.ColorTeam, colorTeam, AtsClientService.get().getAtsBranch()));
   }

   @Override
   public List<ArtifactId> getRelatedTeamWorkflowIdsBasedOnTeamDefsAisAndVersions(List<AtsAttributeQuery> teamWorkflowAttr) {
      AtsQueryImpl search = new AtsQueryImpl(atsApi);
      search.isOfType(AtsArtifactTypes.TeamWorkflow);
      if (teamDefIds != null && !teamDefIds.isEmpty()) {
         search.andTeam(new ArrayList<>(teamDefIds));
      }
      if (teamWorkflowAttr != null && !teamWorkflowAttr.isEmpty()) {
         for (AtsAttributeQuery attrQuery : teamWorkflowAttr) {
            search.andAttr.add(attrQuery);
         }
      }
      if (aiIds != null && !aiIds.isEmpty()) {
         search.andActionableItem(new ArrayList<>(aiIds));
      }
      if (versionId != null && versionId > 0) {
         search.andVersion(versionId);
      }
      return new ArrayList<>(search.getItemIds());
   }

   @Override
   protected void queryAndNotExists(AttributeTypeId attributeType) {
      query.andNotExists(attributeType);
   }

   @Override
   protected void queryAndExists(AttributeTypeToken attributeType) {
      query.andExists(attributeType);
   }

}