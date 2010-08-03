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
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.NoteItem;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.AtsBulkLoad;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AnnotationComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.MessageSummaryNote;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.skynet.util.OseeDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
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
   private final StateMachineArtifact sma;
   private final ArrayList<SMAWorkFlowSection> sections = new ArrayList<SMAWorkFlowSection>();
   private final XFormToolkit toolkit;
   private final List<AtsWorkPage> atsWorkPages = new ArrayList<AtsWorkPage>();
   private ScrolledForm scrolledForm;
   private final Integer HEADER_COMP_COLUMNS = 4;
   private static Map<String, Integer> guidToScrollLocation = new HashMap<String, Integer>();
   private SMARelationsHyperlinkComposite smaRelationsComposite;
   private IManagedForm managedForm;
   private Composite bodyComp;
   private Composite atsBody;
   private SMAActionableItemHeader actionableItemHeader;
   private SMAWorkflowMetricsHeader workflowMetricsHeader;
   private SMADetailsSection smaDetailsSection;
   private SMARelationsSection smaRelationsSection;
   private SMAOperationsSection smaOperationsSection;
   private SMAGoalMembersSection smaGoalMembersSection;
   private SMAHistorySection smaHistorySection;
   private LoadingComposite loadingComposite;
   private static String PRIVILEGED_EDIT = "(Priviledged Edit Enabled)";

   public SMAWorkFlowTab(StateMachineArtifact sma) {
      super(sma.getEditor(), "overview", "Workflow");
      this.sma = sma;
      toolkit = sma.getEditor().getToolkit();
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      try {
         scrolledForm = managedForm.getForm();
         scrolledForm.addDisposeListener(new DisposeListener() {
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
            AtsPlugin.getInstance().setHelp(scrolledForm, sma.getHelpContext(), "org.eclipse.osee.ats.help.ui");
         }

         refreshData();

      } catch (Exception ex) {
         handleException(ex);
      }
   }

   private void updateTitleBar() throws OseeCoreException {
      String titleString = sma.getEditor().getTitleStr();
      String displayableTitle = Strings.escapeAmpersands(titleString);
      scrolledForm.setText(displayableTitle);
      scrolledForm.setImage(ArtifactImageManager.getImage(sma));
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   public void refreshData() {
      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(AtsBulkLoad.getConfigLoadingOperation());
      IOperation operation = new CompositeOperation("Load SMA Workflow Tab", AtsPlugin.PLUGIN_ID, ops);
      Operations.executeAsJob(operation, true, Job.LONG, new ReloadJobChangeAdapter(sma.getEditor()));

      // Don't put in operation cause doesn't have to be loaded before editor displays
      OseeDictionary.load();
   }
   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final IDirtiableEditor editor;

      private ReloadJobChangeAdapter(IDirtiableEditor editor) {
         this.editor = editor;
         showBusy(true);
      }

      @Override
      public void scheduled(IJobChangeEvent event) {
         super.scheduled(event);
      }

      @Override
      public void aboutToRun(IJobChangeEvent event) {
         super.aboutToRun(event);
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
                  addMessageDecoration(scrolledForm);
                  FormsUtil.addHeadingGradient(toolkit, scrolledForm, true);
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
      atsBody = toolkit.createComposite(bodyComp);
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
         smaDetailsSection = new SMADetailsSection(sma.getEditor(), atsBody, toolkit, SWT.NONE);
         managedForm.addPart(smaDetailsSection);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createOperationsSection() {
      try {
         smaOperationsSection = new SMAOperationsSection(sma.getEditor(), atsBody, toolkit, SWT.NONE);
         managedForm.addPart(smaOperationsSection);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createRelationsSection() {
      try {
         smaRelationsSection = new SMARelationsSection(sma.getEditor(), atsBody, toolkit, SWT.NONE);
         managedForm.addPart(smaRelationsSection);

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createHistorySection() {
      try {
         smaHistorySection = new SMAHistorySection(sma.getEditor(), atsBody, toolkit, SWT.NONE);
         managedForm.addPart(smaHistorySection);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createGoalSection() {
      try {
         if (sma instanceof GoalArtifact) {
            smaGoalMembersSection = new SMAGoalMembersSection(sma.getEditor(), atsBody, toolkit, SWT.NONE);
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
               if (sma.isCurrentState(atsWorkPage.getName()) || sma.getStateMgr().isStateVisited(atsWorkPage.getName())) {
                  // Don't show completed or cancelled state if not currently those state
                  if (atsWorkPage.isCompletePage() && !sma.isCompleted()) {
                     continue;
                  }
                  if (atsWorkPage.isCancelledPage() && !sma.isCancelled()) {
                     continue;
                  }
                  SMAWorkFlowSection section = new SMAWorkFlowSection(atsBody, toolkit, SWT.NONE, atsWorkPage, sma);
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
      Composite headerComp = toolkit.createComposite(atsBody);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 100;
      headerComp.setLayoutData(gd);
      headerComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      // mainComp.setBackground(Displays.getSystemColor(SWT.COLOR_RED));

      // Display relations
      try {
         createCurrentStateAndTeamHeaders(headerComp, toolkit);
         createTargetVersionAndAssigneeHeader(headerComp, currentAtsWorkPage, toolkit);

         createLatestHeader(headerComp, toolkit);
         if (sma.isTeamWorkflow()) {
            actionableItemHeader = new SMAActionableItemHeader(headerComp, toolkit, sma);
         }
         workflowMetricsHeader = new SMAWorkflowMetricsHeader(headerComp, toolkit, sma);
         createSMANotesHeader(headerComp, toolkit, sma, HEADER_COMP_COLUMNS);
         createStateNotesHeader(headerComp, toolkit, sma, HEADER_COMP_COLUMNS, null);
         createAnnotationsHeader(headerComp, toolkit);

         sections.clear();
         atsWorkPages.clear();

         if (SMARelationsHyperlinkComposite.relationExists(sma)) {
            smaRelationsComposite = new SMARelationsHyperlinkComposite(atsBody, toolkit, SWT.NONE);
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
         new SMATargetedVersionHeader(comp, SWT.NONE, sma, toolkit);
         toolkit.createLabel(comp, "    ");
      }

      // Create Privileged Edit label
      if (sma.getEditor().isPriviledgedEditModeEnabled()) {
         Label label = toolkit.createLabel(comp, PRIVILEGED_EDIT);
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         label.setToolTipText("Priviledged Edit Mode is Enabled.  Editing any field in any state is authorized.  Select icon to disable");
      }

      // Current Assignees
      if (isCurrentNonCompleteCanceledState) {
         boolean editable = !sma.isCancelledOrCompleted() && !sma.isReadOnly() &&
         // and access control writeable
         sma.isAccessControlWrite() && //

         (SMAWorkFlowSection.isEditable(sma, page) || //
         // page is define to allow anyone to edit
         sma.getWorkPageDefinition().hasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsAllowAssigneeToAll.name()) ||
         // team definition has allowed anyone to edit
         sma.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsAllowAssigneeToAll.name()));

         new SMAAssigneesHeader(comp, SWT.NONE, sma, toolkit, editable);
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
      IToolBarManager toolBarMgr = scrolledForm.getToolBarManager();
      toolBarMgr.removeAll();

      if (sma.isTeamWorkflow() && (((TeamWorkFlowArtifact) sma).getBranchMgr().isCommittedBranchExists() || ((TeamWorkFlowArtifact) sma).getBranchMgr().isWorkingBranchInWork())) {
         toolBarMgr.add(new ShowMergeManagerAction((TeamWorkFlowArtifact) sma));
         toolBarMgr.add(new ShowChangeReportAction((TeamWorkFlowArtifact) sma));
      }
      toolBarMgr.add(new FavoriteAction(sma.getEditor()));
      if (sma.getParentSMA() != null) {
         toolBarMgr.add(new OpenParentAction(sma));
      }
      toolBarMgr.add(new EmailActionAction(sma.getEditor()));
      toolBarMgr.add(new AddNoteAction(sma));
      toolBarMgr.add(new OpenInAtsWorldAction(sma));
      if (AtsUtil.isAtsAdmin()) {
         toolBarMgr.add(new OpenInArtifactEditorAction(sma.getEditor()));
      }
      toolBarMgr.add(new OpenVersionArtifactAction(sma));
      toolBarMgr.add(new OpenTeamDefinitionAction(sma));
      toolBarMgr.add(new CopyActionDetailsAction(sma));
      toolBarMgr.add(new PrivilegedEditAction(sma));
      toolBarMgr.add(new ResourceHistoryAction(sma));
      toolBarMgr.add(new ReloadAction(sma));

      OseeUiActions.addButtonToEditorToolBar(sma.getEditor(), this, AtsPlugin.getInstance(),
         scrolledForm.getToolBarManager(), SMAEditor.EDITOR_ID, "ATS Editor");

      scrolledForm.updateToolBar();
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

      if (toolkit != null) {
         toolkit.dispose();
      }
   }

   @Override
   public String getActionDescription() {
      return "Workflow Tab";
   }

   private Control control = null;

   private void storeScrollLocation() {
      if (scrolledForm != null) {
         Integer selection = scrolledForm.getVerticalBar().getSelection();
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
         FormsUtil.createLabelText(toolkit, topLineComp, "Created: ",
            XDate.getDateStr(sma.getLog().getCreationDate(), XDate.MMDDYYHHMM));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      new SMAOriginatorHeader(topLineComp, SWT.NONE, sma, toolkit);

      if (sma.isTeamWorkflow()) {
         FormsUtil.createLabelText(toolkit, topLineComp, "Team: ", ((TeamWorkFlowArtifact) sma).getTeamName());
      }
      FormsUtil.createLabelText(toolkit, topLineComp, sma.getArtifactSuperTypeName() + "Id: ", sma.getHumanReadableId());

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

   public static void createSMANotesHeader(Composite comp, XFormToolkit toolkit, StateMachineArtifact sma, int horizontalSpan) throws OseeCoreException {
      // Display SMA Note
      String note = sma.getSoleAttributeValue(ATSAttributes.SMA_NOTE_ATTRIBUTE.getStoreName(), "");
      if (!note.equals("")) {
         FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, "Note: " + note);
      }
   }

   public static void createStateNotesHeader(Composite comp, XFormToolkit toolkit, StateMachineArtifact sma, int horizontalSpan, String forStateName) {
      // Display global Notes
      for (NoteItem noteItem : sma.getNotes().getNoteItems()) {
         if (forStateName == null || noteItem.getState().equals(forStateName)) {
            FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, noteItem.toString());
         }
      }
   }

   public void refresh() {
      if (sma.getEditor() != null && !sma.isInTransition()) {
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