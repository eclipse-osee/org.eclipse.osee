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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.note.wf.WfNoteColors;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredListDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class NotesColorColumnUI extends XViewerAtsCoreCodeXColumn {

   public static NotesColorColumnUI instance = new NotesColorColumnUI();

   public static NotesColorColumnUI getInstance() {
      return instance;
   }

   private NotesColorColumnUI() {
      super(AtsColumnTokensDefault.NotesColorColumn, AtsApiService.get());
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsCoreCodeXColumn copy() {
      NotesColorColumnUI newXCol = new NotesColorColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof IAtsWorkItem) {
         Long colorId = AtsApiService.get().getAttributeResolver().getSoleAttributeValue((IAtsWorkItem) element,
            AtsAttributeTypes.WorkflowNoteColor, null);
         if (colorId != null) {
            WfNoteColors color = WfNoteColors.getById(colorId);
            if (color == null) {
               return String.format("Error: Can't resolve color for [%s]", colorId);
            }
            return color.toStringWithId();
         }
      }
      return "";
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         IAtsWorkItem workItem = null;
         boolean modified = false;
         XViewer xViewer = null;
         if (treeItem.getData() instanceof IAtsAction) {
            if (AtsApiService.get().getWorkItemService().getTeams(workItem).size() == 1) {
               workItem = AtsApiService.get().getWorkItemService().getFirstTeam(workItem);
            } else {
               return false;
            }
         } else if (treeItem.getData() instanceof IAtsWorkItem) {
            workItem = (IAtsWorkItem) treeItem.getData();
         }

         if (workItem == null) {
            return false;
         }

         modified = promptChangeColor(Arrays.asList(workItem));
         xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();

         if (modified) {
            AtsApiService.get().getStoreService().executeChangeSet("persist assignees via alt-left-click", workItem);
         }
         if (modified) {
            xViewer.update(workItem.getStoreObject(), null);
            return true;
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeColor(IAtsWorkItem workItem) {
      return promptChangeColor(Arrays.asList(workItem));
   }

   public static boolean promptChangeColor(final Collection<? extends IAtsWorkItem> workItems) {
      FilteredListDialog<WfNoteColors> diag = new FilteredListDialog<>("Select Notes Color", "Select Notes Color");
      diag.setClearAllowed(true);
      List<WfNoteColors> sortColors = new ArrayList<>();
      sortColors.addAll(WfNoteColors.get());
      sortColors.remove(WfNoteColors.SENTINEL);
      sortColors.sort(Comparator.naturalOrder());
      diag.setInput(sortColors);
      if (diag.open() == Window.OK) {
         WfNoteColors selected = diag.getSelected();
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Notes Color");
         for (IAtsWorkItem workItem : workItems) {
            if (diag.isClearAllowedSelected() || selected.isInvalid() || selected.equals(WfNoteColors.COLOR_BLACK)) {
               changes.deleteAttributes(workItem, AtsAttributeTypes.WorkflowNoteColor);
            } else {
               changes.setSoleAttributeValue(workItem, AtsAttributeTypes.WorkflowNoteColor, selected.getId());
            }
         }
         changes.executeIfNeeded();
      }
      return true;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<IAtsWorkItem> workItems = new HashSet<>();
         for (TreeItem item : treeItems) {
            if (item.getData() instanceof IAtsWorkItem) {
               IAtsWorkItem workItem = (IAtsWorkItem) item.getData();
               if (workItem instanceof AbstractWorkflowArtifact) {
                  workItems.add(workItem);
               }
            }
         }
         if (workItems.isEmpty()) {
            AWorkbench.popup("Invalid selection for setting colors.");
            return;
         }
         promptChangeColor(workItems);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) element;
         return getWorkItemForground(workItem);
      }
      return super.getForeground(element, xCol, columnIndex);
   }

   /**
    * @return foreground color based on Notes Color attribute
    */
   public static Color getWorkItemForground(IAtsWorkItem workItem) {
      Long colorId = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem,
         AtsAttributeTypes.WorkflowNoteColor, null);
      if (colorId != null) {
         WfNoteColors color = WfNoteColors.getById(colorId);
         if (color != null) {
            Long swtId = color.getSwtId();
            if (swtId > 0) {
               try {
                  Color col = Displays.getSystemColor(swtId.intValue());
                  if (col != null) {
                     return col;
                  }
               } catch (Exception ex) {
                  // do nothing
               }
            }
         }
      }
      return null;
   }

}
