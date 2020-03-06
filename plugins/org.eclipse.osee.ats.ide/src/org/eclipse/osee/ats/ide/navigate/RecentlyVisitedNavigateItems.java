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
package org.eclipse.osee.ats.ide.navigate;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.util.RecentlyVisistedItem;
import org.eclipse.osee.ats.core.util.RecentlyVisitedItems;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class RecentlyVisitedNavigateItems extends XNavigateItemAction implements IWorkbenchListener {

   private static RecentlyVisitedItems visitedItems;
   private static String RECENTLY_VISITED_TOKENS = "recentlyVisitedTokens";
   private static RecentlyVisitedNavigateItems navigateItem;

   public RecentlyVisitedNavigateItems(XNavigateItem parent) {
      super(parent, "Recently Visited Workflows", AtsImage.GLOBE);
      navigateItem = this;
      PlatformUI.getWorkbench().addWorkbenchListener(this);
      refreshChildren();
   }

   private static void refreshChildren() {
      if (navigateItem != null) {
         Thread refresh = new Thread(navigateItem.getClass().getSimpleName()) {
            @Override
            public void run() {
               super.run();
               ensureLoaded();
               navigateItem.getChildren().clear();
               for (RecentlyVisistedItem item : visitedItems.getReverseVisited()) {
                  new RecentlyVisitedNavigateItem(navigateItem, item);
               }
               Displays.ensureInDisplayThread(new Runnable() {

                  @Override
                  public void run() {
                     NavigateView.getNavigateView().refresh(navigateItem);
                  }
               });
            }
         };
         refresh.start();
      }
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      ensureLoaded();
      List<ArtifactToken> workItems = new ArrayList<>();
      for (RecentlyVisistedItem item : visitedItems.getReverseVisited()) {
         IAtsWorkItem workItem = AtsClientService.get().getWorkItemService().getWorkItem(item.getIdToken());
         if (workItem != null && !AtsClientService.get().getStoreService().isDeleted(item.getIdToken())) {
            workItems.add(workItem.getStoreObject());
         }
      }
      WorldEditor.open(new WorldEditorSimpleProvider(getName(), workItems, null, tableLoadOptions));
   }

   public static void clearVisited() {
      ensureLoaded();
      visitedItems.clearVisited();
      refreshChildren();
   }

   public static void addVisited(IAtsWorkItem workItem) {
      ensureLoaded();
      visitedItems.addVisited(workItem);
      refreshChildren();
   }

   private static void ensureLoaded() {
      if (visitedItems == null) {
         try {
            String recentlyVisistedTokensJson = AtsClientService.get().getUserConfigValue(RECENTLY_VISITED_TOKENS);
            if (Strings.isValid(recentlyVisistedTokensJson)) {
               ObjectMapper mapper = JsonUtil.getMapper();
               visitedItems = mapper.readValue(recentlyVisistedTokensJson, RecentlyVisitedItems.class);
            } else {
               visitedItems = new RecentlyVisitedItems();
            }
         } catch (Exception ex) {
            AtsClientService.get().getLogger().error(
               "Unable to read visited items from Ats Config attribute on user artifact %s",
               AtsClientService.get().getUserService().getCurrentUser());
            visitedItems = new RecentlyVisitedItems();
         }
      }
   }

   public static List<RecentlyVisistedItem> getReverseItems() {
      ensureLoaded();
      return visitedItems.getReverseVisited();
   }

   @Override
   public void postShutdown(IWorkbench workbench) {
      // do nothing
   }

   @Override
   public boolean preShutdown(IWorkbench workbench, boolean forced) {
      if (!forced && visitedItems != null && !visitedItems.getReverseVisited().isEmpty()) {
         ObjectMapper mapper = JsonUtil.getMapper();
         try {
            String toStoreJson = mapper.writeValueAsString(visitedItems);
            String fromStoreJson = AtsClientService.get().getUserConfigValue(RECENTLY_VISITED_TOKENS);
            if (!toStoreJson.equals(fromStoreJson)) {
               AtsClientService.get().setUserConfigValue(RECENTLY_VISITED_TOKENS, toStoreJson);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE,
               "Unable to write visited items from Ats Config attribute on user artifact");
         }
      }
      return true;
   }
}