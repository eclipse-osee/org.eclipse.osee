/*
 * Created on Dec 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

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

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Instantiation of a StateXWidgetPage for a given StateDefinition to provide for automatic creation and management of
 * the XWidgets
 * 
 * @author Donald G. Dunne
 */
public class StateXWidgetPage implements IDynamicWidgetLayoutListener, IWorkPage {

   protected DynamicXWidgetLayout dynamicXWidgetLayout;
   protected final StateDefinition stateDefinition;
   protected final WorkDefinition workDefinition;
   protected TaskResolutionOptionRule taskResolutionOptions;
   private AbstractWorkflowArtifact sma;

   private StateXWidgetPage(WorkDefinition workDefinition, StateDefinition stateDefinition, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this.workDefinition = workDefinition;
      this.stateDefinition = stateDefinition;
      if (dynamicWidgetLayoutListener == null) {
         dynamicXWidgetLayout = new DynamicXWidgetLayout(this, optionResolver);
      } else {
         dynamicXWidgetLayout = new DynamicXWidgetLayout(dynamicWidgetLayoutListener, optionResolver);
      }
   }

   public StateXWidgetPage(WorkDefinition workFlowDefinition, StateDefinition stateDefinition, String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      this(workFlowDefinition, stateDefinition, xWidgetsXml, optionResolver, null);
   }

   /**
    * @param instructionLines input lines of WorkAttribute declarations
    */
   public StateXWidgetPage(WorkDefinition workDefinition, StateDefinition stateDefinition, String xWidgetsXml, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(workDefinition, stateDefinition, optionResolver, dynamicWidgetLayoutListener);
      try {
         if (xWidgetsXml != null) {
            processXmlLayoutDatas(xWidgetsXml);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "Error processing attributes", ex);
      }
   }

   public StateXWidgetPage(WorkDefinition workDefinition, StateDefinition stateDefinition, List<DynamicXWidgetLayoutData> datas, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(workDefinition, stateDefinition, optionResolver, dynamicWidgetLayoutListener);
      dynamicXWidgetLayout.setLayoutDatas(datas);
   }

   public StateXWidgetPage(List<DynamicXWidgetLayoutData> datas, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(null, null, datas, optionResolver, dynamicWidgetLayoutListener);
   }

   public StateXWidgetPage(List<DynamicXWidgetLayoutData> datas, IXWidgetOptionResolver optionResolver) {
      this(null, null, datas, optionResolver, null);
   }

   public StateXWidgetPage(String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      this(null, null, xWidgetsXml, optionResolver, null);
   }

   public StateXWidgetPage(IXWidgetOptionResolver optionResolver) {
      this(null, null, (String) null, optionResolver, null);
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      widgetCreated(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      widgetCreating(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   public void dispose() {
      try {
         for (DynamicXWidgetLayoutData layoutData : getlayoutDatas()) {
            layoutData.getXWidget().dispose();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof StateXWidgetPage) {
         return getName().equals(((StateXWidgetPage) obj).getName());
      }
      return false;
   }

   public DynamicXWidgetLayout createBody(IManagedForm managedForm, Composite parent, Artifact artifact, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      dynamicXWidgetLayout.createBody(managedForm, parent, artifact, xModListener, isEditable);
      return dynamicXWidgetLayout;
   }

   public Result isPageComplete() {
      try {
         for (DynamicXWidgetLayoutData layoutData : dynamicXWidgetLayout.getLayoutDatas()) {
            if (!layoutData.getXWidget().isValid().isOK()) {
               // Check to see if widget is part of a completed OR or XOR group
               if (!dynamicXWidgetLayout.isOrGroupFromAttrNameComplete(layoutData.getStoreName()) && !dynamicXWidgetLayout.isXOrGroupFromAttrNameComplete(layoutData.getStoreName())) {
                  return new Result(layoutData.getXWidget().isValid().getMessage());
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Result.TrueResult;
   }

   public String getHtml(String backgroundColor) throws OseeCoreException {
      return getHtml(backgroundColor, "", "");
   }

   public String getHtml(String backgroundColor, String preHtml, String postHtml) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.startBorderTable(100, backgroundColor, getPageName()));
      if (preHtml != null) {
         sb.append(preHtml);
      }
      for (DynamicXWidgetLayoutData layoutData : dynamicXWidgetLayout.getLayoutDatas()) {
         XWidget xWidget = layoutData.getXWidget();
         if (xWidget instanceof IArtifactWidget) {
            ((IArtifactWidget) xWidget).setArtifact(layoutData.getArtifact());
         }
         sb.append(layoutData.getXWidget().toHTML(AHTML.LABEL_FONT));
         sb.append(AHTML.newline());
      }
      if (postHtml != null) {
         sb.append(postHtml);
      }
      sb.append(AHTML.endBorderTable());
      return sb.toString();
   }

   @Override
   public String toString() {
      StringBuffer sb =
         new StringBuffer(
            stateDefinition.getPageName() + (stateDefinition.getName() != null ? " (" + stateDefinition.getName() + ") " : "") + "\n");
      try {
         for (StateDefinition page : stateDefinition.getToStates()) {
            sb.append("-> " + page.getPageName() + (stateDefinition.getReturnStates().contains(page) ? " (return)" : "") + "\n");
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return sb.toString();
   }

   public Set<DynamicXWidgetLayoutData> getlayoutDatas() {
      return dynamicXWidgetLayout.getLayoutDatas();
   }

   public void addLayoutDatas(List<DynamicXWidgetLayoutData> datas) {
      dynamicXWidgetLayout.addWorkLayoutDatas(datas);
   }

   public void addLayoutData(DynamicXWidgetLayoutData data) {
      dynamicXWidgetLayout.addWorkLayoutData(data);
   }

   public DynamicXWidgetLayoutData getLayoutData(String layoutName) {
      return dynamicXWidgetLayout.getLayoutData(layoutName);
   }

   public void processInstructions(Document doc) throws OseeCoreException {
      processLayoutDatas(doc.getDocumentElement());
   }

   protected void processXmlLayoutDatas(String xWidgetXml) throws OseeCoreException {
      dynamicXWidgetLayout.processlayoutDatas(xWidgetXml);
   }

   protected void processLayoutDatas(Element element) throws OseeCoreException {
      dynamicXWidgetLayout.processLayoutDatas(element);
   }

   @Override
   public String getPageName() {
      return stateDefinition.getPageName();
   }

   @Override
   public WorkPageType getWorkPageType() {
      return stateDefinition.getWorkPageType();
   }

   public String getName() {
      return stateDefinition.getName();
   }

   public String getFullName() {
      return stateDefinition.getFullName();
   }

   public List<StateDefinition> getToPages() {
      return stateDefinition.getToStates();
   }

   public List<StateDefinition> getReturnStates() {
      return stateDefinition.getReturnStates();
   }

   public boolean isReturnPage(StateDefinition page) {
      return getReturnStates().contains(page);
   }

   public StateDefinition getDefaultToPage() {
      if (!stateDefinition.getDefaultToStates().isEmpty()) {
         return stateDefinition.getDefaultToStates().iterator().next();
      }
      return null;
   }

   public StateDefinition getStateDefinition() {
      return stateDefinition;
   }

   public WorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   public DynamicXWidgetLayout getDynamicXWidgetLayout() {
      return dynamicXWidgetLayout;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public boolean isCompletedOrCancelledPage() {
      return getWorkPageType().isCompletedOrCancelledPage();
   }

   @Override
   public boolean isCompletedPage() {
      return getWorkPageType().isCompletedPage();
   }

   @Override
   public boolean isCancelledPage() {
      return getWorkPageType().isCancelledPage();
   }

   @Override
   public boolean isWorkingPage() {
      return getWorkPageType().isWorkingPage();
   }

   @Override
   public String getDescription() {
      return null;
   }

   @Override
   public Integer getDefaultPercent() {
      return 0;
   }

   public AbstractWorkflowArtifact getSma() {
      return sma;
   }

   public void setsma(AbstractWorkflowArtifact sma) {
      this.sma = sma;
   }

   public TaskResolutionOptionRule getTaskResDef() {
      return taskResolutionOptions;
   }

   public void setTaskResDef(TaskResolutionOptionRule taskResolutionOptions) {
      this.taskResolutionOptions = taskResolutionOptions;
   }

   public boolean isUsingTaskResolutionOptions() {
      return this.taskResolutionOptions != null;
   }

   public boolean isStartPage() {
      return getStateDefinition().isStartState();
   }

   public boolean isCurrentState(AbstractWorkflowArtifact sma) {
      return sma.isInState(this);
   }

   public boolean isCurrentNonCompleteCancelledState(AbstractWorkflowArtifact sma) {
      return isCurrentState(sma) && !isCompletedOrCancelledPage();
   }

   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, StateXWidgetPage page, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      // Check extension points for page creation
      if (sma != null) {
         for (IAtsStateItem item : AtsStateItemManager.getStateItems(page.getStateDefinition())) {
            item.xWidgetCreated(xWidget, toolkit, page, art, xModListener, isEditable);
         }
      }
   }

   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) {

      // If no tool tip, add global tool tip
      if (!Strings.isValid(xWidget.getToolTip())) {
         ATSAttributes atsAttribute = ATSAttributes.getAtsAttributeByStoreName(layoutData.getId());
         if (atsAttribute != null && Strings.isValid(atsAttribute.getDescription())) {
            xWidget.setToolTip(atsAttribute.getDescription());
            layoutData.setToolTip(atsAttribute.getDescription());
         }
      }
      // Store workAttr in control for use by help
      if (xWidget.getControl() != null) {
         xWidget.getControl().setData(layoutData);
      }

   }

   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, StateXWidgetPage page, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      // Check extension points for page creation
      if (sma != null) {
         for (IAtsStateItem item : AtsStateItemManager.getStateItems(page.getStateDefinition())) {
            Result result = item.xWidgetCreating(xWidget, toolkit, page, art, xModListener, isEditable);
            if (result.isFalse()) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Error in page creation => " + result.getText());
            }
         }
      }
   }

   public void generateLayoutDatas(AbstractWorkflowArtifact sma) {
      this.sma = sma;
      // Add static layoutDatas to statePage
      for (StateItem stateItem : stateDefinition.getStateItems()) {
         if (stateItem instanceof WidgetDefinition) {
            processWidgetDefinition((WidgetDefinition) stateItem, sma);
         } else if (stateItem instanceof CompositeStateItem) {
            processComposite((CompositeStateItem) stateItem, sma);
         }
      }
   }

   private void processComposite(CompositeStateItem compositeStateItem, AbstractWorkflowArtifact sma) {
      boolean firstWidget = true;
      List<StateItem> stateItems = compositeStateItem.getStateItems();
      for (int x = 0; x < stateItems.size(); x++) {
         boolean lastWidget = x == stateItems.size() - 1;
         StateItem stateItem = stateItems.get(x);
         if (stateItem instanceof WidgetDefinition) {
            DynamicXWidgetLayoutData data = processWidgetDefinition((WidgetDefinition) stateItem, sma);
            if (firstWidget) {
               if (compositeStateItem.getNumColumns() > 0) {
                  data.setBeginComposite(compositeStateItem.getNumColumns());
               }
            }
            if (lastWidget) {
               data.setEndComposite(true);
            }
         } else if (stateItem instanceof CompositeStateItem) {
            processComposite((CompositeStateItem) stateItem, sma);
         }
         firstWidget = false;
      }
   }

   /**
    * TODO This will eventually go away and ATS pages will be generated straight from WidgetDefinitions.
    */
   private DynamicXWidgetLayoutData processWidgetDefinition(WidgetDefinition widgetDef, AbstractWorkflowArtifact sma) {
      DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(getDynamicXWidgetLayout());
      data.setDefaultValue(widgetDef.getDefaultValue());
      data.setHeight(widgetDef.getHeight());
      data.setStoreName(widgetDef.getStoreName());
      data.setToolTip(widgetDef.getToolTip());
      data.setId(widgetDef.getName());
      data.setXWidgetName(widgetDef.getXWidgetName());
      data.setArtifact(sma);
      data.setName(widgetDef.getName());
      if (widgetDef.is(WidgetOption.REQUIRED_FOR_TRANSITION)) {
         data.getXOptionHandler().add(XOption.REQUIRED);
      }
      for (WidgetOption widgetOpt : widgetDef.getOptions().getXOptions()) {
         XOption option = null;
         try {
            option = XOption.valueOf(widgetOpt.name());
         } catch (IllegalArgumentException ex) {
            // do nothing
         }
         if (option != null) {
            data.getXOptionHandler().add(option);
         }
      }
      addLayoutData(data);
      return data;
   }

   public boolean isValidatePage() {
      return AtsWorkDefinitions.isValidatePage(stateDefinition);
   }

   public boolean isValidateReviewBlocking() {
      return AtsWorkDefinitions.isValidateReviewBlocking(stateDefinition);
   }

   public boolean isForceAssigneesToTeamLeads() {
      return AtsWorkDefinitions.isForceAssigneesToTeamLeads(stateDefinition);
   }

   public boolean isRequireStateHoursSpentPrompt() {
      return AtsWorkDefinitions.isRequireStateHoursSpentPrompt(stateDefinition);
   }

   public boolean isAllowTransitionWithWorkingBranch() {
      return AtsWorkDefinitions.isAllowTransitionWithWorkingBranch(stateDefinition);
   }

   public boolean isAllowCreateBranch() {
      return AtsWorkDefinitions.isAllowCreateBranch(stateDefinition);
   }

   public boolean isAllowCommitBranch() {
      return AtsWorkDefinitions.isAllowCommitBranch(stateDefinition);
   }

}
