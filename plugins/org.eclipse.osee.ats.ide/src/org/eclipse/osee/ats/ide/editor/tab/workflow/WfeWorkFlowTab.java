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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.ide.config.AtsBulkLoad;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeHeaderComposite;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeOperationsSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeUndefinedStateSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeWorkflowSection;
import org.eclipse.osee.ats.ide.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.world.IWorldViewerEventHandler;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.EditorWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetUtility;
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
   private IManagedForm managedForm;
   private Composite bodyComp;
   private Composite atsBody;
   private LoadingComposite loadingComposite;
   public final static String ID = "ats.workflow.tab";
   private final WorkflowEditor editor;
   private final List<WfeUndefinedStateSection> undefinedStateSections = new ArrayList<>();
   private final AtsApi atsApi;

   public WfeWorkFlowTab(WorkflowEditor editor, AbstractWorkflowArtifact awa) {
      super(editor, ID, awa, "Workflow");
      this.editor = editor;
      this.awa = awa;
      atsApi = AtsApiService.get();
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
         setHelp(managedForm);

         List<IOperation> ops = new ArrayList<>();
         ops.addAll(AtsBulkLoad.getConfigLoadingOperations());
         IOperation operation = Operations.createBuilder("Load Workflow Tab").addAll(ops).build();
         Operations.executeAsJob(operation, false, Job.LONG, new ReloadJobChangeAdapter(editor));

      } catch (Exception ex) {
         handleException(ex);
      }
   }

   private void setHelp(IManagedForm managedForm) {
      if (awa.isTypeEqual(AtsArtifactTypes.DecisionReview)) {
         HelpUtil.setHelp(managedForm.getForm(), AtsHelpContext.DECISION_REVIEW);

      } else if (awa.isTypeEqual(AtsArtifactTypes.PeerToPeerReview)) {
         HelpUtil.setHelp(managedForm.getForm(), AtsHelpContext.PEER_TO_PEER_REVIEW);

      } else {
         HelpUtil.setHelp(managedForm.getForm(), AtsHelpContext.WORKFLOW_EDITOR__WORKFLOW_TAB);
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

      StateDefinition currentStateDef = getCurrentAtsWorkPage(awa);
      if (currentStateDef == null) {
         OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP,
            "Can't retrieve current page from current state [%s] of work definition [%s]", awa.getCurrentStateName(),
            awa.getWorkDefinition().getName());
      }

      headerComp = new WfeHeaderComposite(atsBody, SWT.NONE, editor, managedForm);
      headerComp.create();

      createPageSections();
      createUndefinedStateSections();
      createOperationsSection();

      setEditorWidgets();
      atsBody.layout();
      // Operations is created last. State-section construction reflows before Operations exists, so a restored editor
      // can cache a scroll extent that ends above its title bar. Reflow once after the complete body is assembled;
      // this is structural construction, not an ordinary refresh, and does not reintroduce no-op refresh flicker.
      managedForm.reflow(true);
      atsBody.setFocus();
   }

   private void setEditorWidgets() {
      if (Widgets.isAccessible(headerComp)) {
         Collection<XWidget> headerWidgets = headerComp.getXWidgets();
         for (XWidget widget : headerWidgets) {
            if (widget instanceof EditorWidget) {
               ((EditorWidget) widget).setEditorData(editor);
               widget.setManagedForm(managedForm);
            }
         }
      }
      for (WfeWorkflowSection currStatePage : stateSections) {
         Collection<XWidget> updateWidgets = currStatePage.getXWidgets();
         for (XWidget widget : updateWidgets) {
            if (widget instanceof EditorWidget) {
               ((EditorWidget) widget).setEditorData(editor);
               widget.setManagedForm(managedForm);
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

   private final Map<String, Composite> stateNameToComposite = new HashMap<>();

   private void createPageSections() {
      try {
         Composite sectionsComp = editor.getToolkit().createComposite(atsBody);
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.widthHint = 100;
         sectionsComp.setLayoutData(gd);
         sectionsComp.setLayout(ALayout.getZeroMarginLayout(1, false));
         XWidgetUtility.debugLayout(sectionsComp, SWT.COLOR_CYAN);

         for (StateDefinition stateDef : getStatePagesOrderedByOrdinal(awa)) {
            try {
               // Only display current or past states
               if (awa.isInState(stateDef) || awa.getLog().isStateVisited(stateDef)) {
                  createStateSection(sectionsComp, stateDef);
               }
               // Else make placeholder for state transition
               else {
                  Composite placeHolderComp = editor.getToolkit().createComposite(sectionsComp);
                  gd = new GridData(GridData.FILL_HORIZONTAL);
                  gd.widthHint = 100;
                  placeHolderComp.setLayoutData(gd);
                  placeHolderComp.setLayout(ALayout.getZeroMarginLayout(1, false));
                  stateNameToComposite.put(stateDef.getName(), placeHolderComp);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private WfeWorkflowSection createStateSection(Composite sectionsComp, StateDefinition statePage) {
      WfeWorkflowSection section = new WfeWorkflowSection(sectionsComp, SWT.NONE, statePage, awa, editor);
      managedForm.addPart(section);
      stateSections.add(section);
      return section;
   }

   @Override
   public void dispose() {
      if (Widgets.isAccessible(headerComp)) {
         headerComp.dispose();
      }
      for (WfeUndefinedStateSection section : undefinedStateSections) {
         section.dispose();
      }
      for (WfeWorkflowSection section : stateSections) {
         section.dispose();
      }
   }

   private WfeHeaderComposite headerComp;

   public WfeWorkflowSection getCurrentStateSection() {
      for (WfeWorkflowSection section : stateSections) {
         if (section.getStateDef().getName().equals(editor.getWorkItem().getCurrentStateName())) {
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
               long refreshStartNanos = System.nanoTime();
               if (XWidget.debugXWidgetPerformance) {
                  XWidget.debugXWidgets.syserr("WFE REFRESH PERF epochMs=%s phase=START\n", System.currentTimeMillis());
               }
               String stateName = awa.getCurrentStateName();

               // Determine if state already exists
               boolean found = false;
               for (WfeWorkflowSection section : stateSections) {
                  if (section.getStateDef().getName().equals(stateName)) {
                     found = true;
                  }
               }

               long phaseStartNanos = System.nanoTime();
               headerComp.refresh();
               debugRefreshPhase("header", phaseStartNanos);

               // Create state if not exist
               if (!found) {
                  Composite placeholderSectionComp = stateNameToComposite.get(stateName);
                  if (placeholderSectionComp != null) {
                     createStateSection(placeholderSectionComp, awa.getWorkDefinition().getStateByName(stateName));
                  }
               }

               phaseStartNanos = System.nanoTime();
               for (WfeWorkflowSection section : stateSections) {
                  section.refresh();
               }
               debugRefreshPhase("sections", phaseStartNanos);

               phaseStartNanos = System.nanoTime();
               refreshExpandStates();
               debugRefreshPhase("expandStates", phaseStartNanos);

               phaseStartNanos = System.nanoTime();
               setEditorWidgets();
               debugRefreshPhase("setEditorWidgets", phaseStartNanos);
               debugRefreshPhase("TOTAL", refreshStartNanos);
            }

         });
      }
   }

   /** Logs one Workflow Editor refresh phase using monotonic elapsed time; active only with XWidget debugging. */
   private void debugRefreshPhase(String phase, long startNanos) {
      if (XWidget.debugXWidgets.isLogToSysErr() && XWidget.debugXWidgetPerformance) {
         double durationMs = (System.nanoTime() - startNanos) / 1_000_000.0;
         XWidget.debugXWidgets.syserr("WFE REFRESH PERF epochMs=%s phase=%s durationMs=%.3f\n",
            System.currentTimeMillis(), phase, durationMs);
      }
   }

   public void refreshExpandStates() {
      boolean expansionChanged = false;
      for (WfeWorkflowSection wfeSection : stateSections) {
         if (!Widgets.isAccessible(wfeSection.getMainComp())) {
            continue;
         }
         if (wfeSection.isPinned()) {
            expansionChanged |= setSectionExpanded(wfeSection, true);
            continue;
         }
         expansionChanged |= setSectionExpanded(wfeSection, wfeSection.isCurrentState());
      }
      // Only relayout when a section actually expanded/collapsed. This method runs on every editor refresh
      // (including a single-attribute auto-persist); doing a deep layout(true, true) every time repaints the
      // whole workflow body and is the primary cause of the save flicker. See docs/ai/ide/ide-swt-layouts.md.
      if (expansionChanged && Widgets.isAccessible(bodyComp)) {
         bodyComp.layout(true);
      }
   }

   /**
    * Expands or collapses a section only when its SWT expanded state actually changes. Calling
    * {@link Section#setExpanded(boolean)} with the existing value still invalidates the Eclipse Forms hierarchy on
    * Windows; during an ordinary attribute refresh that repaints the shared form and makes every native Text value
    * visibly blank/repaint. The equality guard is therefore required even though no logical state changes.
    *
    * @return true when the section's expanded state changed and the workflow body needs one layout
    */
   private boolean setSectionExpanded(WfeWorkflowSection wfeSection, boolean expand) {
      if (!Widgets.isAccessible(wfeSection.getSection())) {
         if (expand) {
            wfeSection.expand();
         }
         return false;
      }
      boolean wasExpanded = wfeSection.getSection().isExpanded();
      if (wasExpanded == expand) {
         return false;
      }
      if (expand) {
         wfeSection.expand();
      } else {
         wfeSection.getSection().setExpanded(false);
      }
      return true;
   }

   @Override
   public void relationsModifed(Collection<Artifact> relModifiedArts, Collection<Artifact> goalMemberReordered,
      Collection<Artifact> sprintMemberReordered) {
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

   private StateDefinition getCurrentAtsWorkPage(AbstractWorkflowArtifact awa) {
      for (StateDefinition stateDef : getStatePagesOrderedByOrdinal(awa)) {
         if (awa.getCurrentStateName().equals(stateDef.getName())) {
            return stateDef;
         }
      }
      return null;
   }

   private List<StateDefinition> getStatePagesOrderedByOrdinal(IAtsWorkItem workItem) {
      return atsApi.getWorkDefinitionService().getStatesOrderedByOrdinal(workItem.getWorkDefinition());
   }

}
