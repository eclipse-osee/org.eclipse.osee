/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.executors;

import java.util.Collection;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
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
   private final Collection<Integer> artifactIds;

   public LoadExecutor(SqlObjectLoader loader, JdbcClient jdbcClient, SqlJoinFactory joinFactory, OrcsSession session, BranchId branch, Collection<Integer> artifactIds) {
      super(loader, jdbcClient);
      this.joinFactory = joinFactory;
      this.session = session;
      this.branch = branch;
      this.artifactIds = artifactIds;
   }

   @Override
   public void load(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, Options options)  {
      checkCancelled(cancellation);

      Id4JoinQuery join = joinFactory.createId4JoinQuery();
      TransactionId transactionId = OptionsUtil.getFromTransaction(options);
      for (Integer artId : artifactIds) {
         join.add(branch, ArtifactId.valueOf(artId), transactionId);
      }

      LoadSqlContext loadContext = new LoadSqlContext(session, options, branch);
      int fetchSize = LoadUtil.computeFetchSize(artifactIds.size());
      getLoader().loadArtifacts(cancellation, handler, join, criteria, loadContext, fetchSize);
   }
}