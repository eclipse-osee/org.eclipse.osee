/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.editor.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.AtsStateItems;
import org.eclipse.osee.ats.editor.IAtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.branch.CommitWorkingBranchService;
import org.eclipse.osee.ats.editor.service.branch.CreateWorkingBranchService;
import org.eclipse.osee.ats.editor.service.branch.DeleteWorkingBranch;
import org.eclipse.osee.ats.editor.service.branch.SetAsDefaultBranchService;
import org.eclipse.osee.ats.editor.service.branch.ShowChangeReportService;
import org.eclipse.osee.ats.editor.service.branch.ShowMergeManagerService;
import org.eclipse.osee.ats.editor.service.branch.ShowWorkingBranchService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Donald G. Dunne
 */
public class ServicesArea {

   private final SMAManager smaMgr;
   private final List<WorkPageService> sideBarServices = new ArrayList<WorkPageService>();
   private final List<WorkPageService> toolBarServices = new ArrayList<WorkPageService>();
   private final ArrayList<Group> groups = new ArrayList<Group>();
   public static String STATISTIC_CATEGORY = "Statistics";
   public static String OPERATION_CATEGORY = "Operation";
   public static String DEBUG_PAGE_CATEGORY = "Debug";

   public ServicesArea(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   public void dispose() {
      for (WorkPageService service : sideBarServices)
         service.dispose();
      for (WorkPageService service : toolBarServices)
         service.dispose();
   }

   public void loadSidebarServices(AtsWorkPage page) throws OseeCoreException {
      if (sideBarServices.size() == 0) {
         // Operations
         sideBarServices.add(new FavoriteOperation(smaMgr));
         sideBarServices.add(new SubscribedOperation(smaMgr));
         sideBarServices.add(new OpenLatestVersion(smaMgr));
         sideBarServices.add(new DebugOperations(smaMgr));
         sideBarServices.add(new PrivilegedEditService(smaMgr));
         // Services
         sideBarServices.add(new AtsAdminStat(smaMgr));
         sideBarServices.add(new TotalPercentCompleteStat(smaMgr));
         sideBarServices.add(new TotalEstimatedHoursStat(smaMgr));
         sideBarServices.add(new TotalHoursSpentStat(smaMgr));
         sideBarServices.add(new TargetedForVersionState(smaMgr));
         sideBarServices.add(new StatePercentCompleteStat(smaMgr));
         sideBarServices.add(new StateEstimatedHoursStat(smaMgr));
         sideBarServices.add(new StateHoursSpentStat(smaMgr));
         sideBarServices.add(new RemainingHoursStat(smaMgr));
         sideBarServices.add(new AddDecisionReviewService(smaMgr));
         sideBarServices.add(new AddPeerToPeerReviewService(smaMgr));
         sideBarServices.add(new BlockingReview(smaMgr));
         // Add page configured branchable state items
         if (page != null && (page.isAllowCommitBranch() || page.isAllowCreateBranch())) {
            if (page.isAllowCreateBranch()) sideBarServices.add(new CreateWorkingBranchService(smaMgr));
            sideBarServices.add(new ShowWorkingBranchService(smaMgr));
            sideBarServices.add(new SetAsDefaultBranchService(smaMgr));
            sideBarServices.add(new ShowChangeReportService(smaMgr));
            sideBarServices.add(new ShowMergeManagerService(smaMgr));
            if (page.isAllowCommitBranch()) {
               sideBarServices.add(new CommitWorkingBranchService(smaMgr, false));
            }
            if (AtsPlugin.isAtsAdmin()) {
               sideBarServices.add(new CommitWorkingBranchService(smaMgr, true));
            }
            sideBarServices.add(new DeleteWorkingBranch(smaMgr));
         }
         // Add state specific items (these can also contain branch items through extending BranchableStateItem class
         for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
            sideBarServices.addAll(item.getSidebarServices(smaMgr));
         }
      }
   }

   public void loadToolbarServices(AtsWorkPage atsWorkPage) throws OseeCoreException {
      if (toolBarServices.size() == 0) {
         // Toolbar Services
         if (atsWorkPage != null && (atsWorkPage.isAllowCommitBranch() || atsWorkPage.isAllowCreateBranch() || smaMgr.getBranchMgr().isCommittedBranch() || smaMgr.getBranchMgr().isWorkingBranch())) {
            toolBarServices.add(new ShowMergeManagerService(smaMgr));
            toolBarServices.add(new ShowChangeReportService(smaMgr));
         }
         toolBarServices.add(new OpenParent(smaMgr));
         toolBarServices.add(new EmailActionService(smaMgr));
         toolBarServices.add(new AddNoteOperation(smaMgr));
         toolBarServices.add(new ShowNotesOperation(smaMgr));
         toolBarServices.add(new OpenInAtsWorldOperation(smaMgr));
         toolBarServices.add(new OpenInArtifactEditorOperation(smaMgr));
         toolBarServices.add(new OpenInSkyWalkerOperation(smaMgr));
         toolBarServices.add(new OpenVersionArtifact(smaMgr));
         toolBarServices.add(new OpenTeamDefinition(smaMgr));
         toolBarServices.add(new CopyActionDetailsService(smaMgr));
         for (IAtsStateItem item : AtsStateItems.getAllStateItems()) {
            toolBarServices.addAll(item.getToolbarServices(smaMgr));
         }
      }
   }

   public void createSidebarServices(Composite comp, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) throws OseeCoreException {
      loadSidebarServices(page);
      Set<String> categories = new HashSet<String>();
      for (WorkPageService service : sideBarServices) {
         if (service.getSidebarCategory() != null) categories.add(service.getSidebarCategory());
      }
      createSidebarServicesArea(comp, STATISTIC_CATEGORY, page, toolkit, section);
      categories.remove(STATISTIC_CATEGORY);
      createSidebarServicesArea(comp, OPERATION_CATEGORY, page, toolkit, section);
      categories.remove(OPERATION_CATEGORY);
      for (String category : categories) {
         createSidebarServicesArea(comp, category, page, toolkit, section);
      }
   }

   public void createToolbarServices(AtsWorkPage page, IToolBarManager toolbarManager) throws OseeCoreException {
      try {
         loadToolbarServices(page);
         for (final WorkPageService service : toolBarServices) {
            try {
               Action action = service.createToolbarService();
               if (action != null) toolbarManager.add(action);
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void createSidebarServicesArea(Composite comp, String category, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) throws OseeCoreException {

      // Determine services that are in this category and confirm that they should be displayed
      List<WorkPageService> displayServices = new ArrayList<WorkPageService>();
      for (WorkPageService service : sideBarServices)
         if (service.getSidebarCategory() != null && service.getSidebarCategory().equals(category) && service.isShowSidebarService(page)) displayServices.add(service);
      if (displayServices.size() == 0) return;

      Group workComp = new Group(comp, SWT.NONE);
      groups.add(workComp);
      workComp.setText(category);
      toolkit.adapt(workComp);
      GridLayout layout = new GridLayout(1, false);
      layout.marginWidth = layout.marginHeight = 7;

      workComp.setLayout(layout);
      workComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
      toolkit.paintBordersFor(workComp);

      for (WorkPageService service : displayServices)
         service.createSidebarService(workComp, page, toolkit, section);

   }

   public void refresh() {
      for (WorkPageService stat : sideBarServices)
         stat.refresh();
      for (WorkPageService stat : toolBarServices)
         stat.refresh();
      for (Group group : groups)
         if (group != null && !group.isDisposed()) group.layout();
   }

}
