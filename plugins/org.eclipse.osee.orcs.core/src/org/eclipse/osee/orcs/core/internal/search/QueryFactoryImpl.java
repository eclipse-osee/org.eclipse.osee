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
package org.eclipse.osee.orcs.core.internal.search;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class QueryFactoryImpl implements QueryFactory {

   private final CriteriaFactory criteriaFctry = new CriteriaFactory();
   private final ResultSetFactory rsetFctry = new ResultSetFactory();

   private QueryBuilder createBuilder(Criteria baseCriteria) {
      QueryOptions options = new QueryOptions();
      CriteriaSet criteriaSet = new CriteriaSet();
      criteriaSet.setBaseCriteria(baseCriteria);
      return new QueryBuilderImpl(rsetFctry, criteriaFctry, criteriaSet, options);
   }

   @Override
   public QueryBuilder fromBranch(IOseeBranch branch) {
      return null;
   }

   @Override
   public QueryBuilder fromArtifactType(IOseeBranch branch, IArtifactType artifactType) {
      return null;
   }

   @Override
   public QueryBuilder fromArtifactTypes(IOseeBranch branch, Collection<? extends IArtifactType> artifactTypes) {
      return null;
   }

   @Override
   public QueryBuilder fromArtifactTypeAllBranches(IArtifactType artifactType) {
      return null;
   }

   @Override
   public QueryBuilder fromUuids(IOseeBranch branch, Collection<Integer> artifactIds) {
      return null;
   }

   @Override
   public QueryBuilder fromGuidOrHrid(IOseeBranch branch, String guidOrHrid) {
      return null;
   }

   @Override
   public QueryBuilder fromGuidOrHrids(IOseeBranch branch, List<String> guidOrHrids) {
      return null;
   }

   @Override
   public QueryBuilder fromArtifact(IOseeBranch branch, IArtifactToken artifactToken) {
      return null;
   }

   @Override
   public QueryBuilder fromArtifacts(Collection<? extends ReadableArtifact> artifacts) {
      return null;
   }

   @Override
   public QueryBuilder fromName(IOseeBranch branch, String artifactName) {
      return null;
   }

   @Override
   public QueryBuilder fromArtifactTypeAndName(IOseeBranch branch, IArtifactType artifactType, String artifactName) {
      return null;
   }

}
