/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsCurrentUserService;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowSearchItem;
import org.eclipse.osee.ats.ide.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.ide.world.IAtsWorldEditorItem;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.util.FrameworkEvents;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Create Saved Searches navigate item.</br>
 * Refresh on events:</br>
 * FrameworkEvents.NAVIGATE_VIEW_LOADED</br>
 * AtsTopicEvents.SAVED_SEARCHES_MODIFIED
 *
 * @author Donald G. Dunne
 */
public class SavedSearchesNavigateItem extends XNavigateItem implements EventHandler {

   private static IAtsCurrentUserService currentUserService;
   private static long SAVED_SEARCH_ID = 824378923L;

   public SavedSearchesNavigateItem() {
      // for jax-rs
      super(null, null, null);
   }

   public SavedSearchesNavigateItem(XNavigateItem parent) {
      super(parent, "Saved Action Searches", AtsImage.SEARCH);
      setId(SAVED_SEARCH_ID);
   }

   public void setAtsCurrentUserService(IAtsCurrentUserService currentUserService) {
      SavedSearchesNavigateItem.currentUserService = currentUserService;
   }

   protected void populateSavedSearchesItem(final SavedSearchesNavigateItem topSearchItem, final AtsUser currentUser, final AtsApi atsApi) {

      Job populateSavedSearchesJob = new Job("Populate Saved Searches") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            topSearchItem.getChildren().clear();
            for (IAtsWorldEditorItem worldEditorItem : AtsWorldEditorItems.getItems()) {
               for (AtsSearchWorkflowSearchItem item : worldEditorItem.getSearchWorkflowSearchItems()) {
                  ArrayList<AtsSearchData> savedSearches =
                     atsApi.getQueryService().getSavedSearches(currentUser, item.getNamespace());
                  for (AtsSearchData data : savedSearches) {
                     AtsSearchWorkflowSearchItem searchItem = item.copy();
                     searchItem.setSavedData(data);
                     SearchNavigateItem navItem = new SearchNavigateItem(topSearchItem, searchItem);
                     navItem.setName(item.getShortNamePrefix() + ": " + data.getSearchName());
                  }
               }
            }
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  NavigateView.getNavigateView().refresh(topSearchItem);
               }
            });
            return Status.OK_STATUS;
         }

      };
      Jobs.startJob(populateSavedSearchesJob, false);
   }

   public void refresh() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            SavedSearchesNavigateItem topSearchItem =
               (SavedSearchesNavigateItem) NavigateView.getNavigateView().getItem(SAVED_SEARCH_ID, true);
            populateSavedSearchesItem(topSearchItem, currentUserService.getCurrentUser(), AtsClientService.get());
         }
      });
   }

   @Override
   public void handleEvent(Event event) {
      try {
         refresh();
      } catch (Exception ex) {
         OseeLog.log(NavigateViewLinksTopicEventHandler.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String toString() {
      return String.format("%s for %s ", getClass().getSimpleName(), FrameworkEvents.NAVIGATE_VIEW_LOADED);
   }

}
