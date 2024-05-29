/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.editor.tab.workflow.section.goal;

import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonViaAction;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class WfeGoalSection {

   private final Composite parent;
   private final WorkflowEditor editor;
   private final FormToolkit toolkit;

   public WfeGoalSection(Composite parent, WorkflowEditor editor, FormToolkit toolkit) {
      this.parent = parent;
      this.editor = editor;
      this.toolkit = toolkit;
   }

   public void create() {
      if (!editor.getWorkItem().isGoal()) {
         return;
      }
      Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
      section.setText("Goal Web Export");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, SWT.NONE);
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      boolean admin = AtsApiService.get().getUserService().isAtsAdmin();

      new XButtonViaAction(new OpenInstructionsAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(
         sectionBody, 2);
      new XButtonViaAction(
         new SelectWebExportCustomizationAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(sectionBody,
            2);

      if (admin) {
         new XButtonViaAction(
            new OpenWebViewLegacyHtmlAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(sectionBody, 2);
         new XButtonViaAction(new OpenWebViewJsonLiveAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(
            sectionBody, 2);
      }

      new XButtonViaAction(new OpenWebViewLiveAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(
         sectionBody, 2);
      new XButtonViaAction(new OpenDifferencesIdeViewAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(
         sectionBody, 2);
      new XButtonViaAction(new OpenDifferencesWebViewAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(
         sectionBody, 2);
      new XButtonViaAction(new PublishWebViewJsonAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(
         sectionBody, 2);

      if (admin) {
         new XButtonViaAction(
            new OpenWebViewJsonSavedAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(sectionBody, 2);
      }

      new XButtonViaAction(new OpenWebViewSavedAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(
         sectionBody, 2);
      new XButtonViaAction(new OpenWebExportAction((GoalArtifact) editor.getWorkItem(), editor)).createWidgets(
         sectionBody, 2);

      section.setClient(sectionBody);
   }

}
