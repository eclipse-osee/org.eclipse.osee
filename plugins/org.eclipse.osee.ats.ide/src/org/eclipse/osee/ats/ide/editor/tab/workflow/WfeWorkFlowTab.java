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

package org.eclipse.osee.ats.ide.editor.tab.workflow;

import java.util.ArrayList;
import java.util.Collection;
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
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.event.IAtsWorkItemTopicEventListener;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.actions.AddNoteAction;
import org.eclipse.osee.ats.ide.actions.CloneWorkflowAction;
import org.eclipse.osee.ats.ide.actions.CopyActionDetailsAction;
import org.eclipse.osee.ats.ide.actions.EmailActionAction;
import org.eclipse.osee.ats.ide.actions.FavoriteAction;
import org.eclipse.osee.ats.ide.actions.OpenInArtifactEditorAction;
import org.eclipse.osee.ats.ide.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.ide.actions.OpenInBrowserAction;
import org.eclipse.osee.ats.ide.actions.OpenParentAction;
import org.eclipse.osee.ats.ide.actions.OpenTeamDefinitionAction;
import org.eclipse.osee.ats.ide.actions.OpenVersionArtifactAction;
import org.eclipse.osee.ats.ide.actions.ReloadAction;
import org.eclipse.osee.ats.ide.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.ide.actions.ShowChangeReportAction;
import org.eclipse.osee.ats.ide.actions.ShowMergeManagerAction;
import org.eclipse.osee.ats.ide.config.AtsBulkLoad;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.relations.WfeRelationsSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeHeaderComposite;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeDetailsSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeHistorySection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeOperationsSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeUndefinedStateSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeWorkflowSection;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.walker.action.OpenActionViewAction;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.WorkflowManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.IWorldViewerEventHandler;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.MessageSummaryNote;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactStoredWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
public class WfeWorkFlowTab extends FormPage implements IWorldViewerEventHandler, IAtsWorkItemTopicEventListener {
   private final AbstractWorkflowArtifact awa;
   private final List<WfeWorkflowSection> sections = new ArrayList<>();
   private final List<StateXWidgetPage> statePages = new ArrayList<>();
   private IManagedForm managedForm;
   private Composite bodyComp;
   private Composite atsBody;
   private LoadingComposite loadingComposite;
   public final static String ID = "ats.workflow.tab";
   private final WorkflowEditor editor;
   private final List<WfeUndefinedStateSection> undefinedStateSections = new ArrayList<>();
   private WfeDetailsSection smaDetailsSection;
   private WfeRelationsSection smaRelationsSection;
   private WfeHistorySection smaHistorySection;

   public WfeWorkFlowTab(WorkflowEditor editor, AbstractWorkflowArtifact awa) {
      super(editor, ID, "Workflow");
      this.editor = editor;
      this.awa = awa;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      try {
         updateTitleBar();

         bodyComp = managedForm.getForm().getBody();
         GridLayout gridLayout = new GridLayout(1, false);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, false);
         gd.widthHint = 300;
         bodyComp.setLayoutData(gd);

         setLoading(true);
         if (awa.isTypeEqual(AtsArtifactTypes.DecisionReview)) {
            HelpUtil.setHelp(managedForm.getForm(), AtsHelpContext.DECISION_REVIEW);

         } else if (awa.isTypeEqual(AtsArtifactTypes.PeerToPeerReview)) {
            HelpUtil.setHelp(managedForm.getForm(), AtsHelpContext.PEER_TO_PEER_REVIEW);

         } else {
            HelpUtil.setHelp(managedForm.getForm(), AtsHelpContext.WORKFLOW_EDITOR__WORKFLOW_TAB);
         }

         AtsClientService.get().getEventService().registerAtsWorkItemTopicEvent(this,
            AtsTopicEvent.WORK_ITEM_TRANSITIONED, AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED);

         List<IOperation> ops = new ArrayList<>();
         ops.addAll(AtsBulkLoad.getConfigLoadingOperations());
         IOperation operation = Operations.createBuilder("Load Workflow Tab").addAll(ops).build();
         Operations.executeAsJob(operation, false, Job.LONG, new ReloadJobChangeAdapter(editor));

         // Register for events and deregister on dispose
         AtsClientService.get().getEventService().registerAtsWorkItemTopicEvent(this,
            AtsTopicEvent.WORK_ITEM_TRANSITIONED, AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED);
         final WfeWorkFlowTab fThis = this;
         bodyComp.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
               AtsClientService.get().getEventService().deRegisterAtsWorkItemTopicEvent(fThis);
            }
         });

      } catch (Exception ex) {
         handleException(ex);
      }
   }

   private void updateTitleBar() {
      if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
         String titleString = editor.getTitleStr();
         String displayableTitle = Strings.escapeAmpersands(titleString);
         managedForm.getForm().setToolTipText(displayableTitle);
         String artifactTypeName = awa.isTeamWorkflow() ? "Team Workflow" : awa.getArtifactTypeName();
         String formTitle = null;
         if (awa.getParentTeamWorkflow() != null) {
            formTitle = String.format("%s - %s", awa.getParentTeamWorkflow().getTeamDefinition(), artifactTypeName);
         } else {
            formTitle = String.format("%s", artifactTypeName);
         }
         managedForm.getForm().setText(formTitle);
         if (AtsClientService.get().getAgileService().isBacklog(awa)) {
            managedForm.getForm().setImage(
               ImageManager.getImage(AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_BACKLOG)));
         } else {
            managedForm.getForm().setImage(ArtifactImageManager.getImage(awa));
         }
      }
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      IManagedForm managedForm = getManagedForm();
      if (managedForm != null && Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final WorkflowEditor editor;

      private ReloadJobChangeAdapter(WorkflowEditor editor) {
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
                  if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
                     updateTitleBar();
                     refreshToolbar();
                     setLoading(false);
                     createAtsBody();
                     addMessageDecoration(managedForm.getForm());
                     FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);
                     editor.onDirtied();
                  }
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
      OseeLog.log(Activator.class, Level.SEVERE, ex);
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

   private void createAtsBody() {
      if (Widgets.isAccessible(atsBody)) {
         if (getManagedForm() != null && getManagedForm().getMessageManager() != null) {
            getManagedForm().getMessageManager().removeAllMessages();
         }
         atsBody.dispose();
      }
      atsBody = editor.getToolkit().createComposite(bodyComp);
      atsBody.setLayoutData(new GridData(GridData.FILL_BOTH));
      atsBody.setLayout(new GridLayout(1, false));

      StateXWidgetPage page = WorkflowManager.getCurrentAtsWorkPage(awa);
      if (page == null) {
         OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP,
            "Can't retrieve current page from current state [%s] of work definition [%s]", awa.getCurrentStateName(),
            awa.getWorkDefinition().getName());
      }

      headerComp =
         new WfeHeaderComposite(atsBody, SWT.NONE, editor, WorkflowManager.getCurrentAtsWorkPage(awa), managedForm);
      headerComp.create();

      createPageSections();
      createUndefinedStateSections();
      createHistorySection();
      createRelationsSection();
      createOperationsSection();
      createDetailsSection();

      atsBody.layout();
      atsBody.setFocus();
   }

   private void createDetailsSection() {
      try {
         smaDetailsSection = new WfeDetailsSection(editor, atsBody, editor.getToolkit(), SWT.NONE);
         managedForm.addPart(smaDetailsSection);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createOperationsSection() {
      try {
         WfeOperationsSection smaOperationsSection =
            new WfeOperationsSection(editor, atsBody, editor.getToolkit(), SWT.NONE);
         managedForm.addPart(smaOperationsSection);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createRelationsSection() {
      try {
         smaRelationsSection = new WfeRelationsSection(editor, atsBody, editor.getToolkit(), SWT.NONE);
         managedForm.addPart(smaRelationsSection);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createUndefinedStateSections() {
      try {
         if (WfeUndefinedStateSection.hasUndefinedStates(editor.getWorkItem())) {
            for (String stateName : WfeUndefinedStateSection.getUndefinedStateNames(awa)) {
               WfeUndefinedStateSection section =
                  new WfeUndefinedStateSection(stateName, editor, atsBody, editor.getToolkit(), SWT.NONE);
               managedForm.addPart(section);
               undefinedStateSections.add(section);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createHistorySection() {
      try {
         smaHistorySection = new WfeHistorySection(editor, atsBody, editor.getToolkit(), SWT.NONE);
         managedForm.addPart(smaHistorySection);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private final Map<String, Pair<StateXWidgetPage, Composite>> stateNameToPageAndComposite = new HashMap<>();

   private void createPageSections() {
      try {
         Composite sectionsComp = editor.getToolkit().createComposite(atsBody);
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.widthHint = 100;
         sectionsComp.setLayoutData(gd);
         sectionsComp.setLayout(ALayout.getZeroMarginLayout(1, false));

         for (StateXWidgetPage statePage : WorkflowManager.getStatePagesOrderedByOrdinal(awa)) {
            try {
               // Only display current or past states
               if (awa.isInState(statePage) || awa.getStateMgr().isStateVisited(statePage)) {
                  createStateSection(sectionsComp, statePage);
               }
               // Else make placeholder for state transition
               else {
                  Composite placeHolderComp = editor.getToolkit().createComposite(sectionsComp);
                  gd = new GridData(GridData.FILL_HORIZONTAL);
                  gd.widthHint = 100;
                  placeHolderComp.setLayoutData(gd);
                  placeHolderComp.setLayout(ALayout.getZeroMarginLayout(1, false));
                  stateNameToPageAndComposite.put(statePage.getName(),
                     new Pair<StateXWidgetPage, Composite>(statePage, placeHolderComp));
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createStateSection(Composite sectionsComp, StateXWidgetPage statePage) {
      WfeWorkflowSection section = new WfeWorkflowSection(sectionsComp, SWT.NONE, statePage, awa, editor);
      managedForm.addPart(section);
      sections.add(section);
      statePages.add(statePage);
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

   private void refreshToolbar() {
      IToolBarManager toolBarMgr = managedForm.getForm().getToolBarManager();
      toolBarMgr.removeAll();

      if (awa.isTeamWorkflow() && (AtsClientService.get().getBranchService().isCommittedBranchExists(
         (TeamWorkFlowArtifact) awa) || AtsClientService.get().getBranchService().isWorkingBranchInWork(
            (TeamWorkFlowArtifact) awa))) {
         toolBarMgr.add(new ShowMergeManagerAction((TeamWorkFlowArtifact) awa));
         toolBarMgr.add(new ShowChangeReportAction((TeamWorkFlowArtifact) awa));
      }
      toolBarMgr.add(new FavoriteAction(editor));
      if (awa.getParentAWA() != null) {
         toolBarMgr.add(new OpenParentAction(awa));
      }
      toolBarMgr.add(new EmailActionAction(editor));
      toolBarMgr.add(new AddNoteAction(awa, editor));
      toolBarMgr.add(new OpenInAtsWorldAction(awa));
      toolBarMgr.add(new OpenActionViewAction());
      if (AtsClientService.get().getUserService().isAtsAdmin()) {
         toolBarMgr.add(new OpenInArtifactEditorAction(editor));
      }
      toolBarMgr.add(new OpenVersionArtifactAction(awa));
      if (awa instanceof TeamWorkFlowArtifact) {
         toolBarMgr.add(new OpenTeamDefinitionAction((TeamWorkFlowArtifact) awa));
      }
      toolBarMgr.add(new CopyActionDetailsAction(awa, AtsClientService.get()));
      toolBarMgr.add(new OpenInBrowserAction(awa));
      toolBarMgr.add(new ResourceHistoryAction(awa));
      if (awa.isTeamWorkflow()) {
         toolBarMgr.add(new CloneWorkflowAction((TeamWorkFlowArtifact) awa, null));
      }
      toolBarMgr.add(new ReloadAction(awa, editor));

      managedForm.getForm().updateToolBar();
   }

   public Result isXWidgetDirty() {
      Result result = null;
      if (Widgets.isAccessible(headerComp)) {
         result = headerComp.isXWidgetDirty();
         if (result != null && result.isTrue()) {
            return result;
         }
      }
      for (WfeWorkflowSection section : sections) {
         result = section.isXWidgetDirty();
         if (result.isTrue()) {
            return result;
         }
      }
      return Result.FalseResult;
   }

   public Result isXWidgetSavable() {
      Result result = null;
      if (Widgets.isAccessible(headerComp)) {
         result = headerComp.isXWidgetSavable();
      }
      for (WfeWorkflowSection section : sections) {
         result = section.isXWidgetSavable();
         if (result.isFalse()) {
            return result;
         }
      }
      return Result.TrueResult;
   }

   public void saveXWidgetToArtifact() {
      List<IArtifactStoredWidget> artWidgets = new ArrayList<>();
      headerComp.getDirtyIArtifactWidgets(artWidgets);
      // Collect all dirty widgets first (so same attribute shown on different sections don't colide
      for (WfeWorkflowSection section : sections) {
         section.getDirtyIArtifactWidgets(artWidgets);
      }
      for (IArtifactStoredWidget widget : artWidgets) {
         widget.saveToArtifact();
      }
   }

   @Override
   public void dispose() {
      if (Widgets.isAccessible(headerComp)) {
         headerComp.dispose();
      }
      if (smaDetailsSection != null) {
         smaDetailsSection.dispose();
      }
      for (WfeUndefinedStateSection section : undefinedStateSections) {
         section.dispose();
      }
      if (smaHistorySection != null) {
         smaHistorySection.dispose();
      }
      if (smaRelationsSection != null) {
         smaRelationsSection.dispose();
      }
      for (WfeWorkflowSection section : sections) {
         section.dispose();
      }

      if (editor.getToolkit() != null) {
         editor.getToolkit().dispose();
      }
   }

   private WfeHeaderComposite headerComp;

   public WfeWorkflowSection getCurrentStateSection() {
      for (WfeWorkflowSection section : sections) {
         if (section.getPage().getName().equals(editor.getWorkItem().getCurrentStateName())) {
            return section;
         }
      }
      return null;
   }

   public void refresh() {
      if (editor != null) {
         String stateName = awa.getCurrentStateName();

         // Determine if state already exists
         boolean found = false;
         for (WfeWorkflowSection section : sections) {
            if (section.getPage().getName().equals(stateName)) {
               found = true;
            }
         }

         // Create state if not exist
         if (!found) {
            Pair<StateXWidgetPage, Composite> pageAndComp = stateNameToPageAndComposite.get(stateName);
            StateXWidgetPage statePage = pageAndComp.getFirst();
            createStateSection(pageAndComp.getSecond(), statePage);
         }
         for (WfeWorkflowSection section : sections) {
            section.refresh();
         }
      }
   }

   public void refreshExpandStates() {
      for (WfeWorkflowSection section : sections) {
         boolean isCurrentState = section.isCurrentState();
         if (isCurrentState) {
            section.expand();
         } else {
            section.getSection().setExpanded(false);
         }
      }
      bodyComp.layout(true, true);
      bodyComp.getParent().layout(true, true);
   }

   @Override
   public void relationsModifed(Collection<Artifact> relModifiedArts, Collection<Artifact> goalMemberReordered, Collection<Artifact> sprintMemberReordered) {
      if (relModifiedArts.contains(awa)) {
         refresh();
      }
   }

   @Override
   public boolean isDisposed() {
      return editor.isDisposed();
   }

   public WfeHeaderComposite getHeader() {
      return headerComp;
   }

   public List<StateXWidgetPage> getStatePages() {
      return statePages;
   }

   @Override
   public void handleEvent(AtsTopicEvent topicEvent, Collection<ArtifactId> workItems) {
      if (topicEvent.equals(AtsTopicEvent.WORK_ITEM_TRANSITIONED) || topicEvent.equals(
         AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED)) {
         if (this.isDisposed()) {
            AtsClientService.get().getEventService().deRegisterAtsWorkItemTopicEvent(this);
            return;
         }
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               refresh();
               refreshExpandStates();
            }
         });
      }
   }

}