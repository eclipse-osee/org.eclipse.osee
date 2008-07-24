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

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TagQueueJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.eclipse.osee.framework.search.engine.ITaggerStatistics;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineTagger implements ISearchEngineTagger {
   private static int CACHE_LIMIT = 1000;

   private ExecutorService executor;
   private List<FutureTask<?>> futureTasks;
   private TaggerStatistics statistics;

   public SearchEngineTagger() {
      this.statistics = new TaggerStatistics();
      this.futureTasks = new CopyOnWriteArrayList<FutureTask<?>>();
      this.executor = Executors.newFixedThreadPool(2);
      this.executor.submit(new StartUpRunnable());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngineTagger#deleteTags(int)
    */
   @Override
   public int deleteTags(int joinQueryId) throws Exception {
      return SearchTagDataStore.deleteTags(joinQueryId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngineTagger#tagByQueueQueryId(int)
    */
   @Override
   public void tagByQueueQueryId(int queryId) {
      tagByQueueQueryId(null, queryId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngineTagger#tagByQueueQueryId(org.eclipse.osee.framework.search.engine.ITagListener, int)
    */
   @Override
   public void tagByQueueQueryId(ITagListener listener, int queryId) {
      TaggerRunnable runnable = new TaggerRunnable(queryId);
      runnable.addListener(statistics);
      if (listener != null) {
         runnable.addListener(listener);
      }
      FutureTask<Object> futureTask = new FutureTaggingTask(runnable);
      this.futureTasks.add(futureTask);
      this.executor.submit(futureTask);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngineTagger#tagFromXmlStream(java.io.InputStream)
    */
   @Override
   public void tagFromXmlStream(InputStream inputStream) throws Exception {
      Connection connection = null;
      try {
         connection = OseeDbConnection.getConnection();
         TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery();
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         xmlReader.setContentHandler(new AttributeXmlParser(connection, joinQuery));
         xmlReader.parse(new InputSource(inputStream));
         joinQuery.store(connection);
         tagByQueueQueryId(joinQuery.getQueryId());
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchTagger#getWorkersInQueue()
    */
   @Override
   public int getWorkersInQueue() {
      return futureTasks.size();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngineTagger#clearStatistics()
    */
   @Override
   public void clearStatistics() {
      this.statistics.clear();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngineTagger#getStatistics()
    */
   @Override
   public ITaggerStatistics getStatistics() {
      try {
         return this.statistics.clone();
      } catch (CloneNotSupportedException ex) {
         return TaggerStatistics.EMPTY_STATS;
      }
   }

   private final class FutureTaggingTask extends FutureTask<Object> {

      private TaggerRunnable runnable;
      private long waitStart;
      private long waitTime;

      public FutureTaggingTask(TaggerRunnable runnable) {
         super(runnable, null);
         this.runnable = runnable;
         this.waitStart = System.currentTimeMillis();
         this.waitTime = 0;
      }

      /* (non-Javadoc)
       * @see java.util.concurrent.FutureTask#run()
       */
      @Override
      public void run() {
         this.waitTime = System.currentTimeMillis() - this.waitStart;
         super.run();
      }

      /* (non-Javadoc)
       * @see java.util.concurrent.FutureTask#done()
       */
      @Override
      protected void done() {
         futureTasks.remove(this);

         //         statistics.addEntry(runnable.getGammaId(), runnable.getTotalTags(), waitTime, runnable.getProcessingTime());
      }
   }

   private final class StartUpRunnable implements Runnable {

      /* (non-Javadoc)
       * @see java.lang.Runnable#run()
       */
      @Override
      public void run() {
         Connection connection = null;
         try {
            connection = OseeDbConnection.getConnection();
            List<Integer> queries = JoinUtility.getAllTagQueueQueryIds(connection);
            for (Integer queryId : queries) {
               tagByQueueQueryId(queryId);
            }
         } catch (SQLException ex) {
            OseeLog.log(SearchEngineTagger.class, Level.SEVERE, "Error during start-up.", ex);
         } finally {
            if (connection != null) {
               try {
                  connection.close();
               } catch (SQLException ex) {
                  OseeLog.log(SearchEngineTagger.class, Level.SEVERE, "Error closing connection during start-up.", ex);
               }
            }
         }
      }
   }

   private final class AttributeXmlParser extends AbstractSaxHandler {

      private TagQueueJoinQuery joinQuery;
      private Connection connection;
      private int cacheCount;

      public AttributeXmlParser(Connection connection, TagQueueJoinQuery joinQuery) {
         this.joinQuery = joinQuery;
         this.cacheCount = 0;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#endElementFound(java.lang.String, java.lang.String, java.lang.String)
       */
      @Override
      public void endElementFound(String uri, String localName, String name) throws SAXException {
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
       */
      @Override
      public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
         if (name.equalsIgnoreCase("entry")) {
            String gammaId = attributes.getValue("gammaId");
            if (Strings.isValid(gammaId)) {
               joinQuery.add(Long.parseLong(gammaId));
               cacheCount++;
               if (cacheCount >= CACHE_LIMIT) {
                  try {
                     joinQuery.store(connection);
                     cacheCount = 0;
                  } catch (Exception ex) {
                     OseeLog.log(AttributeXmlParser.class, Level.WARNING, ex);
                  }
               }
            }
         }
      }
   }
}
