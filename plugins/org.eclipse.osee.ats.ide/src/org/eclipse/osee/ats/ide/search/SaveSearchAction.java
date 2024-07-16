/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.search;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.SavedActionSearchNavigateItem;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public final class SaveSearchAction extends Action {

   private final AtsSearchWorkflowSearchItem searchItem;

   public SaveSearchAction(AtsSearchWorkflowSearchItem searchItem) {
      this.searchItem = searchItem;
   }

   @Override
   public String getText() {
      return "Save Search";
   }

   @Override
   public String getToolTipText() {
      return "Enter search criteria and select to save";
   }

   @Override
   public void run() {
      EntryDialog dialog = new EntryDialog("Save Search", "Save Search?\n\n(edit to change Search Name)");
      dialog.setEntry(searchItem.getSearchName());
      if (dialog.open() == Window.OK) {
         if (!Strings.isValid(dialog.getEntry())) {
            AWorkbench.popup("Invalid Search Name");
            return;
         }
         AtsSearchData data = AtsApiService.get().getQueryService().createSearchData(searchItem.getNamespace(),
            searchItem.getSearchName());
         searchItem.loadSearchData(data);
         data.setSearchName(dialog.getEntry());
         if (data.getId() <= 0) {
            data.setId(Lib.generateArtifactIdAsInt());
         }
         Conditions.checkExpressionFailOnTrue(data.getId() <= 0, "searchId must be > 0, not %d", data.getId());
         Conditions.checkNotNullOrEmpty(data.getSearchName(), "Search Name");
         AtsApiService.get().getQueryService().saveSearch(data);

         SavedActionSearchNavigateItem.refreshItems();

         AWorkbench.popupf("Search [%s] Saved", data.getSearchName());
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.SAVE);
   }
};
