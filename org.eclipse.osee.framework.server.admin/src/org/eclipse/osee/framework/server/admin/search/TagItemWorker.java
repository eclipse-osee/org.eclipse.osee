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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.TagQueueJoinQuery;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.TagListenerAdapter;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;

/**
 * @author Roberto E. Escobar
 */
public class TagItemWorker extends BaseServerCommand {

   private TagListener tagListener = null;

   protected TagItemWorker() {
      super("Tag Individual Items");
   }

   private Set<Long> getGammas() {
      Set<Long> toReturn = new HashSet<Long>();
      String arg = null;
      while ((arg = getCommandInterpreter().nextArgument()) != null) {
         toReturn.add(new Long(arg));
      }
      return toReturn;
   }

   @Override
   public void setExecutionAllowed(boolean value) {
      super.setExecutionAllowed(value);
      if (tagListener != null && !value) {
         synchronized (tagListener) {
            tagListener.notify();
         }
      }
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      tagListener = null;
      final Set<Long> toTag = getGammas();
      if (toTag.isEmpty() != true) {
         TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery();
         for (Long item : toTag) {
            joinQuery.add(item);
         }
         joinQuery.store();

         tagListener = new TagListener();
         Activator.getInstance().getSearchTagger().tagByQueueQueryId(tagListener, joinQuery.getQueryId());
         synchronized (tagListener) {
            try {
               tagListener.wait();
            } catch (InterruptedException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         if (tagListener.isProcessing()) {
            joinQuery.delete();
         }

      } else {
         println("No Items to Tag.");
      }
   }

   private final class TagListener extends TagListenerAdapter {
      private int joinQuery;
      private boolean isProcessing;

      public TagListener() {
         this.isProcessing = true;
         this.joinQuery = -1;
      }

      @Override
      public void onTagQueryIdSubmit(int queryId) {
         joinQuery = queryId;
      }

      public boolean isProcessing() {
         return isProcessing;
      }

      @Override
      public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
         if (queryId == joinQuery) {
            println(String.format("GammaId: [%d] Tags: [%d] Processed In: [%d] ms", gammaId, totalTags, processingTime));
         }
      }

      @Override
      synchronized public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
         if (queryId == joinQuery) {
            this.isProcessing = false;
            this.notify();
         }
      }

      @Override
      public void onAttributeAddTagEvent(int queryId, long gammaId, String word, long codedTag) {
         if (queryId == joinQuery && isVerbose()) {
            println(String.format("QueryId: [%d] GammaId: [%d] Word: [%s] Tag: [%d]", queryId, gammaId, word, codedTag));
         }
      }

   }
}
