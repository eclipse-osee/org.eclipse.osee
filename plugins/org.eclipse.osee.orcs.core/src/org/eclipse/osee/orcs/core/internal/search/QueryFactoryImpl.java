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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class QueryFactoryImpl implements QueryFactory {

   private final SessionContext context;
   private final CriteriaFactory criteriaFctry;
   private final ResultSetFactory rsetFctry;

   public QueryFactoryImpl(CriteriaFactory criteriaFctry, ResultSetFactory rsetFctry, SessionContext context) {
      super();
      this.context = context;
      this.criteriaFctry = criteriaFctry;
      this.rsetFctry = rsetFctry;
   }

   @SuppressWarnings("unused")
   private QueryBuilder createBuilder(IOseeBranch branch) throws OseeCoreException {
      QueryOptions options = new QueryOptions();
      CriteriaSet criteriaSet = new CriteriaSet(branch);
      QueryBuilder builder = new QueryBuilderImpl(rsetFctry, criteriaFctry, context, criteriaSet, options);
      return builder;
   }

   @Override
   public QueryBuilder fromBranch(IOseeBranch branch) throws OseeCoreException {
      return createBuilder(branch);
   }

   @Override
   public QueryBuilder fromArtifactType(IOseeBranch branch, IArtifactType artifactType) throws OseeCoreException {
      return fromArtifactTypes(branch, Collections.singleton(artifactType));
   }

   @Override
   public QueryBuilder fromArtifactTypes(IOseeBranch branch, Collection<? extends IArtifactType> artifactTypes) throws OseeCoreException {
      QueryBuilder builder = fromBranch(branch);
      builder.and(artifactTypes);
      return builder;
   }

   @Override
   public QueryBuilder fromUuids(IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException {
      QueryBuilder builder = fromBranch(branch);
      builder.withLocalIds(artifactIds);
      return builder;
   }

   @Override
   public QueryBuilder fromGuidOrHrid(IOseeBranch branch, String guidOrHrid) throws OseeCoreException {
      return fromGuidOrHrids(branch, Collections.singleton(guidOrHrid));
   }

   @Override
   public QueryBuilder fromGuidOrHrids(IOseeBranch branch, Collection<String> guidOrHrids) throws OseeCoreException {
      QueryBuilder builder = fromBranch(branch);
      builder.withGuidsOrHrids(guidOrHrids);
      return builder;
   }

   @Override
   public QueryBuilder fromArtifact(IOseeBranch branch, IArtifactToken artifactToken) throws OseeCoreException {
      return fromGuidOrHrid(branch, artifactToken.getGuid());
   }

   @Override
   public QueryBuilder fromArtifacts(Collection<? extends ReadableArtifact> artifacts) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(artifacts, "artifacts");
      ReadableArtifact artifact = artifacts.iterator().next();
      IOseeBranch branch = artifact.getBranch();
      Set<String> guids = new HashSet<String>();
      for (ReadableArtifact art : artifacts) {
         guids.add(art.getGuid());
      }
      return fromGuidOrHrids(branch, guids);
   }

   @Override
   public QueryBuilder fromName(IOseeBranch branch, String artifactName) throws OseeCoreException {
      QueryBuilder builder = fromBranch(branch);
      builder.and(CoreAttributeTypes.Name, Operator.EQUAL, artifactName);
      return builder;
   }

   @Override
   public QueryBuilder fromArtifactTypeAndName(IOseeBranch branch, IArtifactType artifactType, String artifactName) throws OseeCoreException {
      QueryBuilder builder = fromArtifactType(branch, artifactType);
      builder.and(CoreAttributeTypes.Name, Operator.EQUAL, artifactName);
      return builder;
   }

   @Override
   public QueryBuilder fromArtifactTypeAllBranches(IArtifactType artifactType) throws OseeCoreException {
      QueryBuilder builder = createBuilder(null);
      builder.and(artifactType);
      return builder;
   }
}
