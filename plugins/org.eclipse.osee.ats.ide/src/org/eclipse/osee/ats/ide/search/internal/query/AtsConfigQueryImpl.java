/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.search.internal.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.core.query.AbstractAtsConfigQueryImpl;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigQueryImpl extends AbstractAtsConfigQueryImpl {

   private QueryBuilderArtifact query;

   public AtsConfigQueryImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public Collection<ArtifactToken> runQuery() {
      List<ArtifactToken> results = new ArrayList<>();
      Iterator<Artifact> iterator = query.getResults().iterator();
      while (iterator.hasNext()) {
         results.add(iterator.next());
      }
      return results;
   }

   @Override
   public void createQueryBuilder() {
      query = ArtifactQuery.createQueryBuilder(AtsApiService.get().getAtsBranch());
   }

   private QueryBuilderArtifact getQuery() {
      Conditions.checkNotNull(query, "Query builder not created");
      return query;
   }

   @Override
   public void queryAnd(AttributeTypeToken attrType, Collection<String> values) {
      getQuery().and(attrType, values);
   }

   @Override
   public void queryAndIsOfType(ArtifactTypeToken artifactType) {
      getQuery().andIsOfType(artifactType);
   }

   @Override
   public List<ArtifactId> queryGetIds() {
      return getQuery().getIds();
   }

   @Override
   public void queryAndIsOfType(List<ArtifactTypeToken> artTypes) {
      getQuery().andIsOfType(artTypes);
   }

   @Override
   public void queryAnd(AttributeTypeToken attrType, String value) {
      getQuery().and(attrType, value);
   }

   @Override
   public void queryAnd(AttributeTypeToken attrType, Collection<String> values, QueryOption[] queryOption) {
      getQuery().and(attrType, values, queryOption);
   }

   @Override
   public void queryAnd(AttributeTypeToken attrType, String value, QueryOption[] queryOption) {
      getQuery().and(attrType, value, queryOption);
   }

   @Override
   public void queryAndArtifactId(ArtifactId artifactId) {
      getQuery().andId(artifactId);
   }

   @Override
   public void queryAndNotExists(RelationTypeSide relationTypeSide) {
      getQuery().andNotExists(relationTypeSide);
   }

   @Override
   public void queryAndExists(RelationTypeSide relationTypeSide) {
      getQuery().andExists(relationTypeSide);
   }

}
