/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.search.navigate;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class ImportSavedSearchNavigateItem extends XNavigateItem {

   AtsApi atsApi;

   public ImportSavedSearchNavigateItem() {
      super("Import Saved Search", FrameworkImage.IMPORT, SavedActionSearchNavigateItem.SAVED_ACTION_SEARCHES);
      atsApi = AtsApiService.get();
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      EntryDialog dialog = new EntryDialog(getName(), "Paste Saved Search json");
      dialog.setFillVertically(true);
      try {
         if (dialog.open() == Window.OK) {
            String json = dialog.getEntry();
            AtsSearchData data = atsApi.getSearchDataProvider("ats.search").fromJson("ats.search", json);
            if (data != null) {
               data.setId(Lib.generateArtifactIdAsInt());
               AtsApiService.get().getQueryService().saveSearch(data);

               SavedActionSearchNavigateItem.refreshItems();

               AWorkbench.popupf("Search [%s] Saved", data.getSearchName());
            }
         }
      } catch (Exception ex) {
         ResultsEditor.open(getName(), getName() + " - Error",
            "Error importing saved search: " + Lib.exceptionToString(ex));
      }
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(CoreUserGroups.Everyone);
   }

}
