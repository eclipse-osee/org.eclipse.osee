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
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeEntryDialog;
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
      IAtsUser atsUser = AtsClientService.get().getUserService().getCurrentUser();
      List<AtsSearchData> searchDatas =
         AtsClientService.get().getQueryService().getSavedSearches(atsUser, searchItem.getNamespace());
      Collections.sort(searchDatas, new QuickSearchDataComparator());
      FilteredTreeEntryDialog dialog = new FilteredTreeEntryDialog("Save Search Options",
         "Enter New Search Name or Select Existing Search", "New Search Name", searchDatas);
      if (dialog.open() == 0) {
         AtsSearchData existingData = (AtsSearchData) dialog.getSelectedFirst();
         if (existingData == null) {
            searchItem.setSearchName(dialog.getEntryValue());
         } else {
            searchItem.setSearchName(existingData.getSearchName());
            searchItem.setSearchUuid(existingData.getUuid());
         }
         if (existingData == null && !Strings.isValid(searchItem.getSearchName())) {
            AWorkbench.popup("No Search Name entered or Existing Search Selected");
            return;
         }

         AtsSearchData data = existingData;
         if (data == null) {
            Conditions.checkNotNullOrEmpty(searchItem.getSearchName(), "New Search Name");
            String namespace = searchItem.getNamespace();
            data = AtsClientService.get().getQueryService().createSearchData(namespace, searchItem.getSearchName());
            if (searchItem.getSearchUuid() > 0) {
               data.setUuid(searchItem.getSearchUuid());
            } else if (searchItem.getSearchUuid() <= 0) {
               searchItem.setSearchUuid(data.getUuid());
            }
         }
         searchItem.loadSearchData(data);
         Conditions.checkExpressionFailOnTrue(data.getUuid() <= 0, "searchUuid must be > 0, not %d", data.getUuid());
         Conditions.checkNotNullOrEmpty(data.getSearchName(), "New Search Name");
         AtsClientService.get().getQueryService().saveSearch(AtsClientService.get().getUserService().getCurrentUser(),
            data);
         ((Artifact) AtsClientService.get().getUserService().getCurrentUser().getStoreObject()).reloadAttributesAndRelations();
         if (NavigateView.getNavigateView() != null) {
            AtsNavigateViewItems.refreshTopAtsSearchItem();
         }
         AWorkbench.popup("Search Saved");
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.SAVE);
   }
};
