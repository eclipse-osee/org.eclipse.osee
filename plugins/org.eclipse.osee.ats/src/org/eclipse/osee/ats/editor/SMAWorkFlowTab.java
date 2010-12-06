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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.actions.AddNoteAction;
import org.eclipse.osee.ats.actions.CopyActionDetailsAction;
import org.eclipse.osee.ats.actions.EmailActionAction;
import org.eclipse.osee.ats.actions.FavoriteAction;
import org.eclipse.osee.ats.actions.OpenInArtifactEditorAction;
import org.eclipse.osee.ats.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.actions.OpenParentAction;
import org.eclipse.osee.ats.actions.OpenTeamDefinitionAction;
import org.eclipse.osee.ats.actions.OpenVersionArtifactAction;
import org.eclipse.osee.ats.actions.PrivilegedEditAction;
import org.eclipse.osee.ats.actions.ReloadAction;
import org.eclipse.osee.ats.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.actions.ShowChangeReportAction;
import org.eclipse.osee.ats.actions.ShowMergeManagerAction;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.note.NoteItem;
import org.eclipse.osee.ats.config.AtsBulkLoad;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AnnotationComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.MessageSummaryNote;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.skynet.util.OseeDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactStoredWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowTab extends FormPage implements IActionable {
   private final AbstractWorkflowArtifact sma;
   private final List<SMAWorkFlowSection> sections = new ArrayList<SMAWorkFlowSection>();
   private final List<AtsWorkPage> atsWorkPages = new ArrayList<AtsWorkPage>();
   private static Map<String, Integer> guidToScrollLocation = new HashMap<String, Integer>();
   private SMARelationsHyperlinkComposite smaRelationsComposite;
   private IManagedForm managedForm;
   private Composite bodyComp;
   private Composite atsBody;
   private SMAActionableItemHeader actionableItemHeader;
   private SMAWorkflowMetricsHeader workflowMetricsHeader;
   private SMADetailsSection smaDetailsSection;
   private SMARelationsSection smaRelationsSection;
   private SMAGoalMembersSection smaGoalMembersSection;
   private SMAHistorySection smaHistorySection;
   private LoadingComposite loadingComposite;
   public final static String ID = "ats.workflow.tab";
   private final SMAEditor editor;

   public SMAWorkFlowTab(SMAEditor editor, AbstractWorkflowArtifact sma) {
      super(editor, ID, "Workflow");
      this.editor = editor;
      this.sma = sma;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      try {
         managedForm.getForm().addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
               storeScrollLocation();
            }
         });
         updateTitleBar();

         bodyComp = managedForm.getForm().getBody();
         GridLayout gridLayout = new GridLayout(1, false);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, false);
         gd.widthHint = 300;
         bodyComp.setLayoutData(gd);

         setLoading(true);
         if (sma.getHelpContext() != null) {
            HelpUtil.setHelp(managedForm.getForm(), sma.getHelpContext(), "org.eclipse.osee.ats.help.ui");
         }

         refreshData();

      } catch (Exception ex) {
         handleException(ex);
      }
   }

   private void updateTitleBar() throws OseeCoreException {
      String titleString = editor.getTitleStr();
      String displayableTitle = Strings.escapeAmpersands(titleString);
      managedForm.getForm().setText(displayableTitle);
      managedForm.getForm().setImage(ArtifactImageManager.getImage(sma));
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   public void refreshData() {
      Operations.executeAsJob(AtsBulkLoad.getConfigLoadingOperation(), true, Job.LONG, new ReloadJobChangeAdapter(
         editor));
      // Don't put in operation cause doesn't have to be loaded before editor displays
      OseeDictionary.load();
   }
   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final SMAEditor editor;

      private ReloadJobChangeAdapter(SMAEditor editor) {
         this.editor = editor;
         showBusy(true);
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         Job job = new UIJob("Draw Workflow Tab") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  updateTitleBar();
                  refreshToolbar();
                  setLoading(false);
                  createAtsBody();
                  addMessageDecoration(managedForm.getForm());
                  FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);
                  editor.onDirtied();
               } catch (OseeCoreException ex) {
                  handleException(ex);
               } finally {
                  showBusy(false);
               }
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   private void handleException(Exception ex) {
      setLoading(false);
      if (Widgets.isAccessible(atsBody)) {
         atsBody.dispose();
      }
      OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   private void setLoading(boolean set) {
      if (set) {
         loadingComposite = new LoadingComposite(bodyComp);
         bodyComp.layout();
      } else {
         if (Widgets.isAccessible(loadingComposite)) {
            loadingComposite.dispose();
         }
      }
      showBusy(set);
   }

   private void createAtsBody() throws OseeCoreException {
      if (Widgets.isAccessible(atsBody)) {
         atsBody.dispose();
      }
      atsBody = editor.getToolkit().createComposite(bodyComp);
      atsBody.setLayoutData(new GridData(GridData.FILL_BOTH));
      atsBody.setLayout(new GridLayout(1, false));

      createHeaderSection(sma.getCurrentAtsWorkPage());
      createGoalSection();
      createPageSections();
      createHistorySection();
      createRelationsSection();
      createOperationsSection();
      createDetailsSection();

      atsBody.layout();
      atsBody.setFocus();
      // Jump to scroll location if set
      Integer selection = guidToScrollLocation.get(sma.getGuid());
      if (selection != null) {
         JumpScrollbarJob job = new JumpScrollbarJob("");
         job.schedule(500);
      }

   }

   private void createDetailsSection() {
      try {
         smaDetailsSection = new SMADetailsSection(editor, atsBody, editor.getToolkit(), SWT.NONE);
         managedForm.addPart(smaDetailsSection);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createOperationsSection() {
      try {
         SMAOperationsSection smaOperationsSection =
            new SMAOperationsSection(editor, atsBody, editor.getToolkit(), SWT.NONE);
         managedForm.addPart(smaOperationsSection);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createRelationsSection() {
      try {
         smaRelationsSection = new SMARelationsSection(editor, atsBody, editor.getToolkit(), SWT.NONE);
         managedForm.addPart(smaRelationsSection);

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createHistorySection() {
      try {
         smaHistorySection = new SMAHistorySection(editor, atsBody, editor.getToolkit(), SWT.NONE);
         managedForm.addPart(smaHistorySection);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createGoalSection() {
      try {
         if (sma instanceof GoalArtifact) {
            smaGoalMembersSection = new SMAGoalMembersSection(editor, atsBody, editor.getToolkit(), SWT.NONE);
            managedForm.addPart(smaGoalMembersSection);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createPageSections() {
      try {
         // Only display current or past states
         for (AtsWorkPage atsWorkPage : sma.getAtsWorkPages()) {
            try {
               if (sma.isInState(atsWorkPage) || sma.getStateMgr().isStateVisited(atsWorkPage)) {
                  // Don't show completed or cancelled state if not currently those state
                  if (atsWorkPage.isCompletedPage() && !sma.isCompleted()) {
                     continue;
                  }
                  if (atsWorkPage.isCancelledPage() && !sma.isCancelled()) {
                     continue;
                  }
                  SMAWorkFlowSection section = new SMAWorkFlowSection(atsBody, SWT.NONE, atsWorkPage, sma, editor);
                  managedForm.addPart(section);
                  control = section.getMainComp();
                  sections.add(section);
                  atsWorkPages.add(atsWorkPage);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createHeaderSection(AtsWorkPage currentAtsWorkPage) {
      Composite headerComp = editor.getToolkit().createComposite(atsBody);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 100;
      headerComp.setLayoutData(gd);
      headerComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      // mainComp.setBackground(Displays.getSystemColor(SWT.COLOR_RED));

      // Display relations
      try {
         createCurrentStateAndTeamHeaders(headerComp, editor.getToolkit());
         createTargetVersionAndAssigneeHeader(headerComp, currentAtsWorkPage, editor.getToolkit());

         createLatestHeader(headerComp, editor.getToolkit());
         if (sma.isTeamWorkflow()) {
            actionableItemHeader = new SMAActionableItemHeader(headerComp, editor.getToolkit(), sma);
         }
         workflowMetricsHeader = new SMAWorkflowMetricsHeader(headerComp, editor.getToolkit(), sma);
         int headerCompColumns = 4;
         createSMANotesHeader(headerComp, editor.getToolkit(), sma, headerCompColumns);
         createStateNotesHeader(headerComp, editor.getToolkit(), sma, headerCompColumns, null);
         createAnnotationsHeader(headerComp, editor.getToolkit());

         sections.clear();
         atsWorkPages.clear();

         if (SMARelationsHyperlinkComposite.relationExists(sma)) {
            smaRelationsComposite = new SMARelationsHyperlinkComposite(atsBody, SWT.NONE, editor);
            smaRelationsComposite.create(sma);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   protected boolean isShowTargetedVersion() throws OseeCoreException {
      return sma.isTargetedVersionable();
   }

   private void createTargetVersionAndAssigneeHeader(Composite parent, AtsWorkPage page, XFormToolkit toolkit) throws OseeCoreException {
      boolean isShowTargetedVersion = isShowTargetedVersion();
      boolean isCurrentNonCompleteCanceledState = page.isCurrentNonCompleteCancelledState(sma);
      if (!isShowTargetedVersion && !isCurrentNonCompleteCanceledState) {
         return;
      }

      Composite comp = toolkit.createContainer(parent, 6);
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      comp.setLayout(ALayout.getZeroMarginLayout(6, false));

      // Targeted Version
      if (isShowTargetedVersion) {
         new SMATargetedVersionHeader(comp, SWT.NONE, sma, editor);
         toolkit.createLabel(comp, "    ");
      }

      // Create Privileged Edit label
      if (editor.isPriviledgedEditModeEnabled()) {
         Label label = toolkit.createLabel(comp, "(Priviledged Edit Enabled)");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         label.setToolTipText("Priviledged Edit Mode is Enabled.  Editing any field in any state is authorized.  Select icon to disable");
      }

      // Current Assignees
      if (isCurrentNonCompleteCanceledState) {
         boolean editable = !sma.isCompletedOrCancelled() && !sma.isReadOnly() &&
         // and access control writeable
         sma.isAccessControlWrite() && //

         (SMAWorkFlowSection.isEditable(sma, page, editor) || //
         // page is define to allow anyone to edit
         sma.getWorkPageDefinition().hasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsAllowAssigneeToAll.name()) ||
         // team definition has allowed anyone to edit
         sma.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsAllowAssigneeToAll.name()));

         new SMAAssigneesHeader(comp, SWT.NONE, sma, editable, editor);
      }
   }

   private void addMessageDecoration(ScrolledForm form) {
      form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() {

         @Override
         public void linkActivated(HyperlinkEvent e) {
            String title = e.getLabel();
            Object href = e.getHref();
            if (href instanceof IMessage[]) {
               Point noteLocation = ((Control) e.widget).toDisplay(0, 0);
               noteLocation.x += 10;
               noteLocation.y += 10;

               MessageSummaryNote note = new MessageSummaryNote(getManagedForm(), title, (IMessage[]) href);
               note.setLocation(noteLocation);
               note.open();
            }
         }

      });
   }

   private void refreshToolbar() throws OseeCoreException {
      IToolBarManager toolBarMgr = managedForm.getForm().getToolBarManager();
      toolBarMgr.removeAll();

      if (sma.isTeamWorkflow() && (((TeamWorkFlowArtifact) sma).getBranchMgr().isCommittedBranchExists() || ((TeamWorkFlowArtifact) sma).getBranchMgr().isWorkingBranchInWork())) {
         toolBarMgr.add(new ShowMergeManagerAction((TeamWorkFlowArtifact) sma));
         toolBarMgr.add(new ShowChangeReportAction((TeamWorkFlowArtifact) sma));
      }
      toolBarMgr.add(new FavoriteAction(editor));
      if (sma.getParentSMA() != null) {
         toolBarMgr.add(new OpenParentAction(sma));
      }
      toolBarMgr.add(new EmailActionAction(editor));
      toolBarMgr.add(new AddNoteAction(sma, editor));
      toolBarMgr.add(new OpenInAtsWorldAction(sma));
      if (AtsUtil.isAtsAdmin()) {
         toolBarMgr.add(new OpenInArtifactEditorAction(editor));
      }
      toolBarMgr.add(new OpenVersionArtifactAction(sma));
      toolBarMgr.add(new OpenTeamDefinitionAction(sma));
      toolBarMgr.add(new CopyActionDetailsAction(sma));
      toolBarMgr.add(new PrivilegedEditAction(sma, editor));
      toolBarMgr.add(new ResourceHistoryAction(sma));
      toolBarMgr.add(new ReloadAction(sma));

      OseeUiActions.addButtonToEditorToolBar(editor, this, AtsPlugin.PLUGIN_ID,
         managedForm.getForm().getToolBarManager(), SMAEditor.EDITOR_ID, "ATS Editor");

      managedForm.getForm().updateToolBar();
   }

   public Result isXWidgetDirty() throws OseeCoreException {
      for (SMAWorkFlowSection section : sections) {
         Result result = section.isXWidgetDirty();
         if (result.isTrue()) {
            return result;
         }
      }
      return Result.FalseResult;
   }

   public Result isXWidgetSavable() {
      for (SMAWorkFlowSection section : sections) {
         Result result = section.isXWidgetSavable();
         if (result.isFalse()) {
            return result;
         }
      }
      return Result.TrueResult;
   }

   public void saveXWidgetToArtifact() throws OseeCoreException {
      List<IArtifactStoredWidget> artWidgets = new ArrayList<IArtifactStoredWidget>();
      // Collect all dirty widgets first (so same attribute shown on different sections don't colide
      for (SMAWorkFlowSection section : sections) {
         section.getDirtyIArtifactWidgets(artWidgets);
      }
      for (IArtifactStoredWidget widget : artWidgets) {
         widget.saveToArtifact();
      }
   }

   @Override
   public void dispose() {
      if (actionableItemHeader != null) {
         actionableItemHeader.dispose();
      }
      if (workflowMetricsHeader != null) {
         workflowMetricsHeader.dispose();
      }
      if (smaDetailsSection != null) {
         smaDetailsSection.dispose();
      }
      if (smaHistorySection != null) {
         smaHistorySection.dispose();
      }
      if (smaRelationsSection != null) {
         smaRelationsSection.dispose();
      }
      if (smaGoalMembersSection != null) {
         smaGoalMembersSection.dispose();
      }
      for (SMAWorkFlowSection section : sections) {
         section.dispose();
      }

      if (editor.getToolkit() != null) {
         editor.getToolkit().dispose();
      }
   }

   @Override
   public String getActionDescription() {
      return "Workflow Tab";
   }

   private Control control = null;

   private void storeScrollLocation() {
      if (managedForm != null && managedForm.getForm() != null) {
         Integer selection = managedForm.getForm().getVerticalBar().getSelection();
         // System.out.println("Storing selection => " + selection);
         guidToScrollLocation.put(sma.getGuid(), selection);
      }
   }

   private class JumpScrollbarJob extends Job {
      public JumpScrollbarJob(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               Integer selection = guidToScrollLocation.get(sma.getGuid());
               // System.out.println("Restoring selection => " + selection);

               // Find the ScrolledComposite operating on the control.
               ScrolledComposite sComp = null;
               if (control == null || control.isDisposed()) {
                  return;
               }
               Composite parent = control.getParent();
               while (parent != null) {
                  if (parent instanceof ScrolledComposite) {
                     sComp = (ScrolledComposite) parent;
                     break;
                  }
                  parent = parent.getParent();
               }

               if (sComp != null) {
                  sComp.setOrigin(0, selection);
               }
            }
         });
         return Status.OK_STATUS;

      }
   }

   private void createCurrentStateAndTeamHeaders(Composite comp, XFormToolkit toolkit) {
      Composite topLineComp = new Composite(comp, SWT.NONE);
      topLineComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      topLineComp.setLayout(ALayout.getZeroMarginLayout(3, false));
      toolkit.adapt(topLineComp);

      try {
         FormsUtil.createLabelText(toolkit, topLineComp, "Current State: ", sma.getStateMgr().getCurrentStateName());
         FormsUtil.createLabelText(toolkit, topLineComp, "Created: ", DateUtil.getMMDDYYHHMM(sma.getCreatedDate()));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      new SMAOriginatorHeader(topLineComp, SWT.NONE, sma, editor);

      try {
         if (sma.isTeamWorkflow()) {
            FormsUtil.createLabelText(toolkit, topLineComp, "Team: ", ((TeamWorkFlowArtifact) sma).getTeamName());
         } else if ((sma.isTask() || sma.isReview()) && sma.getParentSMA() != null) {
            FormsUtil.createLabelText(toolkit, topLineComp, "Parent Id: ", sma.getParentSMA().getPcrId());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      FormsUtil.createLabelText(toolkit, topLineComp, sma.getArtifactSuperTypeName() + " Id: ",
         sma.getHumanReadableId());

      try {
         if (Strings.isValid(sma.getPcrId())) {
            FormsUtil.createLabelText(toolkit, topLineComp, " Id: ", sma.getPcrId());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createLatestHeader(Composite comp, XFormToolkit toolkit) {
      if (sma.isHistoricalVersion()) {
         Label label =
            toolkit.createLabel(
               comp,
               "This is a historical version of this " + sma.getArtifactTypeName() + " and can not be edited; Select \"Open Latest\" to view/edit latest version.");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      }
   }

   private void createAnnotationsHeader(Composite comp, XFormToolkit toolkit) {
      if (sma.getAnnotations().size() > 0) {
         new AnnotationComposite(toolkit, comp, SWT.None, sma);
      }
   }

   public static void createSMANotesHeader(Composite comp, XFormToolkit toolkit, AbstractWorkflowArtifact sma, int horizontalSpan) throws OseeCoreException {
      // Display SMA Note
      String note = sma.getSoleAttributeValue(AtsAttributeTypes.SmaNote, "");
      if (!note.equals("")) {
         FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, "Note: " + note);
      }
   }

   public static void createStateNotesHeader(Composite comp, XFormToolkit toolkit, AbstractWorkflowArtifact sma, int horizontalSpan, String forStateName) {
      // Display global Notes
      for (NoteItem noteItem : sma.getNotes().getNoteItems()) {
         if (forStateName == null || noteItem.getState().equals(forStateName)) {
            FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, noteItem.toString());
         }
      }
   }

   public void refresh() {
      if (editor != null && !sma.isInTransition()) {
         // remove all pages
         for (SMAWorkFlowSection section : sections) {
            section.dispose();
         }
         // add pages back
         refreshData();
      }
   }

   public List<AtsWorkPage> getPages() {
      return atsWorkPages;
   }

   public List<SMAWorkFlowSection> getSections() {
      return sections;
   }

   public SMAWorkFlowSection getSectionForCurrentState() {
      return null;
   }
}