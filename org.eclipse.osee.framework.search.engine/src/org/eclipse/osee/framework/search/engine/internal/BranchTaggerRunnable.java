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
package org.eclipse.osee.framework.search.engine.internal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.utility.DatabaseUtil;
import org.eclipse.osee.framework.search.engine.utility.IRowProcessor;

/**
 * @author Roberto E. Escobar
 */
public class BranchTaggerRunnable implements Runnable {

   private final int branchId;
   private final BranchToQueryTx branchToQueryTx;

   BranchTaggerRunnable(ISearchEngineTagger tagger, ITagListener listener, int branchId, boolean isCacheAll, int cacheLimit) {
      this.branchToQueryTx = new BranchToQueryTx(tagger, listener, isCacheAll, cacheLimit);
      this.branchId = branchId;
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      try {
         branchToQueryTx.execute();
      } catch (Exception ex) {
         OseeLog.log(BranchTaggerRunnable.class, Level.SEVERE, ex);
      }
   }

   private final class BranchToQueryTx extends InputToTagQueueTx implements IRowProcessor {
      private Connection connection;

      public BranchToQueryTx(ISearchEngineTagger tagger, ITagListener listener, boolean isCacheAll, int cacheLimit) {
         super(tagger, listener, isCacheAll, cacheLimit);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.search.engine.internal.ConvertToTagQueueTx#doWork(java.sql.Connection)
       */
      @Override
      protected void convertInput(Connection connection) throws Exception {
         this.connection = connection;
         DatabaseUtil.executeQuery(connection, AttributeDataStore.getAllTaggableGammasByBranchQuery(connection,
               branchId), BranchToQueryTx.this, AttributeDataStore.getAllTaggableGammasByBranchQueryData(branchId));
         this.connection = null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.search.engine.utility.IRowProcessor#processRow(java.sql.ResultSet)
       */
      @Override
      public void processRow(ResultSet resultSet) throws Exception {
         addEntry(connection, resultSet.getLong("gamma_id"));
      }
   }
}
