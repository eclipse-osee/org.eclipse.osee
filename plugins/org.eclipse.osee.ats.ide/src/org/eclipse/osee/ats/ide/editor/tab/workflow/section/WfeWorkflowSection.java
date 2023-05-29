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

package org.eclipse.osee.ats.ide.editor.tab.workflow.section;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.core.workflow.log.AtsLogUtility;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeStateNotesHeader;
import org.eclipse.osee.ats.ide.editor.tab.workflow.widget.ReviewInfoXWidget;
import org.eclipse.osee.ats.ide.editor.tab.workflow.widget.TaskInfoXWidget;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.XCancellationReasonTextWidget;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.AttributeFormPart;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValue;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class WfeWorkflowSection extends SectionPart {

   protected final AbstractWorkflowArtifact sma;
   private final StateXWidgetPage statePage;
   private final boolean isEditable;
   private Composite mainComp;
   private final List<XWidget> allXWidgets = new ArrayList<>();
   private boolean sectionCreated = false;
   private Section section;
   private final WorkflowEditor editor;
   private WfeStateNotesHeader stateNotesHeader;

   public WfeWorkflowSection(Composite parent, int style, StateXWidgetPage page, AbstractWorkflowArtifact sma, final WorkflowEditor editor) {
      super(parent, editor.getToolkit(), style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.statePage = page;
      this.sma = sma;
      this.editor = editor;

      isEditable = AtsApiService.get().getAtsAccessService().isWorkflowEditable(sma);
   }

   public boolean isCurrentState() {
      boolean isCurrent = sma.isInState(statePage);
      return isCurrent;
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);

      section = getSection();
      try {
         refreshStateTitle();
         section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         boolean isCurrentSectionExpanded = isCurrentSectionExpanded(statePage);
         createSection(section);
         section.layout();
         section.setExpanded(isCurrentSectionExpanded);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void expand() {
      section.setExpanded(true);
      createSection(section);
   }

   private void refreshStateTitle() {
      String currentStateTitle =
         getCurrentStateTitle(sma, statePage.getName(), isCurrentState(), statePage.isCancelled());
      section.setText(currentStateTitle);
      if (sma.isInState(statePage)) {
         section.setTitleBarForeground(Displays.getSystemColor(SWT.COLOR_DARK_GREEN));
      } else {
         section.setTitleBarForeground(Displays.getSystemColor(SWT.COLOR_DARK_BLUE));
         section.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
      }
   }

   /**
    * Override to apply different algorithm to current section expansion.
    */
   public boolean isCurrentSectionExpanded(IStateToken state) {
      return sma.isInState(state);
   }

   private synchronized void createSection(Section section) {
      if (sectionCreated) {
         return;
      }

      mainComp = editor.getToolkit().createClientContainer(section, 2);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      mainComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      // mainComp.setBackground(Displays.getSystemColor(SWT.COLOR_DARK_YELLOW));
      mainComp.layout();

      stateNotesHeader = new WfeStateNotesHeader(mainComp, SWT.NONE, editor.getWorkItem(), statePage.getName(), editor);

      Composite workComp = createWorkArea(mainComp, statePage, editor.getToolkit());
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.widthHint = 400;
      workComp.setLayoutData(gridData);
      sectionCreated = true;
   }

   protected Composite createWorkArea(Composite comp, StateXWidgetPage statePage, XFormToolkit toolkit) {
      statePage.generateLayoutDatas();

      if (statePage.getStateDefinition().getDescription() != null) {
         Composite labelComp = toolkit.createContainer(comp, 1);
         labelComp.setLayoutData(new GridData(GridData.FILL_BOTH));
         labelComp.setLayout(ALayout.getZeroMarginLayout());

         Label descLabel = editor.getToolkit().createLabel(labelComp,
            " State Description: " + statePage.getStateDefinition().getDescription());
         GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
         descLabel.setLayoutData(gd);
      }

      // Create Page
      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));

      // Add any dynamic XWidgets declared for page by IAtsWorkItemHook extensions
      for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
         for (XWidget xWidget : item.getDynamicXWidgetsPreBody(sma, statePage.getName())) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
            allXWidgets.addAll(xWidget.getChildrenXWidgets());
         }
      }

      // If no layout specified, use default
      if (statePage.isCompletedOrCancelled() && statePage.getStateDefinition().getLayoutItems().isEmpty()) {
         Composite completeComp = new Composite(workComp, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         completeComp.setLayout(layout);
         completeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         if (statePage.isCancelled()) {
            createCancelledPageWidgets(completeComp);
         } else if (statePage.isCompleted()) {
            createCompletedPageWidgets(completeComp);
         }
      }

      // Create dynamic XWidgets
      createSectionBody(statePage, workComp);

      // Add any dynamic XWidgets declared for page by IAtsWorkItemHook extensions
      for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
         for (XWidget xWidget : item.getDynamicXWidgetsPostBody(sma, statePage.getName())) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
            allXWidgets.addAll(xWidget.getChildrenXWidgets());
         }
      }

      createTaskFooter(workComp, statePage);
      createReviewFooter(workComp, statePage);

      XWidgetUtility.setLabelFontsBold(allXWidgets);

      computeTextSizesAndReflow();
      return workComp;
   }

   private void createSectionBody(StateXWidgetPage statePage, Composite workComp) {
      SwtXWidgetRenderer dynamicXWidgetLayout =
         statePage.createBody(getManagedForm(), workComp, sma, xModListener, isEditable);
      for (XWidget xWidget : dynamicXWidgetLayout.getXWidgets()) {
         addAndCheckChildren(xWidget);
      }
   }

   private void addAndCheckChildren(XWidget xWidget) {
      allXWidgets.add(xWidget);
      xWidget.addXModifiedListener(xModListener);
      for (XWidget childWidget : xWidget.getChildrenXWidgets()) {
         addAndCheckChildren(childWidget);
      }
   }

   public void computeTextSizesAndReflow() {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof XText) {
            AttributeFormPart.computeXTextSize((XText) widget);
         }
      }
      getManagedForm().reflow(true);
   }

   private void createCancelledPageWidgets(Composite parent) {
      XWidget xWidget = null;
      xWidget = new XLabelValue("Cancelled from State", sma.getCancelledFromState());
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);
      xWidget = new XCancellationReasonTextWidget(sma, editor);
      xWidget.addXModifiedListener(xModListener);
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);
   }

   private void createCompletedPageWidgets(Composite parent) {
      XWidget xWidget = null;
      xWidget = new XLabelValue("Completed from State", sma.getCompletedFromState());
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);
   }

   private void createReviewFooter(Composite parent, IStateToken forState) {
      if (isShowReviewInfo() && sma.isTeamWorkflow()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         reviewInfoXWidget =
            new ReviewInfoXWidget(this, editor.getToolkit(), (TeamWorkFlowArtifact) sma, forState, comp, 1);
         allXWidgets.add(reviewInfoXWidget);
      }
   }

   private void createTaskFooter(Composite parent, IStateToken state) {
      if (sma instanceof TeamWorkFlowArtifact) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(6, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         taskInfoXWidget = new TaskInfoXWidget(getManagedForm(), (TeamWorkFlowArtifact) sma, state, comp, 2);
         allXWidgets.add(taskInfoXWidget);
      }
   }

   protected boolean isShowReviewInfo() {
      return sma.isTeamWorkflow();
   }

   public Result isXWidgetSavable() {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof ArtifactStoredWidget) {
            IStatus status = widget.isValid();
            if (!status.isOK()) {
               return new Result(false, status.getMessage());
            }
         }
      }
      return Result.TrueResult;
   }

   @Override
   public String toString() {
      return statePage + " for " + getSma();
   }

   public Result isXWidgetDirty(XResultData rd) {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof ArtifactStoredWidget) {
            ArtifactStoredWidget artifactStoredWidget = (ArtifactStoredWidget) widget;
            if (artifactStoredWidget.getArtifact() != null) {
               Result result = artifactStoredWidget.isDirty();
               if (result.isTrue()) {
                  rd.errorf("Widget [%s] is dirty\n", widget.toString());
               }
            }
         }
      }
      return Result.FalseResult;
   }

   public void getDirtyIArtifactWidgets(List<ArtifactStoredWidget> widgets) {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof ArtifactStoredWidget) {
            ArtifactStoredWidget artifactStoredWidget = (ArtifactStoredWidget) widget;
            if (artifactStoredWidget.isDirty().isTrue()) {
               widgets.add(artifactStoredWidget);
            }
         }
      }
   }

   protected static String getCurrentStateTitle(AbstractWorkflowArtifact sma, String statePageName,
      boolean isCurrentState, boolean isCancelledState) {
      StringBuffer sb = new StringBuffer(statePageName);
      if (isCurrentState && !sma.isCompleted() && !sma.isCancelled()) {
         sb.append(" - Current State");
      }
      if (sma.isCancelled()) {
         if (isCancelledState) {
            if (Strings.isValid(sma.getCancelledReason())) {
               sb.append(" - Reason: ");
               sb.append(Strings.truncate(sma.getCancelledReason(), 50, true));
            }
         }
      }
      if (isCurrentState) {
         if (sma.isCompleted()) {
            if (!sma.getCurrentStateName().equals(StateType.Completed.toString())) {
               sb.append(" (Completed)");
            }
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(sma.getCompletedDate()));
            IAtsLogItem item = sma.getStateMgr().getStateStartedData(statePageName);
            if (item != null) {
               sb.append(" by ");
               sb.append(AtsLogUtility.getUserName(item.getUserId(), AtsApiService.get().getUserService()));
            }
         } else if (sma.isCancelled()) {
            if (!sma.getCurrentStateName().equals(StateType.Cancelled.toString())) {
               sb.append(" (Cancelled)");
            }
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(sma.internalGetCancelledDate()));
            IAtsLogItem item = sma.getStateMgr().getStateStartedData(statePageName);
            if (item != null) {
               sb.append(" by ");
               sb.append(AtsLogUtility.getUserName(item.getUserId(), AtsApiService.get().getUserService()));
            }
         }
         if (sma.getStateMgr().getAssignees().size() > 0) {
            sb.append(" assigned to ");
            sb.append(sma.getStateMgr().getAssigneesStr(80));
         }
      } else {
         IAtsLogItem item = null;
         if (sma.isCancelled() && sma.getCancelledFromState().equals(statePageName)) {
            item = sma.getStateCancelledData(statePageName);
            sb.append(" - State Cancelled ");
         } else {
            item = sma.getStateCompletedData(statePageName);
            sb.append(" - State Completed ");
         }
         if (item != null) {
            sb.append(item.getDate(DateUtil.MMDDYYHHMM));
            sb.append(" by ");
            sb.append(AtsLogUtility.getUserName(item.getUserId(), AtsApiService.get().getUserService()));
         }
      }
      return sb.toString();
   }

   @Override
   public void dispose() {
      for (XWidget xWidget : allXWidgets) {
         if (xWidget != null) {
            xWidget.dispose();
         }
      }
      statePage.dispose();
      super.dispose();
   }

   final WfeWorkflowSection fSection = this;
   final XModifiedListener xModListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget xWidget) {
         try {
            if (sma.isDeleted()) {
               return;
            }
            // Notify extensions of widget modified
            for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
               try {
                  item.widgetModified(xWidget, editor.getToolkit(), sma.getStateDefinition(), sma, isEditable);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            // Update duplicate widgets
            DuplicateWidgetUpdateResolver.updateDuplicateWidgets(getManagedForm(), sma, xWidget);
            editor.onDirtied();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   };
   private ReviewInfoXWidget reviewInfoXWidget;
   private TaskInfoXWidget taskInfoXWidget;

   @Override
   public void refresh() {
      synchronized (this) {
         if (!Widgets.isAccessible(mainComp)) {
            return;
         }
      }
      super.refresh();
      try {
         synchronized (this) {
            if (Widgets.isAccessible(stateNotesHeader)) {
               stateNotesHeader.refresh();
            }
         }
         for (XWidget xWidget : allXWidgets) {
            if (xWidget instanceof ArtifactStoredWidget) {
               // Reload with with current artifact/attribute/relation
               ((ArtifactStoredWidget) xWidget).refresh();
            } else {
               xWidget.refresh();
            }
         }
         if (reviewInfoXWidget != null) {
            reviewInfoXWidget.reDisplay();
         }
         refreshStateTitle();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public AbstractWorkflowArtifact getSma() {
      return sma;
   }

   public StateXWidgetPage getPage() {
      return statePage;
   }

   public synchronized Composite getMainComp() {
      return mainComp;
   }

   public List<XWidget> getXWidgets(Class<?> clazz) {
      List<XWidget> widgets = new ArrayList<>();
      for (XWidget widget : allXWidgets) {
         if (clazz.isInstance(widget)) {
            widgets.add(widget);
         }
      }
      return widgets;
   }

   public boolean isEditable() {
      return isEditable;
   }

   public StateXWidgetPage getStatePage() {
      return statePage;
   }

   public WorkflowEditor getEditor() {
      return editor;
   }

}
