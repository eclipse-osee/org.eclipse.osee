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
package org.eclipse.osee.framework.server.admin.search;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TagQueueJoinQuery;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.eclipse.osee.framework.server.admin.Activator;

/**
 * @author Roberto E. Escobar
 */
public class TagItemWorker extends BaseCmdWorker {

   private TagListener tagListener = null;

   private Set<Long> getGammas() {
      Set<Long> toReturn = new HashSet<Long>();
      String arg = null;
      while ((arg = getCommandInterpreter().nextArgument()) != null) {
         toReturn.add(new Long(arg));
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#setExecutionAllowed(boolean)
    */
   @Override
   public void setExecutionAllowed(boolean value) {
      super.setExecutionAllowed(value);
      if (tagListener != null && !value) {
         synchronized (tagListener) {
            tagListener.notify();
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#doWork(long)
    */
   @Override
   protected void doWork(long startTime) throws Exception {
      tagListener = null;
      Set<Long> toTag = getGammas();
      if (toTag.isEmpty() != true) {
         Connection connection = null;
         try {
            connection = OseeDbConnection.getConnection();
            TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery();
            for (Long item : toTag) {
               joinQuery.add(item);
            }
            joinQuery.store(connection);

            tagListener = new TagListener(joinQuery);
            Activator.getInstance().getSearchTagger().tagByQueueQueryId(tagListener, joinQuery.getQueryId());
            synchronized (tagListener) {
               tagListener.wait();
            }
            if (tagListener.isProcessing()) {
               joinQuery.delete(connection);
            }
         } finally {
            if (connection != null) {
               connection.close();
            }
         }
      } else {
         println("No Items to Tag.");
      }
   }

   private final class TagListener implements ITagListener {
      private TagQueueJoinQuery joinQuery;
      private boolean isProcessing;

      public TagListener(TagQueueJoinQuery joinQuery) {
         this.isProcessing = true;
         this.joinQuery = joinQuery;
      }

      /**
       * @return
       */
      public boolean isProcessing() {
         return isProcessing;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.search.engine.ITagListener#onAttributeTagComplete(int, long, int, long)
       */
      @Override
      public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
         if (queryId == joinQuery.getQueryId()) {
            println(String.format("GammaId: [%d] Tags: [%d] ProcessedIn: [%d] ms", gammaId, totalTags, processingTime));
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagQueryIdTagComplete(int, long, long)
       */
      @Override
      synchronized public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
         if (queryId == joinQuery.getQueryId()) {
            this.isProcessing = false;
            this.notify();
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.search.engine.ITagListener#onAttributeAddTagEvent(int, long, java.lang.String, long)
       */
      @Override
      public void onAttributeAddTagEvent(int queryId, long gammaId, String word, long codedTag) {
         if (queryId == joinQuery.getQueryId() && isVerbose()) {
            println(String.format("QueryId: [%d] GammaId: [%d] Word: [%s] Tag: [%d]", queryId, gammaId, word, codedTag));
         }
      }

   }
}
