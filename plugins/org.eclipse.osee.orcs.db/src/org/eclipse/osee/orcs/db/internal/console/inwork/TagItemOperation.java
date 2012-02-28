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
package org.eclipse.osee.orcs.db.internal.console.inwork;

/**
 * @author Roberto E. Escobar
 */
public class TagItemOperation {
   //   
   //   
   //   private final Set<Long> gammas;
   //   private TagListener tagListener = null;
   //
   //   public TagItemOperation(OperationLogger logger, Set<Long> gammas) {
   //      super("Tag Individual Items", Activator.PLUGIN_ID, logger);
   //      this.gammas = gammas;
   //   }
   //
   //   private final class TagListener extends TagListenerAdapter {
   //      private int joinQuery;
   //      private boolean isProcessing;
   //
   //      public TagListener() {
   //         this.isProcessing = true;
   //         this.joinQuery = -1;
   //      }
   //
   //      @Override
   //      public void onTagQueryIdSubmit(int queryId) {
   //         joinQuery = queryId;
   //      }
   //
   //      public boolean isProcessing() {
   //         return isProcessing;
   //      }
   //
   //      @Override
   //      public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
   //         if (queryId == joinQuery) {
   //            logf("GammaId: [%d] Tags: [%d] Processed In: [%d] ms", gammaId, totalTags, processingTime);
   //         }
   //      }
   //
   //      @Override
   //      synchronized public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
   //         if (queryId == joinQuery) {
   //            this.isProcessing = false;
   //            this.notify();
   //         }
   //      }
   //
   //      @Override
   //      public void onAttributeAddTagEvent(int queryId, long gammaId, String word, long codedTag) {
   //         if (queryId == joinQuery) {
   //            logf("QueryId: [%d] GammaId: [%d] Word: [%s] Tag: [%d]", queryId, gammaId, word, codedTag);
   //         }
   //      }
   //
   //   }
   //
   //   @Override
   //   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
   //      tagListener = null;
   //      final Set<Long> toTag = gammas;
   //      if (!toTag.isEmpty()) {
   //         TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery();
   //         for (Long item : toTag) {
   //            joinQuery.add(item);
   //         }
   //         joinQuery.store();
   //
   //         tagListener = new TagListener();
   //         Activator.getSearchTagger().tagByQueueQueryId(tagListener, joinQuery.getQueryId());
   //         synchronized (tagListener) {
   //            try {
   //               tagListener.wait();
   //            } catch (InterruptedException ex) {
   //               OseeLog.log(Activator.class, Level.SEVERE, ex);
   //            }
   //         }
   //         if (tagListener.isProcessing()) {
   //            joinQuery.delete();
   //         }
   //
   //      } else {
   //         log("No Items to Tag.");
   //      }
   //   }
}
