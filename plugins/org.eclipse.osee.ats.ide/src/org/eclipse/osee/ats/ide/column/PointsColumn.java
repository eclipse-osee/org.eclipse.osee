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
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.PromptChangeUtil;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class PointsColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static PointsColumn instance = new PointsColumn();
   private final AtsApi atsApi;

   public static PointsColumn getInstance() {
      return instance;
   }

   private PointsColumn() {
      super("ats.wi.points", "Points", 40, XViewerAlign.Left, false, SortDataType.Integer, true, "");
      atsApi = AtsApiService.get();
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PointsColumn copy() {
      PointsColumn newXCol = new PointsColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsApiService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = AtsApiService.get().getQueryServiceIde().getArtifact(
                     AtsApiService.get().getWorkItemService().getFirstTeam(useArt));
               } else {
                  return false;
               }
            }

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

   public static boolean promptChangePoints(Collection<IAtsWorkItem> workItems, AttributeTypeToken pointsAttrType, AtsApi atsApi) {
      if (pointsAttrType == AtsAttributeTypes.PointsNumeric) {
         EntryDialog dialog = new EntryDialog("Enter Points", "Enter Points");
         if (dialog.open() == Window.OK) {
            String entry = dialog.getEntry();
            if (org.eclipse.osee.framework.jdk.core.util.Strings.isNumeric(entry)) {
               try {
                  double points = Double.valueOf(entry);
                  IAtsChangeSet changes = atsApi.createChangeSet("Set Points");
                  for (IAtsWorkItem workItem : workItems) {
                     changes.setSoleAttributeValue(workItem, pointsAttrType, points);
                  }
                  changes.executeIfNeeded();
                  return true;
               } catch (Exception ex) {
                  // do nothing
               }
            } else if (Strings.isInValid(entry)) {
               IAtsChangeSet changes = atsApi.createChangeSet("Set Points");
               for (IAtsWorkItem workItem : workItems) {
                  changes.deleteAttributes(workItem, pointsAttrType);
               }
            }
         }
      } else {
         PromptChangeUtil.promptChangeAttribute(Collections.castAll(workItems), pointsAttrType, true);
      }
      return false;
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
         promptChangePoints(workItems, pointsAttrType, atsApi);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
