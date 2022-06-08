/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.ide.config.AtsBulkLoad;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeHeaderComposite;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeHistorySection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeOperationsSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeRelationsSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeUndefinedStateSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeWorkflowSection;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.WorkflowManager;
import org.eclipse.osee.ats.ide.world.IWorldViewerEventHandler;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.EditorWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class WfeWorkFlowTab extends WfeAbstractTab implements IWorldViewerEventHandler {
   private final AbstractWorkflowArtifact awa;
   private final List<WfeWorkflowSection> stateSections = new ArrayList<>();
   private final List<StateXWidgetPage> statePages = new ArrayList<>();
   private IManagedForm managedForm;
   private Composite bodyComp;
   private Composite atsBody;
   private LoadingComposite loadingComposite;
   public final static String ID = "ats.workflow.tab";
   private final WorkflowEditor editor;
   private final List<WfeUndefinedStateSection> undefinedStateSections = new ArrayList<>();
   private WfeRelationsSection relationsSection;
   private WfeHistorySection historySection;

   public WfeWorkFlowTab(WorkflowEditor editor, AbstractWorkflowArtifact awa) {
      super(editor, ID, awa, "Workflow");
      this.editor = editor;
      this.awa = awa;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      try {
         updateTitleBar(managedForm);

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

         List<IOperation> ops = new ArrayList<>();
         ops.addAll(AtsBulkLoad.getConfigLoadingOperations());
         IOperation operation = Operations.createBuilder("Load Workflow Tab").addAll(ops).build();
         Operations.executeAsJob(operation, false, Job.LONG, new ReloadJobChangeAdapter(editor));

      } catch (Exception ex) {
         handleException(ex);
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
                     updateTitleBar(managedForm);
                     createToolbar(managedForm);
                     setLoading(false);
                     createAtsBody();
                     XWidgetUtility.addMessageDecoration(managedForm, managedForm.getForm());
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

   @Override
   public void handleException(Exception ex) {
      setLoading(false);
      if (Widgets.isAccessible(atsBody)) {
         atsBody.dispose();
      }
      OseeLog.log(Activator.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   @Override
   public void setLoading(boolean set) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
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
      });
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

      setEditorWidgets();
      atsBody.layout();
      atsBody.setFocus();
   }

   private void setEditorWidgets() {
      if (Widgets.isAccessible(headerComp)) {
         Collection<XWidget> headerWidgets = headerComp.getXWidgets(new ArrayList<XWidget>());
         for (XWidget widget : headerWidgets) {
            if (widget instanceof EditorWidget) {
               ((EditorWidget) widget).setEditorData(editor);
            }
         }
      }
      List<StateXWidgetPage> statePages = getStatePages();
      for (StateXWidgetPage currStatePage : statePages) {
         Collection<XWidget> updateWidgets = currStatePage.getDynamicXWidgetLayout().getXWidgets();
         for (XWidget widget : updateWidgets) {
            if (widget instanceof EditorWidget) {
               ((EditorWidget) widget).setEditorData(editor);
            }
         }
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
      if (!AtsApiService.get().getUserService().isAtsAdmin()) {
         try {
            relationsSection = new WfeRelationsSection(editor, atsBody, editor.getToolkit(), SWT.NONE);
            managedForm.addPart(relationsSection);

         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
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
         historySection = new WfeHistorySection(editor, atsBody, editor.getToolkit(), SWT.NONE);
         managedForm.addPart(historySection);
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
      stateSections.add(section);
      statePages.add(statePage);
   }

   public XResultData isXWidgetDirty(XResultData rd) {
      if (Widgets.isAccessible(headerComp)) {
         rd.log("======> WFE - Header\n");
         headerComp.isXWidgetDirty(rd);
      }
      for (WfeWorkflowSection section : stateSections) {
         rd.logf("======> WFE Section - %s\n", section.getStatePage().getName());
         section.isXWidgetDirty(rd);
      }
      return rd;
   }

   public Result isXWidgetSavable() {
      Result result = null;
      if (Widgets.isAccessible(headerComp)) {
         result = headerComp.isXWidgetSavable();
      }
      for (WfeWorkflowSection section : stateSections) {
         result = section.isXWidgetSavable();
         if (result.isFalse()) {
            return result;
         }
      }
      return Result.TrueResult;
   }

   public void saveXWidgetToArtifact() {
      List<ArtifactStoredWidget> artWidgets = new ArrayList<>();
      headerComp.getDirtyIArtifactWidgets(artWidgets);
      // Collect all dirty widgets first (so same attribute shown on different sections don't colide
      for (WfeWorkflowSection section : stateSections) {
         section.getDirtyIArtifactWidgets(artWidgets);
      }
      for (ArtifactStoredWidget widget : artWidgets) {
         widget.saveToArtifact();
      }
   }

   @Override
   public void dispose() {
      if (Widgets.isAccessible(headerComp)) {
         headerComp.dispose();
      }
      for (WfeUndefinedStateSection section : undefinedStateSections) {
         section.dispose();
      }
      if (historySection != null) {
         historySection.dispose();
      }
      if (relationsSection != null) {
         relationsSection.dispose();
      }
      for (WfeWorkflowSection section : stateSections) {
         section.dispose();
      }
   }

   private WfeHeaderComposite headerComp;

   public WfeWorkflowSection getCurrentStateSection() {
      for (WfeWorkflowSection section : stateSections) {
         if (section.getPage().getName().equals(editor.getWorkItem().getCurrentStateName())) {
            return section;
         }
      }
      return null;
   }

   @Override
   public void refresh() {
      if (editor != null && Widgets.isAccessible(headerComp)) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               String stateName = awa.getCurrentStateName();

               // Determine if state already exists
               boolean found = false;
               for (WfeWorkflowSection section : stateSections) {
                  if (section.getPage().getName().equals(stateName)) {
                     found = true;
                  }
               }

               headerComp.refresh();

               // Create state if not exist
               if (!found) {
                  Pair<StateXWidgetPage, Composite> pageAndComp = stateNameToPageAndComposite.get(stateName);
                  if (pageAndComp != null) {
                     StateXWidgetPage statePage = pageAndComp.getFirst();
                     createStateSection(pageAndComp.getSecond(), statePage);
                  }
               }

               for (WfeWorkflowSection section : stateSections) {
                  section.refresh();
               }
               refreshExpandStates();

               if (relationsSection != null) {
                  relationsSection.refresh();
               }
               historySection.refresh();
            }

         });
      }
   }

   public void refreshExpandStates() {
      for (WfeWorkflowSection wfeSection : stateSections) {
         if (!Widgets.isAccessible(wfeSection.getMainComp())) {
            continue;
         }
         boolean isCurrentState = wfeSection.isCurrentState();
         if (isCurrentState) {
            wfeSection.expand();
         } else {
            if (Widgets.isAccessible(wfeSection.getSection())) {
               wfeSection.getSection().setExpanded(false);
            }
         }
      }
      if (Widgets.isAccessible(bodyComp)) {
         bodyComp.layout(true, true);
         bodyComp.getParent().layout(true, true);
      }
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

   public void computeSizeAndReflow() {
      for (WfeWorkflowSection section : stateSections) {
         section.computeTextSizesAndReflow();
      }
   }

   @Override
   public void handleColumnEvents(ArtifactEvent artifactEvent, WorldXViewer worldXViewer) {
      // no columns in WorkflowTab
   }

}
