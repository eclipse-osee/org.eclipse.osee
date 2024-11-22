/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.RecentlyVisistedItem;
import org.eclipse.osee.ats.api.util.RecentlyVisitedItems;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class RecentlyVisitedNavigateItems extends XNavigateItemAction implements IWorkbenchListener {

   private static final String NAME = "Recently Visited Workflows";
   private static RecentlyVisitedItems visitedItems;
   private static RecentlyVisitedNavigateItems topNavigateItem;

   public RecentlyVisitedNavigateItems(XNavItemCat category) {
      super(NAME, AtsImage.GLOBE, category);
      topNavigateItem = this;
      PlatformUI.getWorkbench().addWorkbenchListener(this);
      refresh();
   }

   @Override
   public void refresh() {
      // Only load once, even if refresh is called cause we cache current visited
      if (topNavigateItem != null) {
         Thread refresh = new Thread(topNavigateItem.getClass().getSimpleName()) {
            @Override
            public void run() {
               super.run();
               ensureFirstLoad();
               topNavigateItem.getChildren().clear();
               for (RecentlyVisistedItem item : visitedItems.getReverseVisited()) {
                  RecentlyVisitedNavigateItem navigateItem = new RecentlyVisitedNavigateItem(item);
                  topNavigateItem.addChild(navigateItem);
               }
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

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      ensureFirstLoad();
      List<ArtifactToken> workItems = new ArrayList<>();

      // Re-add non-deleted/non-purged
      List<RecentlyVisistedItem> reverseVisited = visitedItems.getReverseVisited();
      for (RecentlyVisistedItem item : reverseVisited) {
         IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(item.getWorkflowId());
         if (workItem != null && !AtsApiService.get().getStoreService().isDeleted(
            ArtifactId.valueOf(item.getWorkflowId()))) {
            workItems.add(workItem.getStoreObject());
         } else {
            visitedItems.getVisited().remove(item);
         }
      }

      // Open World View for non-deleted/non-purged
      WorldEditor.open(new WorldEditorSimpleProvider(getName(), workItems, null, tableLoadOptions));

      // Refresh with non-deleted/non-purged
      refresh();
   }

   public static void clearVisited() {
      ensureFirstLoad();
      visitedItems.clearVisited();
      topNavigateItem.refresh();
   }

   public static void addVisited(IAtsWorkItem workItem) {
      ensureFirstLoad();
      visitedItems.addVisited(workItem);
      if (topNavigateItem != null) {
         topNavigateItem.refresh();
      }
   }

   private static void ensureFirstLoad() {
      if (visitedItems == null) {
         try {
            AtsApi atsApi = AtsApiService.get();
            visitedItems = atsApi.getServerEndpoints().getActionEndpoint().getVisited(
               atsApi.getUserService().getCurrentUser().getArtifactId());
         } catch (Exception ex) {
            AtsApiService.get().getLogger().error("Unable to get visited items; Exception %s",
               Lib.exceptionToString(ex));
            visitedItems = new RecentlyVisitedItems();
         }
      }
   }

   @Override
   public void postShutdown(IWorkbench workbench) {
      // do nothing
   }

   @Override
   public boolean preShutdown(IWorkbench workbench, boolean forced) {
      try {
         if (visitedItems != null && !visitedItems.getReverseVisited().isEmpty()) {
            AtsApiService.get().getServerEndpoints().getActionEndpoint().storeVisited(
               AtsApiService.get().getUserService().getCurrentUser().getArtifactId(), visitedItems);
         }
      } catch (Exception ex) {
         OseeLog.log(RecentlyVisitedNavigateItem.class, Level.WARNING, "Error saving Recently Visited Items.", ex);
      }
      return true;
   }
}