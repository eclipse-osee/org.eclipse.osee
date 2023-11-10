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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class RiskFactorColumnUI extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static RiskFactorColumnUI instance = new RiskFactorColumnUI();
   private final AtsApi atsApi;

   public static RiskFactorColumnUI getInstance() {
      return instance;
   }

   private RiskFactorColumnUI() {
      super("ats.taskest.risk.factor", "Risk Factor", 40, XViewerAlign.Left, true, SortDataType.String, true, "");
      atsApi = AtsApiService.get();
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RiskFactorColumnUI copy() {
      RiskFactorColumnUI newXCol = new RiskFactorColumnUI();
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
         final int result = MessageDialog.open(3, Displays.getActiveShell(), "Risk Factor", "Risk Factor", SWT.NONE,
            new String[] {"Low", "Medium", "High", "Clear", "Cancel"});
         if (result != 4) {
            Job signJob = new Job("Set Risk Factor") {

               @Override
               protected IStatus run(IProgressMonitor monitor) {
                  IAtsChangeSet changes = atsApi.createChangeSet("Set");
                  for (IAtsWorkItem workItem : workItems) {
                     if (result == 0) {
                        changes.setSoleAttributeValue(workItem, AtsAttributeTypes.RiskFactor, "Low");
                     } else if (result == 1) {
                        changes.setSoleAttributeValue(workItem, AtsAttributeTypes.RiskFactor, "Medium");
                     } else if (result == 2) {
                        changes.setSoleAttributeValue(workItem, AtsAttributeTypes.RiskFactor, "High");
                     } else if (result == 3) {
                        changes.deleteAttributes(workItem, AtsAttributeTypes.RiskFactor);
                     }
                  }
                  changes.executeIfNeeded();
                  return Status.OK_STATUS;
               }
            };
            Operations.scheduleJob(signJob, false, Job.SHORT, null);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) element;
            return atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.RiskFactor, "");
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
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
