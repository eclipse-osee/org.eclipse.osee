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
package org.eclipse.osee.ats.search;

import java.util.Collections;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.navigate.NavigateViewItems;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public final class DeleteSearchAction extends Action {

   private final AtsSearchWorkflowSearchItem searchItem;

   public DeleteSearchAction(AtsSearchWorkflowSearchItem searchItem) {
      this.searchItem = searchItem;
   }

   @Override
   public String getText() {
      return "Delete Saved Search";
   }

   @Override
   public void run() {
      List<AtsSearchData> searchDatas = AtsClientService.get().getQueryService().getSavedSearches(
         AtsClientService.get().getUserService().getCurrentUser(), searchItem.getNamespace());
      Collections.sort(searchDatas, new QuickSearchDataComparator());
      FilteredTreeDialog dialog = new FilteredTreeDialog("Delete Saved Search", "Select Search to Delete",
         new ArrayTreeContentProvider(), new StringLabelProvider());
      dialog.setInput(searchDatas);

      if (dialog.open() == 0) {
         AtsSearchData selected = (AtsSearchData) dialog.getSelectedFirst();
         AtsClientService.get().getQueryService().removeSearch(AtsClientService.get().getUserService().getCurrentUser(),
            selected);
         ((Artifact) AtsClientService.get().getUserService().getCurrentUser().getStoreObject()).reloadAttributesAndRelations();
         if (NavigateView.getNavigateView() != null) {
            NavigateViewItems.refreshTopAtsSearchItem();
         }
         AWorkbench.popup("Search Deleted");
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DELETE);
   }
};
