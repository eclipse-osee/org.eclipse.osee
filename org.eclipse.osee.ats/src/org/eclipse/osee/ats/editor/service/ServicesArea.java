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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.IAtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.WorkPageService.Location;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
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
   private final AtsWorkPage page;
   private final XFormToolkit toolkit;
   private final SMAWorkFlowSection section;
   private List<WorkPageService> services = new ArrayList<WorkPageService>();
   private ArrayList<Group> groups = new ArrayList<Group>();
   private final boolean isCurrentState;
   public static String STATISTIC_CATEGORY = "Statistics";
   public static String OPERATION_CATEGORY = "Operation";
   public static String ADMIN_CATEGORY = "Admin";
   public static String DEBUG_PAGE_CATEGORY = "Debug";

   public ServicesArea(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      this.smaMgr = smaMgr;
      this.page = page;
      this.toolkit = toolkit;
      this.section = section;
      isCurrentState = smaMgr.isCurrentState(page);
   }

   public void dispose() {
      for (WorkPageService service : services)
         service.dispose();
   }

   public void loadServices() {
      if (services.size() == 0) {
         // Operations
         services.add(new FavoriteOperation(smaMgr, page, toolkit, section));
         services.add(new SubscribedOperation(smaMgr, page, toolkit, section));
         services.add(new OpenInAtsWorldOperation(smaMgr, page, toolkit, section));
         services.add(new OpenInSkyWalkerOperation(smaMgr, page, toolkit, section));
         services.add(new OpenInArtifactEditorOperation(smaMgr, page, toolkit, section));
         services.add(new OpenParent(smaMgr, page, toolkit, section));
         services.add(new OpenTeamDefinition(smaMgr, page, toolkit, section));
         services.add(new OpenLatestVersion(smaMgr, page, toolkit, section));
         services.add(new DebugOperations(smaMgr, page, toolkit, section));
         services.add(new ShowNotesOperation(smaMgr, page, toolkit, section));
         services.add(new AddNoteOperation(smaMgr, page, toolkit, section));
         services.add(new PrivilegedEditService(smaMgr, page, toolkit, section));
         services.add(new EmailActionService(smaMgr, page, toolkit, section));
         services.add(new AddDecisionReviewService(smaMgr, page, toolkit, section));
         services.add(new AddPeerToPeerReviewService(smaMgr, page, toolkit, section));
         // Services
         services.add(new AtsAdminStat(smaMgr, page, toolkit, section));
         services.add(new BlockingReview(smaMgr, page, toolkit, section));
         services.add(new HridStat(smaMgr, page, toolkit, section));
         services.add(new TotalPercentCompleteStat(smaMgr, page, toolkit, section));
         services.add(new TotalHoursSpentStat(smaMgr, page, toolkit, section));
         services.add(new TargetedForVersionState(smaMgr, page, toolkit, section));
         services.add(new StatePercentCompleteStat(smaMgr, page, toolkit, section));
         services.add(new StateHoursSpentStat(smaMgr, page, toolkit, section));
         // Add extension services for this state
         for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
            services.addAll(item.getServices(smaMgr, page, toolkit, section));
         }
      }
   }

   public void create(Composite comp) {
      loadServices();
      Set<String> categories = new HashSet<String>();
      for (WorkPageService service : services) {
         categories.add(service.getCategory());
      }
      if (AtsPlugin.isAtsAdmin()) {
         createServicesArea(comp, ADMIN_CATEGORY);
      }
      categories.remove(ADMIN_CATEGORY);
      createServicesArea(comp, STATISTIC_CATEGORY);
      categories.remove(STATISTIC_CATEGORY);
      createServicesArea(comp, OPERATION_CATEGORY);
      categories.remove(OPERATION_CATEGORY);
      for (String category : categories) {
         createServicesArea(comp, category);
      }
   }

   private void createServicesArea(Composite comp, String category) {

      List<WorkPageService> displayServices = new ArrayList<WorkPageService>();

      // Add all global
      if (smaMgr.isFirstState(page)) {
         for (WorkPageService service : services)
            if (page.isDisplayService(service) && service.getCategory().equals(category) && service.getLocation() == Location.Global) displayServices.add(service);
      }

      // Add all current state
      if (isCurrentState) {
         for (WorkPageService service : services) {
            if (page.isDisplayService(service) && service.getCategory().equals(category) && service.getLocation() == Location.CurrentState)
               displayServices.add(service);
            else if (page.isDisplayService(service) && service.getCategory().equals(category) && service.getLocation() == Location.NonCompleteCurrentState) {
               if (!page.isCancelledPage() && !page.isCompletePage()) displayServices.add(service);
            }
         }
      }

      // Add all state
      for (WorkPageService service : services)
         if (page.isDisplayService(service) && service.getCategory().equals(category) && service.getLocation() == Location.AllState) displayServices.add(service);

      // Add all non-complete state
      for (WorkPageService service : services)
         if (page.isDisplayService(service) && service.getCategory().equals(category) && service.getLocation() == Location.AllNonCompleteState) if (!page.isCancelledPage() && !page.isCompletePage()) displayServices.add(service);

      // Add specified page ids
      for (WorkPageService service : services)
         if (page.isDisplayService(service) && service.getCategory().equals(category) && service.getLocation() == Location.SpecifiedPageId) if (service.isSpecifiedPageId(page.getId())) displayServices.add(service);

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

      for (WorkPageService op : displayServices)
         if (op.displayService()) op.create(workComp);

   }

   public void refresh() {
      for (WorkPageService stat : services)
         stat.refresh();
      for (Group group : groups)
         if (group != null && !group.isDisposed()) group.layout();
   }

}
