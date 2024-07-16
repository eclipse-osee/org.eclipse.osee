/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.rest.internal.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryImpl;
import org.eclipse.osee.ats.core.query.AtsAttributeQuery;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryImpl extends AbstractAtsQueryImpl {
   private final OrcsApi orcsApi;
   private QueryBuilder query;

   public AtsQueryImpl(AtsApi atsApi, OrcsApi orcsApi) {
      super(atsApi);
      this.orcsApi = orcsApi;
   }

   @Override
   public Collection<? extends ArtifactToken> runQuery() {
      return query.getResults().getList();
   }

   @Override
   public Collection<? extends ArtifactToken> runQueryNew() {
      return query.asArtifacts();
   }

   @Override
   public void createQueryBuilder() {
      if (query == null) {
         query = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch());
      }
   }

   @Override
   public void queryAnd(AttributeTypeToken attrType, Collection<String> values) {
      query.and(attrType, values);
   }

   @Override
   public void queryAndIsOfType(ArtifactTypeToken artifactType) {
      query.andIsOfType(artifactType);
   }

   @Override
   public List<ArtifactId> queryGetIds() {
      return query.asArtifactIds();
   }

   @Override
   public void queryAndIsOfType(Collection<ArtifactTypeToken> artTypes) {
      query.andIsOfType(artTypes);
   }

   @Override
   public void queryAnd(AttributeTypeToken attrType, String value) {
      query.and(attrType, value);
   }

   @Override
   public void queryAndRelatedToLocalIds(RelationTypeSide relationTypeSide, ArtifactId artId) {
      query.andRelatedTo(relationTypeSide, artId);
   }

   @Override
   public void queryAndRelatedTo(RelationTypeSide relationTypeSide, List<ArtifactId> artifacts) {
      query.andRelatedTo(relationTypeSide, artifacts);
   }

   @Override
   public void queryAnd(AttributeTypeToken attrType, Collection<String> values, QueryOption[] queryOption) {
      query.and(attrType, values, queryOption);
   }

   @Override
   public void queryAnd(AttributeTypeToken attrType, String value, QueryOption[] queryOption) {
      query.and(attrType, value, queryOption);
   }

   @Override
   public void queryAndIds(Collection<? extends ArtifactId> artIds) {
      query.andIds(artIds);
   }

   @Override
   public void queryAndNotExists(RelationTypeSide relationTypeSide) {
      query.andRelationNotExists(relationTypeSide);
   }

   @Override
   public void queryAndExists(RelationTypeSide relationTypeSide) {
      query.andRelationExists(relationTypeSide);
   }

   @Override
   public List<ArtifactId> getRelatedTeamWorkflowIdsBasedOnTeamDefsAisAndVersions(
      List<AtsAttributeQuery> teamWorkflowAttr) {
      AtsQueryImpl search = new AtsQueryImpl(atsApi, orcsApi);
      search.isOfType(AtsArtifactTypes.TeamWorkflow);
      if (teamDefIds != null && !teamDefIds.isEmpty()) {
         search.andTeam(new ArrayList<>(teamDefIds));
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
   protected void queryAndNotExists(AttributeTypeToken attributeType) {
      query.andNotExists(attributeType);
   }

   @Override
   protected void queryAndExists(AttributeTypeToken attributeType) {
      query.andExists(attributeType);
   }

}