/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowSearchItem;
import org.eclipse.osee.ats.ide.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.ide.world.IAtsWorldEditorItem;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Create Saved Searches navigate item.</br>
 *
 * @author Donald G. Dunne
 */
public class SavedActionSearchNavigateItem extends XNavigateItem {

   private static SavedActionSearchNavigateItem topNavigateItem;

   public SavedActionSearchNavigateItem() {
      // for jax-rs
      super(null, null, null);
   }

   public SavedActionSearchNavigateItem(XNavigateItem parent) {
      super(parent, "Saved Action Searches", AtsImage.SEARCH);
      topNavigateItem = this;
      refresh();
   }

   @Override
   public void refresh() {
      if (topNavigateItem != null) {
         Thread refresh = new Thread(topNavigateItem.getClass().getSimpleName()) {
            @Override
            public void run() {
               super.run();
               topNavigateItem.getChildren().clear();
               load();
               if (refresher != null) {
                  Displays.ensureInDisplayThread(new Runnable() {

                     @Override
                     public void run() {
                        refresher.refresh(topNavigateItem);
                     }
                  });
               }
            }
         };
         refresh.start();
      }
   }

   public static void refreshItems() {
      topNavigateItem.refresh();
   }

   private void load() {
      try {
         // If current user and not first load, reload user to get latest
         AtsApi atsApi = AtsApiService.get();
         AtsUser currentUser = atsApi.getConfigService().getCurrentUserByLoginId();

         if (topNavigateItem.getChildren() != null) {
            topNavigateItem.getChildren().clear();
         }
         Set<Long> ids = new HashSet<Long>();
         for (IAtsWorldEditorItem worldEditorItem : AtsWorldEditorItems.getItems()) {
            for (AtsSearchWorkflowSearchItem item : worldEditorItem.getSearchWorkflowSearchItems()) {
               ArrayList<AtsSearchData> savedSearches =
                  atsApi.getQueryService().getSavedSearches(currentUser, item.getNamespace());
               for (AtsSearchData data : savedSearches) {
                  if (!ids.contains(data.getId())) {
                     AtsSearchWorkflowSearchItem searchItem = item.copy();
                     searchItem.setSavedData(data);
                     SearchNavigateItem navItem = new SearchNavigateItem(topNavigateItem, searchItem);
                     navItem.setName(item.getShortNamePrefix() + ": " + data.getSearchName());
                     ids.add(data.getId());
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SavedActionSearchNavigateItem.class, Level.WARNING, "Error populating searches", ex);
      }
   }

}
