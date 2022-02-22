/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.loader.LoadSqlContext;
import org.eclipse.osee.orcs.db.internal.loader.LoadUtil;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.sql.join.Id4JoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class LoadExecutor extends AbstractLoadExecutor {

   private final SqlJoinFactory joinFactory;
   private final OrcsSession session;
   private final BranchId branch;
   private final Collection<ArtifactId> artifactIds;

   public LoadExecutor(SqlObjectLoader loader, JdbcClient jdbcClient, SqlJoinFactory joinFactory, OrcsSession session, BranchId branch, Collection<ArtifactId> artifactIds) {
      super(loader, jdbcClient);
      this.joinFactory = joinFactory;
      this.session = session;
      this.branch = branch;
      this.artifactIds = artifactIds;
   }

   @Override
   public void load(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, Options options) {
      checkCancelled(cancellation);

      Id4JoinQuery join = joinFactory.createId4JoinQuery();
      TransactionId transactionId = OptionsUtil.getFromTransaction(options);
      for (ArtifactId artId : artifactIds) {
         join.add(branch, artId, transactionId);
      }
      List<BranchCategoryToken> branchCategories = new ArrayList<>();
      Consumer<BranchCategoryToken> consumer = branchCategories::add;
      getJdbcClient().runQuery(stmt -> consumer.accept(BranchCategoryToken.valueOf(stmt.getLong("category"))),
         OseeSql.GET_CURRENT_BRANCH_CATEGORIES.getSql(), branch);
      LoadSqlContext loadContext = new LoadSqlContext(session, options, branch, branchCategories);
      int fetchSize = LoadUtil.computeFetchSize(artifactIds.size());
      getLoader().loadArtifacts(cancellation, handler, join, criteria, loadContext, fetchSize);
   }
}