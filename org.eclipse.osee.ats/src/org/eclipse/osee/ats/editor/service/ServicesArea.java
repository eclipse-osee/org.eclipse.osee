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
import org.eclipse.osee.ats.editor.service.branch.ShowChangeReportService;
import org.eclipse.osee.ats.editor.service.branch.ShowChangeReportToolbarService;
import org.eclipse.osee.ats.editor.service.branch.ShowWorkingBranchService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
   private List<WorkPageService> services = new ArrayList<WorkPageService>();
   private ArrayList<Group> groups = new ArrayList<Group>();
   public static String STATISTIC_CATEGORY = "Statistics";
   public static String OPERATION_CATEGORY = "Operation";
   public static String DEBUG_PAGE_CATEGORY = "Debug";

   public ServicesArea(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   public void dispose() {
      for (WorkPageService service : services)
         service.dispose();
   }

   public void loadServices(AtsWorkPage page) {
      if (services.size() == 0) {
         // Operations
         services.add(new FavoriteOperation(smaMgr));
         services.add(new SubscribedOperation(smaMgr));
         services.add(new OpenLatestVersion(smaMgr));
         services.add(new DebugOperations(smaMgr));
         services.add(new PrivilegedEditService(smaMgr));
         // Services
         services.add(new AtsAdminStat(smaMgr));
         services.add(new TotalPercentCompleteStat(smaMgr));
         services.add(new TotalHoursSpentStat(smaMgr));
         services.add(new TargetedForVersionState(smaMgr));
         services.add(new StatePercentCompleteStat(smaMgr));
         services.add(new StateHoursSpentStat(smaMgr));
         services.add(new AddDecisionReviewService(smaMgr));
         services.add(new AddPeerToPeerReviewService(smaMgr));
         services.add(new BlockingReview(smaMgr));
         // Toolbar Services
         services.add(new ShowChangeReportToolbarService(smaMgr));
         services.add(new OpenParent(smaMgr));
         services.add(new EmailActionService(smaMgr));
         services.add(new AddNoteOperation(smaMgr));
         services.add(new ShowNotesOperation(smaMgr));
         services.add(new OpenInAtsWorldOperation(smaMgr));
         services.add(new OpenInArtifactEditorOperation(smaMgr));
         services.add(new OpenInSkyWalkerOperation(smaMgr));
         services.add(new OpenVersionArtifact(smaMgr));
         services.add(new OpenTeamDefinition(smaMgr));
         services.add(new CopyActionDetailsService(smaMgr));
         // Add page configured branchable state items
         if (page != null && (page.isAllowCommitBranch() || page.isAllowCreateBranch())) {
            if (page.isAllowCreateBranch()) services.add(new CreateWorkingBranchService(smaMgr));
            services.add(new ShowWorkingBranchService(smaMgr));
            services.add(new ShowChangeReportService(smaMgr));
            if (page.isAllowCommitBranch()) services.add(new CommitWorkingBranchService(smaMgr, false));
            if (AtsPlugin.isAtsAdmin()) services.add(new CommitWorkingBranchService(smaMgr, true));
            services.add(new DeleteWorkingBranch(smaMgr));
         }
         // Add state specific items (these can also contain branch items through extending BranchableStateItem class
         if (page != null)
            for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
               services.addAll(item.getServices(smaMgr));
            }
         // If page is null, this is a toolbar request for services, load all state items
         else if (page == null) for (IAtsStateItem item : AtsStateItems.getAllStateItems()) {
            services.addAll(item.getServices(smaMgr));
         }
      }
   }

   public void createSidebarServices(Composite comp, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      loadServices(page);
      Set<String> categories = new HashSet<String>();
      for (WorkPageService service : services) {
         categories.add(service.getSidebarCategory());
      }
      createServicesArea(comp, STATISTIC_CATEGORY, page, toolkit, section);
      categories.remove(STATISTIC_CATEGORY);
      createServicesArea(comp, OPERATION_CATEGORY, page, toolkit, section);
      categories.remove(OPERATION_CATEGORY);
      for (String category : categories) {
         createServicesArea(comp, category, page, toolkit, section);
      }
   }

   public void createToolbarServices(IToolBarManager toolbarManager) {
      loadServices(null);
      for (final WorkPageService service : services) {
         try {
            Action action = service.createToolbarService();
            if (action != null) toolbarManager.add(action);
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
         }
      }
   }

   private void createServicesArea(Composite comp, String category, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {

      // Determine services that are in this category and confirm that they should be displayed
      List<WorkPageService> displayServices = new ArrayList<WorkPageService>();
      for (WorkPageService service : services)
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
      for (WorkPageService stat : services)
         stat.refresh();
      for (Group group : groups)
         if (group != null && !group.isDisposed()) group.layout();
   }

}
