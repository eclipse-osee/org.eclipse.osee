/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchReadable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
public class BranchQueryImpl extends BranchQueryBuilderImpl<BranchQuery> implements BranchQuery {
   private final QueryEngine queryEngine;

   public BranchQueryImpl(QueryEngine queryEngine, BranchCriteriaFactory criteriaFactory, QueryData queryData) {
      super(criteriaFactory, queryData);
      this.queryEngine = queryEngine;
   }

   @Override
   public ResultSet<BranchReadable> getResults() {
      List<BranchReadable> branches = new ArrayList<>();
      query(branches);
      return ResultSets.newResultSet(branches);
   }

   @Override
   public ResultSet<IOseeBranch> getResultsAsId() {
      List<IOseeBranch> branches = new ArrayList<>();
      query(branches);
      return ResultSets.newResultSet(branches);
   }

   private void query(List<? super BranchReadable> branches) {
      QueryData queryData = build();
      OptionsUtil.setLoadLevel(queryData.getOptions(), LoadLevel.ALL);
      queryEngine.runBranchQuery(queryData, branches);
   }

   @Override
   public int getCount() {
      return queryEngine.getBranchCount(build());
   }

   @Override
   public boolean exists() {
      return getCount() > 0;
   }
}