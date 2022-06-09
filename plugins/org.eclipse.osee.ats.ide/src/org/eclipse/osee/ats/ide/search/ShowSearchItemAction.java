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

import java.io.File;
import java.io.IOException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public final class ShowSearchItemAction extends Action {

   private final AtsSearchWorkflowSearchItem searchItem;
   private final AtsApi atsApi;

   public ShowSearchItemAction(AtsSearchWorkflowSearchItem searchItem) {
      this.searchItem = searchItem;
      atsApi = AtsApiService.get();
   }

   @Override
   public String getText() {
      return "Show Search Item";
   }

   @Override
   public String getToolTipText() {
      return "Select to show Search Item json";
   }

   @Override
   public void run() {
      AtsSearchData data =
         AtsApiService.get().getQueryService().createSearchData(searchItem.getNamespace(), searchItem.getSearchName());
      searchItem.loadSearchData(data);
      String json = AtsApiService.get().jaxRsApi().toJson(data);
      ResultsEditor.open("json", String.format("Search Item [%s]", data.getSearchName()), json);

      if (atsApi.getUserService().isAtsAdmin()) {
         File file = OseeData.getFile("SearchItem.json");
         try {
            Lib.writeStringToFile(json, file);
            Program.launch(file.getAbsolutePath());
         } catch (IOException ex) {
            // do nothing
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.QUESTION);
   }
};
