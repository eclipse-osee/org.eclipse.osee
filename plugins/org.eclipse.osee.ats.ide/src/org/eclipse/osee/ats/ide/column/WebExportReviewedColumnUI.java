/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class WebExportReviewedColumnUI extends XViewerAtsCoreCodeXColumn {

   public static WebExportReviewedColumnUI instance = new WebExportReviewedColumnUI();
   private final AtsApi atsApi;

   public static WebExportReviewedColumnUI getInstance() {
      return instance;
   }

   private WebExportReviewedColumnUI() {
      super(AtsColumnTokensDefault.WebExportReviewed, AtsApiService.get());
      atsApi = AtsApiService.get();
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WebExportReviewedColumnUI copy() {
      WebExportReviewedColumnUI newXCol = new WebExportReviewedColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) treeItem.getData();
            return promptChange(Arrays.asList(workItem), atsApi);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChange(IAtsWorkItem workItem, AtsApi atsApi) {
      return promptChange(java.util.Collections.singleton(workItem), atsApi);
   }

   public static boolean promptChange(final Collection<IAtsWorkItem> workItems, AtsApi atsApi) {
      try {
         IAtsWorkItem workItem = workItems.iterator().next();
         List<String> options = new ArrayList<>();
         for (String tag : workItem.getParentTeamWorkflow().getTeamDefinition().getTags()) {
            if (tag.startsWith("WebExportReviewOptions")) {
               String exportOptions = tag.replaceFirst("WebExportReviewOptions=", "");
               for (String option : exportOptions.split(";")) {
                  options.add(option);
               }
            }
         }
         if (options.isEmpty()) {
            AWorkbench.popup(
               "No Options Configured for Team " + workItem.getParentTeamWorkflow().getTeamDefinition().toStringWithId());
            return false;
         }
         FilteredCheckboxTreeDialog<String> dialog = new FilteredCheckboxTreeDialog<String>("Select Reviewed",
            "Select Reviewed", new ArrayTreeContentProvider(), new StringLabelProvider(), null, true);
         dialog.setInput(options);
         if (dialog.open() != Window.CANCEL) {
            IAtsChangeSet changes = atsApi.createChangeSet("Set");

            boolean clear = dialog.isClearSelected();
            if (clear) {
               options.removeAll(dialog.getChecked());
               for (IAtsWorkItem wi : workItems) {
                  changes.setAttributeValues(wi, AtsAttributeTypes.WebExportReviewed, Collections.castAll(options));
               }
            } else {
               for (IAtsWorkItem wi : workItems) {
                  for (String selected : dialog.getChecked()) {
                     if (!AtsApiService.get().getAttributeResolver().getAttributesToStringList(workItem,
                        AtsAttributeTypes.WebExportReviewed).contains(selected)) {
                        changes.addAttribute(wi, AtsAttributeTypes.WebExportReviewed, selected);
                     }
                  }
               }
            }
            changes.executeIfNeeded();
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
      }
      return false;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof IAtsWorkItem) {
         return AtsApiService.get().getAttributeResolver().getAttributesToStringUniqueList((IAtsWorkItem) element,
            AtsAttributeTypes.WebExportReviewed, ", ");
      }
      return "";
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<IAtsWorkItem> workItems = new HashSet<>();
         for (TreeItem item : treeItems) {
            if (item.getData() instanceof IAtsWorkItem) {
               Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
               if (art instanceof IAtsWorkItem) {
                  workItems.add((IAtsWorkItem) art);
               }
            }
         }
         promptChange(workItems, atsApi);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
