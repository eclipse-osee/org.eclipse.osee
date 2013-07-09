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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class QueryFactoryImpl implements QueryFactory {

   private final OrcsSession context;
   private final CriteriaFactory criteriaFctry;
   private final CallableQueryFactory queryFctry;

   public QueryFactoryImpl(OrcsSession context, CriteriaFactory criteriaFctry, CallableQueryFactory queryFctry) {
      super();
      this.context = context;
      this.criteriaFctry = criteriaFctry;
      this.queryFctry = queryFctry;
   }

   @SuppressWarnings("unused")
   private QueryBuilder createBuilder(IOseeBranch branch) throws OseeCoreException {
      QueryOptions options = new QueryOptions();
      CriteriaSet criteriaSet = new CriteriaSet(branch);
      QueryData queryData = new QueryData(criteriaSet, options);
      QueryBuilder builder = new QueryBuilderImpl(queryFctry, criteriaFctry, context, queryData);
      return builder;
   }

   @Override
   public QueryBuilder fromBranch(IOseeBranch branch) throws OseeCoreException {
      return createBuilder(branch);
   }

   @Override
   public QueryBuilder fromArtifacts(Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(artifacts, "artifacts");
      ArtifactReadable artifact = artifacts.iterator().next();
      IOseeBranch branch = artifact.getBranch();
      Set<String> guids = new HashSet<String>();
      for (ArtifactReadable art : artifacts) {
         guids.add(art.getGuid());
      }
      return fromBranch(branch).andGuidsOrHrids(guids);
   }

   @Override
   public QueryBuilder fromArtifactTypeAllBranches(IArtifactType artifactType) throws OseeCoreException {
      QueryBuilder builder = createBuilder(null);
      builder.andIsOfType(artifactType);
      return builder;
   }
}
