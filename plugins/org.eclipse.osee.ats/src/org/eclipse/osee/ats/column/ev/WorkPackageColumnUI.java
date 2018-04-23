/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column.ev;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.column.WorkPackageFilterTreeDialog;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.ev.WorkPackageCollectionProvider;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Provides column to show selected Work Package and provides for the selection of valid Work Packages configured for
 * Team Def and AIs
 *
 * @author Donald G. Dunne
 */
public class WorkPackageColumnUI extends XViewerAtsColumn implements IMultiColumnEditProvider, IXViewerValueColumn, IAltLeftClickProvider {

   public static WorkPackageColumnUI instance = new WorkPackageColumnUI();

   public static WorkPackageColumnUI getInstance() {
      return instance;
   }

   private WorkPackageColumnUI() {
      super(AtsColumnId.ActivityId.getId(), "Work Package (EV)", 80, XViewerAlign.Left, false, SortDataType.String,
         true,
         "Provides Work Package dialog from the configured Work Packages related to the selected workflow's Team Definitions.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkPackageColumnUI copy() {
      WorkPackageColumnUI newXCol = new WorkPackageColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      if (element instanceof IAtsObject) {
         result = AtsClientService.get().getColumnService().getColumn(AtsColumnId.ActivityId).getColumnText(
            (IAtsObject) element);
      }
      return result;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      boolean modified = false;
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact selectedArt = (Artifact) treeItem.getData();
            AbstractWorkflowArtifact useAwa = null;
            if (selectedArt instanceof IAtsAction && AtsClientService.get().getWorkItemService().getTeams(
               selectedArt).size() == 1) {
               useAwa = (AbstractWorkflowArtifact) AtsClientService.get().getWorkItemService().getFirstTeam(
                  selectedArt).getStoreObject();
            } else if (selectedArt instanceof AbstractWorkflowArtifact) {
               useAwa = (AbstractWorkflowArtifact) selectedArt;
            }
            if (useAwa != null) {
               modified = promptChangeActivityId(useAwa, false);
               if (modified) {
                  ((XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer()).update(useAwa, null);
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return modified;
   }

   public static boolean promptChangeActivityId(AbstractWorkflowArtifact teamWf, boolean persist) {
      return promptChangeActivityIds(Arrays.asList(teamWf));
   }

   private static boolean promptChangeActivityIds(final Collection<? extends AbstractWorkflowArtifact> awas) {
      boolean modified = false;
      Set<IAtsWorkPackage> commonWorkPackageOptions = new HashSet<>();
      Set<IAtsWorkPackage> uniqueWorkPackageOptions = new HashSet<>();
      Result result = getConfiguredWorkPackageOptions(awas, commonWorkPackageOptions, uniqueWorkPackageOptions);
      if (result.isFalse()) {
         AWorkbench.popup("Options Invalid", result.getText());
      } else {
         WorkPackageFilterTreeDialog dialog = new WorkPackageFilterTreeDialog("Select Work Package",
            getMessage(awas, commonWorkPackageOptions, uniqueWorkPackageOptions),
            new WorkPackageCollectionProvider(commonWorkPackageOptions));
         dialog.setInput();
         if (dialog.open() == Window.OK) {
            boolean removeFromWorkPackage = dialog.isRemoveFromWorkPackage();
            IAtsWorkPackage workPackage = dialog.getSelection();
            if (removeFromWorkPackage) {
               AtsClientService.get().getEarnedValueService().removeWorkPackage(workPackage, Collections.castAll(awas));
            } else {
               AtsClientService.get().getEarnedValueService().setWorkPackage(workPackage, Collections.castAll(awas));
            }
            modified = true;
         }
      }
      return modified;
   }

   private static String getMessage(Collection<? extends AbstractWorkflowArtifact> awas, Set<IAtsWorkPackage> commonWorkPackageOptions, Set<IAtsWorkPackage> uniqueWorkPackageOptions) {
      String message = "Select Work Package";
      if (awas.size() > 1) {
         message = String.format(
            "Select Work Package Option from %d common option(s) out of %d unique options from selected Work Items",
            commonWorkPackageOptions.size(), uniqueWorkPackageOptions.size());
      }
      return message;
   }

   private static Result getConfiguredWorkPackageOptions(final Collection<? extends AbstractWorkflowArtifact> awas, Set<IAtsWorkPackage> workPackageOptions, Set<IAtsWorkPackage> uniqueWorkPackageOptions) {
      Result result = null;
      for (AbstractWorkflowArtifact teamWf : awas) {
         Collection<IAtsWorkPackage> options =
            AtsClientService.get().getEarnedValueService().getWorkPackageOptions(teamWf);
         uniqueWorkPackageOptions.addAll(options);
         if (options.isEmpty()) {
            result = new Result(false, "One or more selected Work Items had no Work Package Options configured.");
            break;
         }
         if (workPackageOptions.isEmpty()) {
            workPackageOptions.addAll(options);
         } else {
            ArrayList<IAtsWorkPackage> setIntersection = Collections.setIntersection(options, workPackageOptions);
            workPackageOptions.clear();
            workPackageOptions.addAll(setIntersection);
         }
      }
      if (result == null) {
         if (workPackageOptions.isEmpty()) {
            result = new Result(false, "Found no common Work Package Options from selected Work Items.");
         } else {
            result = Result.TrueResult;
         }
      }
      return result;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> awas = new HashSet<>();
         for (TreeItem item : treeItems) {
            Artifact art = (Artifact) item.getData();
            if (art instanceof AbstractWorkflowArtifact) {
               awas.add((AbstractWorkflowArtifact) art);
            }
         }
         if (awas.isEmpty()) {
            AWorkbench.popup("No Work Items Selected");
         } else {
            promptChangeActivityIds(awas);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
