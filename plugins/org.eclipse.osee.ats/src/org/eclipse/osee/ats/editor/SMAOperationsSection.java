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
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.actions.AccessControlAction;
import org.eclipse.osee.ats.actions.AtsLogHistoryAction;
import org.eclipse.osee.ats.actions.ConvertActionableItemsAction;
import org.eclipse.osee.ats.actions.DeletePurgeAtsArtifactsAction;
import org.eclipse.osee.ats.actions.DirtyReportAction;
import org.eclipse.osee.ats.actions.DuplicateWorkflowAction;
import org.eclipse.osee.ats.actions.EditActionableItemsAction;
import org.eclipse.osee.ats.actions.EmailActionAction;
import org.eclipse.osee.ats.actions.FavoriteAction;
import org.eclipse.osee.ats.actions.OpenInArtifactEditorAction;
import org.eclipse.osee.ats.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.actions.OpenInSkyWalkerAction;
import org.eclipse.osee.ats.actions.OpenParentAction;
import org.eclipse.osee.ats.actions.RefreshDirtyAction;
import org.eclipse.osee.ats.actions.ReloadAction;
import org.eclipse.osee.ats.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.actions.ShowBranchChangeDataAction;
import org.eclipse.osee.ats.actions.SubscribedAction;
import org.eclipse.osee.ats.actions.WorkflowDebugAction;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
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
public class SMAOperationsSection extends SectionPart {

   protected final SMAEditor editor;
   private static List<ISMAOperationsSection> advOperations;
   private boolean sectionCreated = false;

   public SMAOperationsSection(SMAEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.editor = editor;
      registerAdvancedSectionsFromExtensionPoints();
   }

   private synchronized void registerAdvancedSectionsFromExtensionPoints() {
      if (advOperations == null) {
         advOperations = new ArrayList<ISMAOperationsSection>();
         ExtensionDefinedObjects<ISMAOperationsSection> extensions =
            new ExtensionDefinedObjects<ISMAOperationsSection>(AtsPlugin.PLUGIN_ID + ".AtsAdvancedOperationAction",
               "AtsAdvancedOperationAction", "classname");
         for (ISMAOperationsSection item : extensions.getObjects()) {
            advOperations.add(item);
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
      sectionBody.setLayout(ALayout.getZeroMarginLayout(3, false));
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
      if (!editor.getSma().isTeamWorkflow()) {
         return;
      }
      Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
      section.setText("Impacts and Workflows");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (editor.getSma().isTeamWorkflow()) {
         new XButtonViaAction(new EditActionableItemsAction((TeamWorkFlowArtifact) editor.getSma())).createWidgets(
            sectionBody, 2);
         new XButtonViaAction(
            new DuplicateWorkflowAction(Collections.singleton((TeamWorkFlowArtifact) editor.getSma()))).createWidgets(
            sectionBody, 2);
         new XButtonViaAction(new AccessControlAction(editor.getSma())).createWidgets(sectionBody, 2);
      }
      section.setClient(sectionBody);
   }

   private void createAdvancedSection(Composite parent, FormToolkit toolkit) {
      if (!editor.getSma().isTeamWorkflow()) {
         return;
      }

      Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
      section.setText("Advanced");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (editor.getSma().isTeamWorkflow()) {
         new XButtonViaAction(new DirtyReportAction(editor.getSma(), editor)).createWidgets(sectionBody, 2);
         new XButtonViaAction(new ReloadAction(editor.getSma())).createWidgets(sectionBody, 2);
         new XButtonViaAction(new ConvertActionableItemsAction(editor)).createWidgets(sectionBody, 2);
      }

      for (ISMAOperationsSection operation : advOperations) {
         if (operation.isValid(editor)) {
            operation.createAdvancedSection(editor, sectionBody, toolkit);
         }
      }

      section.setClient(sectionBody);
   }

   private void createAdminSection(Composite parent, FormToolkit toolkit) {
      if (!AtsUtil.isAtsAdmin()) {
         return;
      }
      Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
      section.setText("Admin");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      new XButtonViaAction(new RefreshDirtyAction(editor.getSma(), editor)).createWidgets(sectionBody, 2);
      new XButtonViaAction(new DeletePurgeAtsArtifactsAction(editor)).createWidgets(sectionBody, 2);
      new XButtonViaAction(new WorkflowDebugAction(editor.getSma())).createWidgets(sectionBody, 2);
      if (ShowBranchChangeDataAction.isApplicable(editor.getSma())) {
         new XButtonViaAction(new ShowBranchChangeDataAction(editor.getSma())).createWidgets(sectionBody, 2);
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
         new XButtonViaAction(new OpenInAtsWorldAction(editor.getSma())).createWidgets(sectionBody, 2);
         new XButtonViaAction(new OpenInSkyWalkerAction(editor.getSma())).createWidgets(sectionBody, 2);
         new XButtonViaAction(new ResourceHistoryAction(editor.getSma())).createWidgets(sectionBody, 2);
         new XButtonViaAction(new AtsLogHistoryAction(editor.getSma())).createWidgets(sectionBody, 2);
         if (editor.getSma().getParentSMA() != null) {
            new XButtonViaAction(new OpenParentAction(editor.getSma())).createWidgets(sectionBody, 2);
         }
         if (AtsUtil.isAtsAdmin()) {
            new XButtonViaAction(new OpenInArtifactEditorAction(editor)).createWidgets(sectionBody, 2);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      section.setClient(sectionBody);
   }

}
