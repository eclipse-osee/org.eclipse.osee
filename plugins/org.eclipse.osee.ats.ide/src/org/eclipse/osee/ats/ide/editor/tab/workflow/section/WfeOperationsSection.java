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
package org.eclipse.osee.ats.ide.editor.tab.workflow.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.ide.actions.AccessControlAction;
import org.eclipse.osee.ats.ide.actions.CreateActionFromTaskAction;
import org.eclipse.osee.ats.ide.actions.DeletePurgeAtsArtifactsAction;
import org.eclipse.osee.ats.ide.actions.DirtyReportAction;
import org.eclipse.osee.ats.ide.actions.DuplicateWorkflowAction;
import org.eclipse.osee.ats.ide.actions.EditActionableItemsAction;
import org.eclipse.osee.ats.ide.actions.EmailActionAction;
import org.eclipse.osee.ats.ide.actions.FavoriteAction;
import org.eclipse.osee.ats.ide.actions.ModifyActionableItemAction;
import org.eclipse.osee.ats.ide.actions.MoveWorkflowWorkingBranchToWorkflowAction;
import org.eclipse.osee.ats.ide.actions.OpenInArtifactEditorAction;
import org.eclipse.osee.ats.ide.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.ide.actions.OpenInSkyWalkerAction;
import org.eclipse.osee.ats.ide.actions.OpenParentAction;
import org.eclipse.osee.ats.ide.actions.RefreshDirtyAction;
import org.eclipse.osee.ats.ide.actions.ReloadAction;
import org.eclipse.osee.ats.ide.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.ide.actions.ShowBranchChangeDataAction;
import org.eclipse.osee.ats.ide.actions.ShowWorkDefinitionAction;
import org.eclipse.osee.ats.ide.actions.SubscribedAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.operation.MoveTeamWorkflowsAction;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonViaAction;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class WfeOperationsSection extends SectionPart {

   protected final WorkflowEditor editor;
   private static List<IWfeOperationsSection> operationsSectionProviders;
   private boolean sectionCreated = false;

   public WfeOperationsSection(WorkflowEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.editor = editor;
      registerAdvancedSectionsFromExtensionPoints();
   }

   private synchronized void registerAdvancedSectionsFromExtensionPoints() {
      if (operationsSectionProviders == null) {
         operationsSectionProviders = new ArrayList<>();
         ExtensionDefinedObjects<IWfeOperationsSection> extensions = new ExtensionDefinedObjects<>(
            Activator.PLUGIN_ID + ".AtsAdvancedOperationAction", "AtsAdvancedOperationAction", "classname", true);
         for (IWfeOperationsSection item : extensions.getObjects()) {
            operationsSectionProviders.add(item);
         }
      }
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      final Section section = getSection();
      section.setText("Operations");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      // Only load when users selects section
      section.addListener(SWT.Activate, new Listener() {

         @Override
         public void handleEvent(Event e) {
            createSection(section, toolkit);
         }
      });
   }

   private synchronized void createSection(Section section, FormToolkit toolkit) {
      if (sectionCreated) {
         return;
      }

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(new GridLayout(3, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createImpactsSection(sectionBody, toolkit);
      createViewsEditorsSection(sectionBody, toolkit);
      createNotificationsSection(sectionBody, toolkit);

      createAdvancedSection(sectionBody, toolkit);
      createAdminSection(sectionBody, toolkit);

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);
      sectionCreated = true;

   }

   private void createImpactsSection(Composite parent, FormToolkit toolkit) {
      if (!editor.getWorkItem().isTeamWorkflow() && !editor.getWorkItem().isTask()) {
         return;
      }
      Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
      section.setText("Impacts and Workflows");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (editor.getWorkItem().isTeamWorkflow()) {
         new XButtonViaAction(new EditActionableItemsAction((TeamWorkFlowArtifact) editor.getWorkItem())).createWidgets(
            sectionBody, 2);
         new XButtonViaAction(new DuplicateWorkflowAction(
            Collections.singleton((TeamWorkFlowArtifact) editor.getWorkItem()))).createWidgets(sectionBody, 2);
         new XButtonViaAction(
            new ModifyActionableItemAction((TeamWorkFlowArtifact) editor.getWorkItem())).createWidgets(sectionBody, 2);
         new XButtonViaAction(new AccessControlAction(editor.getWorkItem())).createWidgets(sectionBody, 2);
      }
      if (editor.getWorkItem().isTask()) {
         new XButtonViaAction(
            new CreateActionFromTaskAction(Collections.singleton((TaskArtifact) editor.getWorkItem()))).createWidgets(
               sectionBody, 2);
      }
      section.setClient(sectionBody);
   }

   private void createAdvancedSection(Composite parent, FormToolkit toolkit) {
      Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
      section.setText("Advanced");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (editor.getWorkItem().isTeamWorkflow()) {
         new XButtonViaAction(new DirtyReportAction(editor)).createWidgets(sectionBody, 2);
         new XButtonViaAction(new ReloadAction(editor.getWorkItem(), editor)).createWidgets(sectionBody, 2);
         new XButtonViaAction(new MoveTeamWorkflowsAction()).createWidgets(sectionBody, 2);
      }

      for (IWfeOperationsSection operation : operationsSectionProviders) {
         operation.createAdvancedSection(editor, sectionBody, toolkit);
      }

      section.setClient(sectionBody);
   }

   private void createAdminSection(Composite parent, FormToolkit toolkit) {
      if (!AtsClientService.get().getUserService().isAtsAdmin()) {
         return;
      }
      Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
      section.setText("Admin");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      new XButtonViaAction(new RefreshDirtyAction(editor)).createWidgets(sectionBody, 2);
      new XButtonViaAction(new DeletePurgeAtsArtifactsAction(editor, false)).createWidgets(sectionBody, 2);
      if (ShowBranchChangeDataAction.isApplicable(editor.getWorkItem())) {
         new XButtonViaAction(new ShowBranchChangeDataAction(editor.getWorkItem())).createWidgets(sectionBody, 2);
      }
      new XButtonViaAction(new MoveWorkflowWorkingBranchToWorkflowAction(editor, AtsClientService.get())).createWidgets(
         sectionBody, 2);

      for (IWfeOperationsSection operation : operationsSectionProviders) {
         operation.createAdminSection(editor, sectionBody, toolkit);
      }

      section.setClient(sectionBody);
   }

   private void createViewsEditorsSection(Composite parent, FormToolkit toolkit) {
      Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
      section.setText("Views and Editors");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      try {
         new XButtonViaAction(new OpenInAtsWorldAction(editor.getWorkItem())).createWidgets(sectionBody, 2);
         new XButtonViaAction(new OpenInSkyWalkerAction(editor.getWorkItem())).createWidgets(sectionBody, 2);
         new XButtonViaAction(new ResourceHistoryAction(editor.getWorkItem())).createWidgets(sectionBody, 2);
         if (editor.getWorkItem().getParentAWA() != null) {
            new XButtonViaAction(new OpenParentAction(editor.getWorkItem())).createWidgets(sectionBody, 2);
         }
         if (AtsClientService.get().getUserService().isAtsAdmin()) {
            new XButtonViaAction(new OpenInArtifactEditorAction(editor)).createWidgets(sectionBody, 2);
         }
         new XButtonViaAction(new ShowWorkDefinitionAction()).createWidgets(sectionBody, 2);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      section.setClient(sectionBody);
   }

   private void createNotificationsSection(Composite parent, FormToolkit toolkit) {
      Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
      section.setText("Notifications and Favorites");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      try {
         new XButtonViaAction(new SubscribedAction(editor)).createWidgets(sectionBody, 2);
         new XButtonViaAction(new FavoriteAction(editor)).createWidgets(sectionBody, 2);
         new XButtonViaAction(new EmailActionAction(editor)).createWidgets(sectionBody, 2);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      section.setClient(sectionBody);
   }

}
