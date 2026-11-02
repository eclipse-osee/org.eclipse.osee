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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.core.workflow.log.AtsLogUtility;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXTaskInfoWfeWidget;
import org.eclipse.osee.ats.ide.workdef.XWidgetWorkDefRenderer;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelValueWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetUtility;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public final class WfeWorkflowSection extends SectionPart {

   protected final AbstractWorkflowArtifact sma;
   private final StateDefinition stateDef;
   private boolean currentStateStyle;
   private boolean currentStateStyleInitialized;
   private boolean sectionCreated = false;
   private boolean titleStyleInitialized;
   private Color titleBarBackground;
   private Composite mainComp;
   private final List<XWidget> xWidgets = new ArrayList<>();
   private Section section;
   private final WorkflowEditor editor;
   private final AtsApi atsApi;
   private boolean pinned;
   private XXTaskInfoWfeWidget taskInfoXWidget;
   private XFormToolkit toolkit;
   private Composite workComp;

   public WfeWorkflowSection(Composite parent, int style, StateDefinition stateDef, AbstractWorkflowArtifact sma, final WorkflowEditor editor) {
      super(parent, editor.getToolkit(), style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.stateDef = stateDef;
      this.sma = sma;
      this.editor = editor;
      this.atsApi = AtsApiService.get();
   }

   public boolean isCurrentState() {
      boolean isCurrent = sma.isInState(stateDef);
      return isCurrent;
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);
      toolkit = editor.getToolkit();
      section = getSection();
      try {
         refreshStateTitle();
         section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         boolean isCurrentSectionExpanded = isCurrentSectionExpanded();

         // Don't create till current-state / expanded, only main comp
         createWfeSection();

         createPinAction(section);

         section.layout();
         section.setExpanded(isCurrentSectionExpanded);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createPinAction(Section section) {
      ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
      Action pinAction = new Action("Pin Open", IAction.AS_CHECK_BOX) {
         @Override
         public void run() {
            pinned = isChecked();
         }
      };
      pinAction.setToolTipText("Pin this state open during refresh");
      pinAction.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.PIN_EDITOR));
      toolBarManager.add(pinAction);
      ToolBar toolbar = toolBarManager.createControl(section);
      section.setTextClient(toolbar);
   }

   public void expand() {
      section.setExpanded(true);

      createWfeSection();
   }

   public boolean isPinned() {
      return pinned;
   }

   /**
    * Refreshes the section title and colors only when their values actually change. These setters invalidate the
    * Eclipse Forms Section; calling them on every widget refresh repaints all native Text children even when their
    * values did not change, producing the visible blank/repaint flicker.
    */
   private void refreshStateTitle() {
      boolean isCurrentState = isCurrentState();
      String currentStateTitle =
         getCurrentStateTitle(sma, stateDef.getName(), isCurrentState, stateDef.isCancelled());
      if (!currentStateTitle.equals(section.getText())) {
         section.setText(currentStateTitle);
      }
      if (!titleStyleInitialized) {
         titleBarBackground = new Color(section.getDisplay(), 220, 235, 252);
         section.setTitleBarBackground(titleBarBackground);
         section.setTitleBarBorderColor(null);
         section.setTitleBarGradientBackground(null);
         titleStyleInitialized = true;
      }
      if (!currentStateStyleInitialized || currentStateStyle != isCurrentState) {
         section.setTitleBarForeground(
            Displays.getSystemColor(isCurrentState ? SWT.COLOR_DARK_GREEN : SWT.COLOR_DARK_BLUE));
         if (!isCurrentState) {
            section.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
         }
         currentStateStyle = isCurrentState;
         currentStateStyleInitialized = true;
      }
   }

   private boolean isCurrentSectionExpanded() {
      return sma.isInState(stateDef);
   }

   private synchronized void createWfeSection() {
      if (sectionCreated) {
         return;
      }
      mainComp = editor.getToolkit().createClientContainer(section, 2);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      mainComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      XWidgetUtility.debugLayout(mainComp, SWT.COLOR_YELLOW);
      mainComp.layout();

      createWorkArea();

   }

   private Composite createWorkArea() {

      if (stateDef.getDescription() != null) {
         Composite labelComp = toolkit.createContainer(mainComp, 1);
         labelComp.setLayoutData(new GridData(GridData.FILL_BOTH));
         labelComp.setLayout(ALayout.getZeroMarginLayout());

         Label descLabel =
            editor.getToolkit().createLabel(labelComp, " State Description: " + stateDef.getDescription());
         GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
         descLabel.setLayoutData(gd);
      }

      workComp = toolkit.createContainer(mainComp, 1);
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.widthHint = 400;
      workComp.setLayoutData(gridData);
      XWidgetUtility.debugLayout(workComp, SWT.COLOR_BLUE);
      sectionCreated = true;

      // Add any dynamic XWidgets declared for page by IAtsWorkItemHook extensions
      for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
         for (XWidget xWidget : item.getDynamicXWidgetsPreBody(sma, stateDef.getName())) {
            xWidget.createWidgets(workComp, 2);
            xWidgets.add(xWidget);
            xWidgets.addAll(xWidget.getChildrenXWidgets());
         }
      }

      // If no layout specified, use default
      if (stateDef.isCompletedOrCancelled() && stateDef.getLayoutItems().isEmpty()) {
         Composite completeComp = new Composite(workComp, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         completeComp.setLayout(layout);
         completeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         if (stateDef.isCancelled()) {
            createCancelledPageWidgets(completeComp);
         } else if (stateDef.isCompleted()) {
            createCompletedPageWidgets(completeComp);
         }
      }

      // Create dynamic XWidgets
      createSectionXWidgetBody();

      // Add any dynamic XWidgets declared for page by IAtsWorkItemHook extensions
      for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
         for (XWidget xWidget : item.getDynamicXWidgetsPostBody(sma, stateDef.getName())) {
            xWidget.createWidgets(workComp, 2);
            xWidgets.add(xWidget);
            xWidgets.addAll(xWidget.getChildrenXWidgets());
         }
      }

      createTaskFooter();

      computeTextSizesAndReflow();
      return workComp;
   }

   public void computeTextSizesAndReflow() {
      for (XWidget widget : xWidgets) {
         if (widget instanceof XXTextWidget) {
            XWidgetUtility.computeXTextSize((XXTextWidget) widget);
         } else if (widget instanceof XXTextWidget) {
            XWidgetUtility.computeXTextSize((XXTextWidget) widget);
         }
      }
      getManagedForm().reflow(true);
   }

   private void createSectionXWidgetBody() {
      XWidgetWorkDefRenderer workDefRender =
         new XWidgetWorkDefRenderer(sma, sma.getWorkDefinition(), stateDef, stateDef.getLayoutItems());
      workDefRender.createBody(getManagedForm(), workComp, sma, null, true);
      xWidgets.addAll(workDefRender.getXWidgets());
   }

   private void createCancelledPageWidgets(Composite parent) {
      XWidget xWidget = null;
      xWidget = new XXLabelValueWidget("Cancelled from State", sma.getCancelledFromState());
      xWidget.createWidgets(parent, 1);
      xWidgets.add(xWidget);
      xWidget = new XXStringsWidget("Cancellation Reason");
      xWidget.set(XOption.VAL_SINGLE_TEXT);
      xWidget.setAttributeType(AtsAttributeTypes.CancelledReason);
      xWidget.setArtifact(sma);
      xWidget.createWidgets(parent, 1);
      xWidgets.add(xWidget);
      xWidget = new XXLabelValueWidget("Implementer(s)", atsApi.getImplementerService().getImplementersStr(sma));
      xWidget.createWidgets(parent, 1);
      xWidgets.add(xWidget);
   }

   private void createCompletedPageWidgets(Composite parent) {
      XWidget xWidget = null;
      xWidget = new XXLabelValueWidget("Completed from State", sma.getCompletedFromState());
      xWidget.createWidgets(parent, 1);
      xWidgets.add(xWidget);
      xWidget = new XXLabelValueWidget("Implementer(s)", atsApi.getImplementerService().getImplementersStr(sma));
      xWidget.createWidgets(parent, 1);
      xWidgets.add(xWidget);
   }

   private void createTaskFooter() {
      if (sma instanceof TeamWorkFlowArtifact) {
         Composite comp = new Composite(workComp, SWT.None);
         GridLayout layout = new GridLayout(6, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         taskInfoXWidget = new XXTaskInfoWfeWidget((TeamWorkFlowArtifact) sma, comp);
         xWidgets.add(taskInfoXWidget);
      }
   }

   @Override
   public String toString() {
      return stateDef + " for " + getSma();
   }

   protected static String getCurrentStateTitle(IAtsWorkItem workItem, String statePageName, boolean isCurrentState,
      boolean isCancelledState) {
      StringBuffer sb = new StringBuffer(statePageName);
      AtsApi atsApi = AtsApiService.get();
      if (isCurrentState && !workItem.isCompleted() && !workItem.isCancelled()) {
         sb.append(" - Current State");
      }
      if (workItem.isCancelled()) {
         if (isCancelledState) {
            if (Strings.isValid(workItem.getCancelledReason())) {
               sb.append(" - Reason: ");
               sb.append(Strings.truncate(workItem.getCancelledReason(), 50, true));
            }
         }
      }
      if (isCurrentState) {
         if (workItem.isCompleted()) {
            if (!workItem.getCurrentStateName().equals(StateType.Completed.toString())) {
               sb.append(" (Completed)");
            }
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(workItem.getCompletedDate()));
            IAtsLogItem item = atsApi.getWorkItemService().getStateStartedData(workItem, statePageName);
            if (item != null) {
               sb.append(" by ");
               sb.append(AtsLogUtility.getUserName(item.getUserId(), atsApi.getUserService()));
            }
         } else if (workItem.isCancelled()) {
            if (!workItem.getCurrentStateName().equals(StateType.Cancelled.toString())) {
               sb.append(" (Cancelled)");
            }
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(workItem.getCancelledDate()));
            IAtsLogItem item = atsApi.getWorkItemService().getStateStartedData(workItem, statePageName);
            if (item != null) {
               sb.append(" by ");
               sb.append(AtsLogUtility.getUserName(item.getUserId(), atsApi.getUserService()));
            }
         }
         if (workItem.getAssignees().size() > 0) {
            sb.append(" assigned to ");
            sb.append(workItem.getAssigneesStr(80));
         }
      } else {
         IAtsLogItem item = null;
         if (workItem.isCancelled() && workItem.getCancelledFromState().equals(statePageName)) {
            item = atsApi.getWorkItemService().getStateCancelledData(workItem, statePageName);
            sb.append(" - State Cancelled ");
         } else {
            item = atsApi.getWorkItemService().getStateCompletedData(workItem, statePageName);
            sb.append(" - State Completed ");
         }
         if (item != null) {
            sb.append(item.getDate(DateUtil.MMDDYYHHMM));
            sb.append(" by ");
            sb.append(AtsLogUtility.getUserName(item.getUserId(), atsApi.getUserService()));
         }
      }
      return sb.toString();
   }

   @Override
   public void dispose() {
      for (XWidget xWidget : xWidgets) {
         if (xWidget != null) {
            xWidget.dispose();
         }
      }
      super.dispose();
      if (titleBarBackground != null && !titleBarBackground.isDisposed()) {
         titleBarBackground.dispose();
      }
   }

   @Override
   public void refresh() {
      synchronized (this) {
         if (!Widgets.isAccessible(mainComp)) {
            return;
         }
      }
      super.refresh();
      try {
         for (XWidget xWidget : xWidgets) {
            xWidget.refresh();
         }
         refreshStateTitle();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public AbstractWorkflowArtifact getSma() {
      return sma;
   }

   public synchronized Composite getMainComp() {
      return mainComp;
   }

   public List<XWidget> getXWidgets(Class<?> clazz) {
      List<XWidget> widgets = new ArrayList<>();
      for (XWidget widget : xWidgets) {
         if (clazz.isInstance(widget)) {
            widgets.add(widget);
         }
      }
      return widgets;
   }

   public WorkflowEditor getEditor() {
      return editor;
   }

   public StateDefinition getStateDef() {
      return stateDef;
   }

   public List<XWidget> getXWidgets() {
      return xWidgets;
   }

   public XWidget getWidget(String widgetName) {
      for (XWidget widget : xWidgets) {
         if (widget.getLabel().equals(widgetName)) {
            return widget;
         }
      }
      return null;
   }

}
