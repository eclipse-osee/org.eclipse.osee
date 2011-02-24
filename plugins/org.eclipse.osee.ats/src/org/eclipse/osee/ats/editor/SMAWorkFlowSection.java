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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.log.LogItem;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.editor.widget.ReviewInfoXWidget;
import org.eclipse.osee.ats.editor.widget.StateHoursSpentXWidget;
import org.eclipse.osee.ats.editor.widget.StatePercentCompleteXWidget;
import org.eclipse.osee.ats.editor.widget.TaskInfoXWidget;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.XCancellationReasonTextWidget;
import org.eclipse.osee.ats.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.workdef.StateXWidgetPage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValue;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowSection extends SectionPart {

   protected final AbstractWorkflowArtifact sma;
   private final StateXWidgetPage statePage;
   private final boolean isEditable, isCurrentState, isGlobalEditable;
   private Composite mainComp;
   private final List<XWidget> allXWidgets = new ArrayList<XWidget>();
   private boolean sectionCreated = false;
   private Section section;
   private final SMAEditor editor;
   private WETransitionComposite workflowTransitionComposite;

   public SMAWorkFlowSection(Composite parent, int style, StateXWidgetPage page, AbstractWorkflowArtifact sma, final SMAEditor editor) throws OseeCoreException {
      super(parent, editor.getToolkit(), style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.statePage = page;
      this.sma = sma;
      this.editor = editor;

      isEditable = isEditable(sma, page, editor);
      isGlobalEditable = !sma.isReadOnly() && sma.isAccessControlWrite() && editor.isPriviledgedEditModeEnabled();
      isCurrentState = sma.isInState(page);
      // parent.setBackground(Displays.getSystemColor(SWT.COLOR_CYAN));
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);

      section = getSection();
      try {
         section.setText(getCurrentStateTitle());
         if (sma.isInState(statePage)) {
            section.setTitleBarForeground(Displays.getSystemColor(SWT.COLOR_DARK_GREEN));
            section.setBackground(AtsUtil.ACTIVE_COLOR);
         }
         section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         // section.setBackground(Displays.getSystemColor(SWT.COLOR_MAGENTA));

         boolean isCurrentSectionExpanded = isCurrentSectionExpanded(statePage);

         if (isCurrentSectionExpanded) {
            createSection(section);
         }
         // Only load when users selects section
         section.addListener(SWT.Activate, new Listener() {

            @Override
            public void handleEvent(Event e) {
               try {
                  createSection(section);
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         });

         section.layout();
         section.setExpanded(isCurrentSectionExpanded);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Override to apply different algorithm to current section expansion.
    */
   public boolean isCurrentSectionExpanded(IWorkPage state) {
      return sma.isInState(state);
   }

   private synchronized void createSection(Section section) throws OseeCoreException {
      if (sectionCreated) {
         return;
      }

      mainComp = editor.getToolkit().createClientContainer(section, 2);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      mainComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      // mainComp.setBackground(Displays.getSystemColor(SWT.COLOR_DARK_YELLOW));
      mainComp.layout();

      SMAWorkFlowTab.createStateNotesHeader(mainComp, editor.getToolkit(), sma, 2, statePage.getPageName());

      Composite workComp = createWorkArea(mainComp, statePage, editor.getToolkit());

      if (isCurrentState) {
         workflowTransitionComposite = new WETransitionComposite(mainComp, this, editor, isEditable);
      }

      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.widthHint = 400;
      workComp.setLayoutData(gridData);
      sectionCreated = true;
   }

   protected Composite createWorkArea(Composite comp, StateXWidgetPage statePage, XFormToolkit toolkit) throws OseeCoreException {

      statePage.generateLayoutDatas(sma);

      // Create Page
      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      // workComp.setBackground(Displays.getSystemColor(SWT.COLOR_GREEN));

      createMetricsHeader(workComp);

      // Add any dynamic XWidgets declared for page by IAtsStateItem extensions
      for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
         for (XWidget xWidget : item.getDynamicXWidgetsPreBody(sma, statePage.getPageName())) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
            allXWidgets.addAll(xWidget.getChildrenXWidgets());
         }
      }

      if (statePage.isCompletedOrCancelledPage()) {
         Composite completeComp = new Composite(workComp, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         completeComp.setLayout(layout);
         completeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         if (statePage.isCancelledPage()) {
            createCancelledPageWidgets(completeComp);
         } else if (statePage.isCompletedPage()) {
            createCompletedPageWidgets(completeComp);
         }
      }

      // Create dynamic XWidgets
      DynamicXWidgetLayout dynamicXWidgetLayout =
         statePage.createBody(getManagedForm(), workComp, sma, xModListener, isEditable || isGlobalEditable);
      for (XWidget xWidget : dynamicXWidgetLayout.getXWidgets()) {
         allXWidgets.add(xWidget);
         allXWidgets.addAll(xWidget.getChildrenXWidgets());
      }

      // Add any dynamic XWidgets declared for page by IAtsStateItem extensions
      for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
         for (XWidget xWidget : item.getDynamicXWidgetsPostBody(sma, statePage.getPageName())) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
            allXWidgets.addAll(xWidget.getChildrenXWidgets());
         }
      }

      createTaskFooter(workComp, statePage);
      createReviewFooter(workComp, statePage);

      // Set all XWidget labels to bold font
      for (XWidget xWidget : allXWidgets) {
         if (xWidget.getLabelWidget() != null) {
            SMAEditor.setLabelFonts(xWidget.getLabelWidget(), FontManager.getDefaultLabelFont());
         }
      }

      return workComp;
   }

   private void createCancelledPageWidgets(Composite parent) throws OseeCoreException {
      XWidget xWidget = null;
      xWidget = new XLabelValue("Cancelled from State", sma.getCancelledFromState());
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);

      if (editor.isPriviledgedEditModeEnabled()) {
         xWidget = new XCancellationReasonTextWidget(sma);
         xWidget.addXModifiedListener(xModListener);
      } else {
         xWidget = new XLabelValue("Cancellation Reason", sma.getCancelledReason());
      }
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);
   }

   private void createCompletedPageWidgets(Composite parent) throws OseeCoreException {
      XWidget xWidget = null;
      xWidget = new XLabelValue("Completed from State", sma.getCompletedFromState());
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);
   }

   private void createMetricsHeader(Composite parent) {
      if (!statePage.isCompletedOrCancelledPage()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = ALayout.getZeroMarginLayout(4, false);
         layout.marginLeft = 2;
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         if (sma.getWorkDefinition().isStateWeightingEnabled()) {
            allXWidgets.add(new StatePercentCompleteXWidget(getManagedForm(), statePage, sma, comp, 2, xModListener,
               isCurrentState, editor));
         }
         allXWidgets.add(new StateHoursSpentXWidget(getManagedForm(), statePage, sma, comp, 2, xModListener,
            isCurrentState, editor));
      }
   }

   private void createReviewFooter(Composite parent, IWorkPage forState) {
      if (isShowReviewInfo() && sma.isTeamWorkflow()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new ReviewInfoXWidget(getManagedForm(), editor.getToolkit(), (TeamWorkFlowArtifact) sma,
            forState, comp, 1));
      }
   }

   private void createTaskFooter(Composite parent, IWorkPage state) {
      if (sma instanceof AbstractTaskableArtifact) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(6, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new TaskInfoXWidget(getManagedForm(), ((AbstractTaskableArtifact) sma), state, comp, 2));
      }
   }

   protected boolean isShowReviewInfo() {
      return sma.isTeamWorkflow();
   }

   public Result isXWidgetSavable() {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof IArtifactStoredWidget) {
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

   public Result isXWidgetDirty() throws OseeCoreException {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof IArtifactStoredWidget) {
            IArtifactStoredWidget artifactStoredWidget = (IArtifactStoredWidget) widget;
            Result result = artifactStoredWidget.isDirty();
            if (result.isTrue()) {
               return result;
            }
         }
      }
      return Result.FalseResult;
   }

   public void getDirtyIArtifactWidgets(List<IArtifactStoredWidget> widgets) throws OseeCoreException {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof IArtifactStoredWidget) {
            IArtifactStoredWidget artifactStoredWidget = (IArtifactStoredWidget) widget;
            if (artifactStoredWidget.isDirty().isTrue()) {
               widgets.add(artifactStoredWidget);
            }
         }
      }
   }

   private String getCurrentStateTitle() throws OseeCoreException {
      StringBuffer sb = new StringBuffer(statePage.getPageName());
      if (isEditable && !sma.isCompleted() && !sma.isCancelled()) {
         sb.append(" - Current State");
      }
      if (sma.isCancelled()) {
         if (statePage.isCancelledPage()) {
            if (Strings.isValid(sma.getCancelledReason())) {
               sb.append(" - Reason: ");
               sb.append(Strings.truncate(sma.getCancelledReason(), 50, true));
            }
         }
      }
      if (isCurrentState) {
         if (sma.isCompleted()) {
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(sma.getCompletedDate()));
            LogItem item = sma.getStateStartedData(statePage);
            sb.append(" by ");
            sb.append(item.getUser().getName());
         } else if (sma.isCancelled()) {
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(sma.internalGetCancelledDate()));
            LogItem item = sma.getStateStartedData(statePage);
            sb.append(" by ");
            sb.append(item.getUser().getName());
         }
         if (sma.getStateMgr().getAssignees().size() > 0) {
            sb.append(" assigned to ");
            sb.append(sma.getStateMgr().getAssigneesStr(80));
         }
      } else {
         LogItem item = sma.getStateCompletedData(statePage);
         if (item != null) {
            sb.append(" - State Completed ");
            sb.append(item.getDate(DateUtil.MMDDYYHHMM));
            sb.append(" by ");
            sb.append(item.getUser().getName());
         }
      }
      return sb.toString();
   }

   @Override
   public void dispose() {
      super.dispose();
      for (XWidget xWidget : allXWidgets) {
         if (xWidget != null) {
            xWidget.dispose();
         }
      }
      statePage.dispose();
   }

   final SMAWorkFlowSection fSection = this;
   final XModifiedListener xModListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget xWidget) {
         try {
            if (sma.isDeleted()) {
               return;
            }
            // Notify extensions of widget modified
            for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
               try {
                  item.widgetModified(fSection, xWidget);
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
            if (workflowTransitionComposite != null) {
               workflowTransitionComposite.updateTransitionToState();
               workflowTransitionComposite.updateTransitionToAssignees();
            }
            editor.onDirtied();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   };

   @Override
   public void refresh() {
      if (!Widgets.isAccessible(mainComp)) {
         return;
      }
      super.refresh();
      try {
         if (workflowTransitionComposite != null) {
            workflowTransitionComposite.refresh();
         }
         editor.onDirtied();
         for (XWidget xWidget : allXWidgets) {
            xWidget.refresh();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public boolean isCurrentState() {
      return isCurrentState;
   }

   public XComboViewer getTransitionToStateCombo() {
      if (workflowTransitionComposite != null) {
         return workflowTransitionComposite.getTransitionToStateCombo();
      }
      return null;
   }

   public AbstractWorkflowArtifact getSma() {
      return sma;
   }

   public StateXWidgetPage getPage() {
      return statePage;
   }

   public Composite getMainComp() {
      return mainComp;
   }

   public List<XWidget> getXWidgets(Class<?> clazz) {
      List<XWidget> widgets = new ArrayList<XWidget>();
      for (XWidget widget : allXWidgets) {
         if (clazz.isInstance(widget)) {
            widgets.add(widget);
         }
      }
      return widgets;
   }

   public static boolean isEditable(AbstractWorkflowArtifact sma, StateXWidgetPage page, SMAEditor editor) throws OseeCoreException {
      // must be writeable
      return !sma.isReadOnly() &&
      // and access control writeable
      sma.isAccessControlWrite() &&
      // and current state
      (page == null || sma.isInState(page)) &&
      // and one of these
      //
      // page is define to allow anyone to edit
      (sma.getStateDefinition().hasRule(RuleDefinitionOption.AllowEditToAll) ||
      // team definition has allowed anyone to edit
      sma.teamDefHasRule(RuleDefinitionOption.AllowEditToAll) ||
      // priviledged edit mode is on
      editor.isPriviledgedEditModeEnabled() ||
      // current user is assigned
      sma.isAssigneeMe() ||
      // current user is ats admin
      AtsUtil.isAtsAdmin());
   }

   public boolean isEditable() {
      return isEditable;
   }

   public StateXWidgetPage getStatePage() {
      return statePage;
   }
}
