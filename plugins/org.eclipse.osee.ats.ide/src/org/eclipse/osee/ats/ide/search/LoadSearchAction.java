/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.search;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Donald G. Dunne
 */
public final class LoadSearchAction extends Action implements IMenuCreator {

   private final AtsSearchWorkflowSearchItem searchItem;
   private Menu fMenu;

   public LoadSearchAction(AtsSearchWorkflowSearchItem searchItem) {
      this.searchItem = searchItem;
      setMenuCreator(this);
   }

   @Override
   public String getText() {
      return "Load Saved Search";
   }

   @Override
   public void run() {
      List<AtsSearchData> searchDatas = AtsClientService.get().getQueryService().getSavedSearches(
         AtsClientService.get().getUserService().getCurrentUser(), searchItem.getNamespace());
      Collections.sort(searchDatas, new QuickSearchDataComparator());
      FilteredTreeDialog dialog = new FilteredTreeDialog("Load Saved Search", "Select Search",
         new ArrayTreeContentProvider(), new StringLabelProvider());
      dialog.setInput(searchDatas);

      if (dialog.open() == 0) {
         AtsSearchData selected = (AtsSearchData) dialog.getSelectedFirst();
         searchItem.loadWidgets(selected);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.LOAD);
   }

   @Override
   public Menu getMenu(Control parent) {
      if (fMenu != null) {
         fMenu.dispose();
      }

      fMenu = new Menu(parent);

      try {
         List<AtsSearchData> searchDatas = AtsClientService.get().getQueryService().getSavedSearches(
            AtsClientService.get().getUserService().getCurrentUser(), searchItem.getNamespace());
         Collections.sort(searchDatas, new QuickSearchDataComparator());

         for (AtsSearchData data : searchDatas) {
            addActionToMenu(fMenu, new LoadSearchItemAction(data, searchItem));
         }
         if (searchDatas.isEmpty()) {
            setToolTipText("No Searches Saved");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Unable to load saved search data", ex);
      }
      return fMenu;
   }

   @Override
   public void dispose() {
      if (fMenu != null) {
         fMenu.dispose();
         fMenu = null;
      }
   }

   @Override
   public Menu getMenu(Menu parent) {
      return null;
   }

   protected void addActionToMenu(Menu parent, Action action) {
      ActionContributionItem item = new ActionContributionItem(action);
      item.fill(parent, -1);
   }

   /**
    * Get's rid of the menu, because the menu hangs on to * the searches, etc.
    */
   void clear() {
      dispose();
   }

};
