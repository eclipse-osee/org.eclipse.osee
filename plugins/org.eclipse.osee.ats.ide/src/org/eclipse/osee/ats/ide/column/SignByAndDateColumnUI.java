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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsCoreCodeColumnToken;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.config.MultiEdit;
import org.eclipse.osee.ats.api.config.Show;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XReviewedWidget;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractSignDateAndByButton;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class SignByAndDateColumnUI extends XViewerAtsCoreCodeXColumn {

   private final AttributeTypeToken dateAttrType;
   private final AttributeTypeToken byAttrType;

   public SignByAndDateColumnUI(AttributeTypeToken byAttrType, AttributeTypeToken dateAttrType) {
      super(new AtsCoreCodeColumnToken(
         WorldXViewerFactory.COLUMN_NAMESPACE + "." + byAttrType.getUnqualifiedName() + " and Date",
         byAttrType.getUnqualifiedName() + " and Date", 40, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.Yes,
         ""), AtsApiService.get());
      this.dateAttrType = dateAttrType;
      this.byAttrType = byAttrType;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public SignByAndDateColumnUI copy() {
      SignByAndDateColumnUI newXCol = new SignByAndDateColumnUI(byAttrType, dateAttrType);
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            //            XResultData rd = XReviewedWidget.checkReviewedBy(useArt);
            //            if (rd.isErrors()) {
            //               return false;
            //            }
            if (!(useArt instanceof IAtsWorkItem)) {
               return false;
            }
            IAtsWorkItem workItem = (IAtsWorkItem) useArt;
            XReviewedWidget widget = new XReviewedWidget();
            widget.setArtifact((Artifact) workItem.getStoreObject());
            widget.handleSelection();
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            xViewer.update(useArt, null);
            return true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChange(IAtsWorkItem workItem, AttributeTypeToken byAttrType,
      AttributeTypeToken dateAttrType, AtsApi atsApi) {
      return promptChange(java.util.Collections.singleton(workItem), byAttrType, dateAttrType, atsApi);
   }

   public static boolean promptChange(final Collection<IAtsWorkItem> workItems, AttributeTypeToken byAttrType,
      AttributeTypeToken dateAttrType, AtsApi atsApi) {
      try {
         for (IAtsWorkItem workItem : workItems) {
            XResultData rd = XReviewedWidget.checkReviewedBy(workItem.getArtifactToken());
            if (rd.isErrors()) {
               return false;
            }
         }
         // Ok --> 0, Cancel --> 1, Clear --> 2
         int res = MessageDialog.open(3, Displays.getActiveShell(), byAttrType.getUnqualifiedName(),
            byAttrType.getUnqualifiedName(), SWT.NONE, new String[] {"Ok", "Cancel", "Clear"});
         Job signJob = new Job("Set " + byAttrType.getUnqualifiedName()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               List<Artifact> artifacts = new ArrayList<>();
               for (IAtsWorkItem workItem : workItems) {
                  artifacts.add((Artifact) workItem.getStoreObject());
               }
               if (res == 2) {
                  XAbstractSignDateAndByButton.setSigned(artifacts, dateAttrType, byAttrType,
                     byAttrType.getUnqualifiedName(), false);
               } else if (res == 0) {
                  XAbstractSignDateAndByButton.setSigned(artifacts, dateAttrType, byAttrType,
                     byAttrType.getUnqualifiedName(), true);
               }
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(signJob, false, Job.SHORT, null);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (!(element instanceof IAtsWorkItem)) {
            return "";
         }
         IAtsWorkItem workItem = (IAtsWorkItem) element;
         return XAbstractSignDateAndByButton.getText((Artifact) workItem.getStoreObject(), dateAttrType, byAttrType);
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
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
         promptChange(workItems, byAttrType, dateAttrType, AtsApiService.get());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static SignByAndDateColumnUI valueOf(AttributeTypeToken byAttrType, AttributeTypeToken dateAttrType) {
      return new SignByAndDateColumnUI(byAttrType, dateAttrType);
   }

}
