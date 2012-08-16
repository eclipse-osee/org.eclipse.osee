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

import java.util.concurrent.CancellationException;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.db.internal.loader.LoadSqlContext;
import org.eclipse.osee.orcs.db.internal.loader.RelationalConstants;
import org.eclipse.osee.orcs.db.internal.loader.SqlArtifactLoader;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class AbstractLoadExecutor {

   private final SqlArtifactLoader loader;
   private final IOseeDatabaseService dbService;

   protected AbstractLoadExecutor(SqlArtifactLoader loader, IOseeDatabaseService dbService) {
      super();
      this.loader = loader;
      this.dbService = dbService;
   }

   public abstract void load(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, LoadOptions options) throws OseeCoreException;

   protected IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   protected void loadFromJoin(ArtifactJoinQuery join, HasCancellation cancellation, LoadDataHandler builder, CriteriaOrcsLoad criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      loader.loadArtifacts(cancellation, builder, join, criteria, loadContext, fetchSize);
   }

   protected void checkCancelled(HasCancellation cancellation) throws CancellationException {
      if (cancellation != null) {
         cancellation.checkForCancelled();
      }
   }

   protected int computeFetchSize(int initialSize) {
      int fetchSize = initialSize;

      if (fetchSize < 10) {
         fetchSize = 10;
      }

      // Account for attribute and relation loading
      fetchSize *= 20;

      if (fetchSize < 0 || fetchSize > RelationalConstants.MAX_FETCH_SIZE) {
         fetchSize = RelationalConstants.MAX_FETCH_SIZE;
      }
      return fetchSize;
   }
}
