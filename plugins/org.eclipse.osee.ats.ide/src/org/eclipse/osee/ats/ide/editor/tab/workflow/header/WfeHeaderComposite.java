/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.note.NoteItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.WorkflowManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AnnotationComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AttributeAnnotationManager;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class WfeHeaderComposite extends Composite {

   private final WorkflowEditor editor;
   private final IAtsWorkItem workItem;
   private WfeRelationsHyperlinkComposite smaRelationsComposite;
   private WfeActionableItemHeader actionableItemHeader;
   private WfeMetricsHeader workflowMetricsHeader;
   private final StateXWidgetPage currentStateXWidgetPage;
   private static Color LIGHT_GREY;
   private final IManagedForm managedForm;

   public WfeHeaderComposite(Composite parent, int style, WorkflowEditor editor, StateXWidgetPage currentStateXWidgetPage, IManagedForm managedForm) {
      super(parent, style);
      this.editor = editor;
      this.currentStateXWidgetPage = currentStateXWidgetPage;
      this.managedForm = managedForm;
      this.workItem = editor.getWorkItem();
   }

   public void create() {
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 100;
      this.setLayoutData(gd);
      this.setLayout(ALayout.getZeroMarginLayout(1, false));

      // Display relations
      try {
         new WfeStateCreatedOrigHeader(this, SWT.NONE, workItem, editor);
         new WfeTeamAndIdsHeader(this, SWT.NONE, workItem, editor);
         createTargetVersionAndAssigneeHeader(this, currentStateXWidgetPage, editor.getToolkit());

         createLatestHeader(this, editor.getToolkit());
         if (workItem.isTeamWorkflow()) {
            actionableItemHeader = new WfeActionableItemHeader(this, editor.getToolkit(), workItem, editor);
         }
         workflowMetricsHeader = new WfeMetricsHeader(this, editor.getToolkit(), workItem, editor, managedForm);
         int thisColumns = 4;
         createWorkPacakageHeader(this, editor.getToolkit(), thisColumns, editor);
         createWorkDefHeader(this, editor.getToolkit(), workItem, thisColumns);
         new WfeBlockedWorkflowHeader(this, SWT.NONE, workItem, editor);
         createSMANotesHeader(this, editor.getToolkit(), thisColumns);
         createStateNotesHeader(this, workItem, editor.getToolkit(), thisColumns, null);
         createAnnotationsHeader(this, editor.getToolkit());

         if (WfeRelationsHyperlinkComposite.relationExists((AbstractWorkflowArtifact) workItem)) {
            smaRelationsComposite = new WfeRelationsHyperlinkComposite(this, SWT.NONE, editor);
            smaRelationsComposite.create((AbstractWorkflowArtifact) workItem);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public static void createWorkDefHeader(Composite comp, XFormToolkit toolkit, IAtsWorkItem workItem, int horizontalSpan) {
      Composite headerComp = new Composite(comp, SWT.NONE);
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = horizontalSpan;
      headerComp.setLayoutData(gridData);
      headerComp.setLayout(ALayout.getZeroMarginLayout(5, false));
      toolkit.adapt(headerComp);

      IAtsWorkDefinition workDef = workItem.getWorkDefinition();
      Label label = FormsUtil.createLabelValue(toolkit, headerComp, "Work Definition: ", workDef.getName());
      label.addListener(SWT.MouseDoubleClick, new Listener() {

         @Override
         public void handleEvent(Event event) {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.showView("org.eclipse.ui.views.ContentOutline", null, IWorkbenchPage.VIEW_ACTIVATE);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      Label dragDropLabel = new Label(headerComp, SWT.NONE);
      dragDropLabel.setText("Drag/Drop Related Here: ");
      FormsUtil.setLabelFonts(dragDropLabel, FontManager.getDefaultLabelFont());
      dragDropLabel.setLayoutData(new GridData());
      toolkit.adapt(dragDropLabel, true, true);

      Label dragDropBox = new Label(headerComp, SWT.BORDER);
      new WfeDragAndDrop(dragDropBox, (AbstractWorkflowArtifact) workItem.getStoreObject(), WorkflowEditor.EDITOR_ID);
      dragDropBox.setText("                             ");
      dragDropBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      toolkit.adapt(dragDropBox, true, true);
      dragDropBox.setBackground(getLightGreyColor());
   }

   public void createSMANotesHeader(Composite comp, XFormToolkit toolkit, int horizontalSpan) {
      // Display SMA Note
      String note =
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.SmaNote, "");
      if (!note.equals("")) {
         FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, "Note: " + note);
      }
   }

   public static void createStateNotesHeader(Composite comp, IAtsWorkItem workItem, XFormToolkit toolkit, int horizontalSpan, String forStateName) {
      // Display global Notes
      for (NoteItem noteItem : AtsClientService.get().getWorkItemService().getNotes(workItem).getNoteItems()) {
         if (forStateName == null || noteItem.getState().equals(forStateName)) {
            FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, noteItem.toString());
         }
      }
   }

   public void createWorkPacakageHeader(Composite parent, XFormToolkit toolkit, int horizontalSpan, WorkflowEditor editor) {
      boolean show = workItem.isTeamWorkflow() && !AtsClientService.get().getEarnedValueService().getWorkPackageOptions(
         workItem).isEmpty();

      if (show) {
         Composite comp = toolkit.createContainer(parent, 6);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         comp.setLayout(ALayout.getZeroMarginLayout(6, false));

         new WfeWorkPackage(comp, SWT.NONE, workItem, editor);
      }
   }

   private void createLatestHeader(Composite comp, XFormToolkit toolkit) {
      if (AtsClientService.get().getStoreService().isHistorical(workItem)) {
         Label label = toolkit.createLabel(comp,
            "This is a historical version of this " + workItem.getArtifactTypeName() + " and can not be edited; Select \"Open Latest\" to view/edit latest version.");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      }
   }

   private void createAnnotationsHeader(Composite comp, XFormToolkit toolkit) {
      try {
         if (AttributeAnnotationManager.getAnnotations((Artifact) workItem.getStoreObject()).size() > 0) {
            new AnnotationComposite(toolkit, comp, SWT.None, (Artifact) workItem.getStoreObject());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Exception resolving annotations", ex);
      }
   }

   private void createTargetVersionAndAssigneeHeader(Composite parent, StateXWidgetPage page, XFormToolkit toolkit) {
      boolean isShowTargetedVersion = isShowTargetedVersion();
      boolean isCurrentNonCompleteCanceledState =
         page.isCurrentNonCompleteCancelledState((AbstractWorkflowArtifact) workItem.getStoreObject());
      if (!isShowTargetedVersion && !isCurrentNonCompleteCanceledState) {
         return;
      }

      Composite comp = toolkit.createContainer(parent, 6);
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      comp.setLayout(ALayout.getZeroMarginLayout(6, false));

      // Targeted Version
      if (isShowTargetedVersion) {
         new WfeTargetedVersionHeader(comp, SWT.NONE, (IAtsTeamWorkflow) workItem, editor);
         toolkit.createLabel(comp, "    ");
      }

      // Current Assignees
      if (isCurrentNonCompleteCanceledState) {
         boolean editable = WorkflowManager.isAssigneeEditable((AbstractWorkflowArtifact) workItem.getStoreObject());

         new WfeAssigneesHeader(comp, SWT.NONE, workItem, editable, editor);
      }
   }

   public boolean isShowTargetedVersion() {
      if (!workItem.isTeamWorkflow()) {
         return false;
      }
      return ((TeamWorkFlowArtifact) workItem).getTeamDefinition().isTeamUsesVersions();
   }

   private static Color getLightGreyColor() {
      if (LIGHT_GREY == null) {
         LIGHT_GREY = Displays.getColor(240, 240, 240);
      }
      return LIGHT_GREY;
   }

   @Override
   public void dispose() {
      if (actionableItemHeader != null) {
         actionableItemHeader.dispose();
      }
      if (workflowMetricsHeader != null) {
         workflowMetricsHeader.dispose();
      }
   }

}
