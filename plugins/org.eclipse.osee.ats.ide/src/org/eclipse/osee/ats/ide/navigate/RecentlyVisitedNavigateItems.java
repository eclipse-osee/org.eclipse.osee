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
import org.eclipse.osee.ats.core.util.RecentlyVisistedItem;
import org.eclipse.osee.ats.core.util.RecentlyVisitedItems;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
   private static String RECENTLY_VISITED_TOKENS = "recentlyVisitedTokens";
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
      for (RecentlyVisistedItem item : visitedItems.getReverseVisited()) {
         IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(item.getIdToken());
         if (workItem != null && !AtsApiService.get().getStoreService().isDeleted(item.getIdToken())) {
            workItems.add(workItem.getStoreObject());
         }
      }
      WorldEditor.open(new WorldEditorSimpleProvider(getName(), workItems, null, tableLoadOptions));
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
            String recentlyVisistedTokensJson = atsApi.getUserConfigValue(RECENTLY_VISITED_TOKENS);
            if (Strings.isValid(recentlyVisistedTokensJson)) {
               visitedItems = atsApi.jaxRsApi().readValue(recentlyVisistedTokensJson, RecentlyVisitedItems.class);
            } else {
               visitedItems = new RecentlyVisitedItems();
            }
         } catch (Exception ex) {
            AtsApiService.get().getLogger().error(
               "Unable to read visited items from Ats Config attribute on user artifact %s; Exception %s",
               AtsApiService.get().getUserService().getCurrentUser(), Lib.exceptionToString(ex));
            visitedItems = new RecentlyVisitedItems();
         }
      }
   }

   public static List<RecentlyVisistedItem> getReverseItems() {
      ensureFirstLoad();
      return visitedItems.getReverseVisited();
   }

   @Override
   public void postShutdown(IWorkbench workbench) {
      // do nothing
   }

   @Override
   public boolean preShutdown(IWorkbench workbench, boolean forced) {
      try {
         if (visitedItems != null && !visitedItems.getReverseVisited().isEmpty()) {
            String toStoreJson = AtsApiService.get().jaxRsApi().toJson(visitedItems);
            String fromStoreJson = AtsApiService.get().getUserConfigValue(RECENTLY_VISITED_TOKENS);
            if (!toStoreJson.equals(fromStoreJson)) {
               AtsApiService.get().setUserConfigValue(RECENTLY_VISITED_TOKENS, toStoreJson);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(RecentlyVisitedNavigateItem.class, Level.WARNING, "Error saving recently visited items.", ex);
      }
      return true;
   }
}