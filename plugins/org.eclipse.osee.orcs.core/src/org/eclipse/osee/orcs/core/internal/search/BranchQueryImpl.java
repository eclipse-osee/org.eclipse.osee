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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.core.ds.BranchData;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.data.BranchReadable;
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
      return query(new BranchLoadHandler<BranchReadable>());
   }

   @Override
   public ResultSet<IOseeBranch> getResultsAsId() {
      return query(new BranchLoadHandler<IOseeBranch>());
   }

   private <T extends IOseeBranch> ResultSet<T> query(BranchLoadHandler<T> handler) {
      QueryData queryData = build();
      OptionsUtil.setLoadLevel(queryData.getOptions(), LoadLevel.ALL);
      queryEngine.runBranchQuery(queryData, handler);
      return ResultSets.newResultSet(handler.getBranches());
   }

   public static final class BranchLoadHandler<T extends IOseeBranch> extends LoadDataHandlerAdapter {
      private final List<T> results = new LinkedList<>();

      @Override
      public void onLoadStart() {
         results.clear();
      }

      @Override
      public void onData(BranchData data) {
         results.add((T) data);
      }

      public List<T> getBranches() {
         return results;
      }
   }

   @Override
   public int getCount() {
      return queryEngine.getBranchCount(buildAndCopy());
   }

   @Override
   public boolean exists() {
      return getCount() > 0;
   }
}