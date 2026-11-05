/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.Points;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.PromptChangeUtil;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.validate.XXDoubleValidator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.NoOpViewerComparator;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class PointsColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PointsColumnUI instance = new PointsColumnUI();
   private final AtsApi atsApi;

   public static PointsColumnUI getInstance() {
      return instance;
   }

   private PointsColumnUI() {
      super("ats.wi.points", "Points", 40, XViewerAlign.Left, false, SortDataType.Integer, true, "");
      atsApi = AtsApiService.get();
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PointsColumnUI copy() {
      PointsColumnUI newXCol = new PointsColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            if (!(useArt instanceof IAtsWorkItem)) {
               return false;
            }

            IAtsWorkItem workItem = (IAtsWorkItem) useArt;

            AttributeTypeToken pointsAttrType = atsApi.getAgileService().getPointsAttrType(workItem);
            if (!atsApi.getAttributeResolver().isAttributeTypeValid(workItem, pointsAttrType)) {
               return false;
            }

            boolean modified = promptChangePoints(workItem, pointsAttrType, atsApi);
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified) {
               xViewer.update(useArt, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangePoints(IAtsWorkItem workItem, AttributeTypeToken pointsAttrType, AtsApi atsApi) {
      return promptChangePoints(java.util.Collections.singleton(workItem), pointsAttrType, atsApi);
   }

   public static boolean promptChangePoints(Collection<IAtsWorkItem> workItems, AttributeTypeToken pointsAttrType,
      AtsApi atsApi) {
      if (pointsAttrType.equals(AtsAttributeTypes.PointsNumeric)) {
         return promptChangePointsNumeric(workItems, pointsAttrType, atsApi);
      }
      if (pointsAttrType.equals(AtsAttributeTypes.PointsEnum)) {
         return promptChangePointsEnum(workItems, pointsAttrType, atsApi);
      }
      return PromptChangeUtil.promptChangeAttribute(Collections.castAll(workItems), pointsAttrType, true);
   }

   /**
    * Numeric points are a float (partial points allowed), so only a valid double or blank (clear) is accepted.
    */
   private static boolean promptChangePointsNumeric(Collection<IAtsWorkItem> workItems,
      AttributeTypeToken pointsAttrType, AtsApi atsApi) {
      EntryDialog dialog = new EntryDialog("Enter Points", "Enter Points");
      dialog.setValidator(XXDoubleValidator.instance);
      if (dialog.open() != Window.OK) {
         return false;
      }
      String entry = dialog.getEntry();
      IAtsChangeSet changes = atsApi.createChangeSet("Set Points");
      if (Strings.isNumeric(entry)) {
         double points = Double.parseDouble(entry);
         for (IAtsWorkItem workItem : workItems) {
            changes.setSoleAttributeValue(workItem, pointsAttrType, points);
         }
      } else if (Strings.isInvalidOrBlank(entry)) {
         for (IAtsWorkItem workItem : workItems) {
            changes.deleteAttributes(workItem, pointsAttrType);
         }
      } else {
         return false;
      }
      changes.executeIfNeeded();
      return true;
   }

   /**
    * Enum points are a closed set defined by the Work Definition (or the default Points scale), so the user selects
    * rather than types. Same source as XXPointsEnumWidget.getSelectable().
    */
   private static boolean promptChangePointsEnum(Collection<IAtsWorkItem> workItems, AttributeTypeToken pointsAttrType,
      AtsApi atsApi) {
      IAtsWorkItem firstWorkItem = workItems.iterator().next();
      Collection<String> selectable = firstWorkItem.getWorkDefinition().getPointsEnums();
      if (selectable.isEmpty()) {
         selectable = Points.getValuesStr();
      }
      // FilteredListDialog always sorts alphabetically (1, 13, 150, 2...); points scale must keep its order
      FilteredTreeDialog dialog = new FilteredTreeDialog("Select Points", "Select Points",
         new ArrayTreeContentProvider(), new StringLabelProvider(), NoOpViewerComparator.instance, true);
      dialog.setSingleSelect();
      dialog.setInput(selectable);
      dialog.open();
      String selected = dialog.getSelectedFirst();
      if (!(dialog.isOkSelected() && Strings.isValid(selected)) && !dialog.isClearSelected()) {
         return false;
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Set Points");
      for (IAtsWorkItem workItem : workItems) {
         if (dialog.isClearSelected()) {
            changes.deleteAttributes(workItem, pointsAttrType);
         } else {
            changes.setSoleAttributeValue(workItem, pointsAttrType, selected);
         }
      }
      changes.executeIfNeeded();
      return true;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsWorkItem) {
            return atsApi.getAgileService().getPointsStr((IAtsWorkItem) element);
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         AttributeTypeToken pointsAttrType = null;
         Set<IAtsWorkItem> workItems = new HashSet<>();
         for (TreeItem item : treeItems) {
            if (item.getData() instanceof IAtsWorkItem) {
               IAtsWorkItem workItem = (IAtsWorkItem) item.getData();
               AttributeTypeToken ptsAttrType = atsApi.getAgileService().getPointsAttrType(workItem);
               if (pointsAttrType == null) {
                  pointsAttrType = ptsAttrType;
               } else if (!pointsAttrType.equals(ptsAttrType)) {
                  throw new OseeArgumentException(
                     "Can not change points attribute for workflows of different attr types");
               }
               Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
               if (art instanceof IAtsWorkItem) {
                  workItems.add((IAtsWorkItem) art);
               }
            }
         }
         if (pointsAttrType != null) {
            promptChangePoints(workItems, pointsAttrType, atsApi);
         }
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
