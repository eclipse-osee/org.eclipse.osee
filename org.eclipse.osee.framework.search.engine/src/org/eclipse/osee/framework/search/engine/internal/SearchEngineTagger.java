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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
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

   private ExecutorService executor;
   private List<FutureTask<?>> futureTasks;
   private TaggerStatistics statistics;

   public SearchEngineTagger() {
      this.statistics = new TaggerStatistics();
      this.futureTasks = new CopyOnWriteArrayList<FutureTask<?>>();
      this.executor = Executors.newFixedThreadPool(2);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngineTagger#tagAttribute(org.eclipse.osee.framework.search.engine.ITagDoneListener, long)
    */
   @Override
   public void tagAttribute(ITagListener listener, long gammaId) {
      FutureTask<Object> futureTask = new FutureTaggingTask(new TaggerRunnable(listener, gammaId));
      this.futureTasks.add(futureTask);
      this.executor.submit(futureTask);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchTagger#tagAttribute(long)
    */
   @Override
   public void tagAttribute(long gammaId) {
      tagAttribute(null, gammaId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchTagger#tagFromXmlStream(java.io.InputStream)
    */
   @Override
   public void tagFromXmlStream(InputStream inputStream) {
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         xmlReader.setContentHandler(new AttributeXmlParser());
         xmlReader.parse(new InputSource(inputStream));
      } catch (SAXException ex) {
         ex.printStackTrace();
      } catch (IOException ex) {
         ex.printStackTrace();
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
   private final class AttributeXmlParser extends AbstractSaxHandler {

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
               tagAttribute(Long.parseLong(gammaId));
            }
         }
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
         statistics.addEntry(runnable.getGammaId(), runnable.getTotalTags(), waitTime, runnable.getProcessingTime());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngineTagger#deleteTags(int)
    */
   @Override
   public int deleteTags(int joinQueryId) throws Exception {
      return SearchTagDataStore.deleteTags(joinQueryId);
   }
}
