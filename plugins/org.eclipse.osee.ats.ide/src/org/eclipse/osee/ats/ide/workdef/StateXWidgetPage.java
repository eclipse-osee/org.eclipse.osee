/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.workdef;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.commit.XCommitManager;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Instantiation of a StateXWidgetPage for a given StateDefinition to provide for automatic creation and management of
 * the XWidgets
 *
 * @author Donald G. Dunne
 */
public class StateXWidgetPage implements IDynamicWidgetLayoutListener, IStateToken {

   protected SwtXWidgetRenderer dynamicXWidgetLayout;
   protected final StateDefinition stateDefinition;
   protected final WorkDefinition workDefinition;
   private final AbstractWorkflowArtifact awa;

   public StateXWidgetPage(WorkDefinition workDefinition, StateDefinition stateDefinition, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener, AbstractWorkflowArtifact awa) {
      this.awa = awa;
      this.workDefinition = workDefinition;
      this.stateDefinition = stateDefinition;
      if (dynamicWidgetLayoutListener == null) {
         dynamicXWidgetLayout = new SwtXWidgetRenderer(this, optionResolver);
      } else {
         dynamicXWidgetLayout = new SwtXWidgetRenderer(dynamicWidgetLayoutListener, optionResolver);
      }
   }

   public StateXWidgetPage(WorkDefinition workFlowDefinition, StateDefinition stateDefinition, String xWidgetsXml, IXWidgetOptionResolver optionResolver, AbstractWorkflowArtifact awa) {
      this(workFlowDefinition, stateDefinition, xWidgetsXml, optionResolver, null, awa);
   }

   /**
    * @param instructionLines input lines of WorkAttribute declarations
    */
   public StateXWidgetPage(WorkDefinition workDefinition, StateDefinition stateDefinition, String xWidgetsXml, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener, AbstractWorkflowArtifact awa) {
      this(workDefinition, stateDefinition, optionResolver, dynamicWidgetLayoutListener, awa);
      try {
         if (xWidgetsXml != null) {
            processXmlLayoutDatas(xWidgetsXml);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error processing attributes", ex);
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art,
      SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      if (art != null) {
         for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
            item.xWidgetCreated(xWidget, toolkit, stateDefinition, art, isEditable);
         }
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art,
      SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      if (art != null) {
         for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
            Result result = item.xWidgetCreating(xWidget, toolkit, stateDefinition, art, isEditable);
            if (result.isFalse()) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error in page creation => " + result.getText());
            }
         }
      }
   }

   public void dispose() {
      WidgetPageUtil.dispose(dynamicXWidgetLayout);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof StateXWidgetPage) {
         return getName().equals(((StateXWidgetPage) obj).getName());
      }
      return false;
   }

   public SwtXWidgetRenderer createBody(IManagedForm managedForm, Composite parent, Artifact artifact,
      XModifiedListener xModListener, boolean isEditable) {
      dynamicXWidgetLayout.createBody(managedForm, parent, artifact, xModListener, isEditable);
      return dynamicXWidgetLayout;
   }

   public String getHtml(String backgroundColor, String preHtml, String postHtml) {
      return WidgetPageUtil.getHtml(backgroundColor, preHtml, postHtml, dynamicXWidgetLayout.getLayoutDatas(),
         getName());
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer(
         stateDefinition.getName() + (stateDefinition.getName() != null ? " (" + stateDefinition.getName() + ") " : "") + "\n");
      try {
         for (StateDefinition page : stateDefinition.getToStates()) {
            sb.append("-> " + page.getName() + "\n");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return sb.toString();
   }

   public XWidgetRendererItem getLayoutData(String layoutName) {
      return dynamicXWidgetLayout.getLayoutData(layoutName);
   }

   public SwtXWidgetRenderer getDynamicXWidgetLayout() {
      return dynamicXWidgetLayout;
   }

   protected void processXmlLayoutDatas(String xWidgetXml) {
      dynamicXWidgetLayout.processlayoutDatas(xWidgetXml);
   }

   @Override
   public String getName() {
      return stateDefinition.getName();
   }

   @Override
   public StateType getStateType() {
      return stateDefinition.getStateType();
   }

   @Override
   public boolean isCompleted() {
      return getStateType().isCompleted();
   }

   @Override
   public boolean isCancelled() {
      return getStateType().isCancelled();
   }

   @Override
   public boolean isWorking() {
      return getStateType().isWorking();
   }

   @Override
   public boolean isCompletedOrCancelled() {
      return getStateType().isCompletedOrCancelled();
   }

   public StateDefinition getStateDefinition() {
      return stateDefinition;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public String getDescription() {
      return null;
   }

   public AbstractWorkflowArtifact getSma() {
      return awa;
   }

   public boolean isCurrentState(AbstractWorkflowArtifact sma) {
      return sma.isInState(this);
   }

   public boolean isCurrentNonCompleteCancelledState(AbstractWorkflowArtifact sma) {
      return isCurrentState(sma) && !isCompletedOrCancelled();
   }

   @Override
   public void createXWidgetLayoutData(XWidgetRendererItem layoutData, XWidget xWidget, FormToolkit toolkit,
      Artifact art, XModifiedListener xModListener, boolean isEditable) {
      // If no tool tip, add global tool tip
      if (!Strings.isValid(xWidget.getToolTip())) {
         String description = "";
         if (layoutData.getXWidgetName().equals(XCommitManager.class.getSimpleName())) {
            description = XCommitManager.DESCRIPTION;
            xWidget.setToolTip(description);
            layoutData.setToolTip(description);
         } else {
            setAttrToolTip(xWidget, layoutData);
         }
      }
      // Store workAttr in control for use by help
      if (xWidget.getControl() != null) {
         xWidget.getControl().setData(layoutData);
      }

   }

   protected void setAttrToolTip(XWidget xWidget, XWidgetRendererItem layoutData) {
      String description = "";
      if (AttributeTypeManager.typeExists(layoutData.getStoreName())) {
         try {
            AttributeTypeToken type = null;
            if (layoutData.getStoreId() > 0) {
               type = AttributeTypeManager.getAttributeType(layoutData.getStoreId());
            }
            if (type == null && Strings.isValid(layoutData.getStoreName())) {
               type = AttributeTypeManager.getType(layoutData.getStoreName());
            }
            if (type != null && Strings.isValid(type.getDescription())) {
               description = type.getDescription();
            }
            if (Strings.isValid(description)) {
               xWidget.setToolTip(description);
               layoutData.setToolTip(description);
            }
         } catch (Exception ex) {
            String msg = String.format("Error setting tooltip for widget [%s].  Error %s (see log for details)",
               xWidget.getLabel(), ex.getLocalizedMessage());
            OseeLog.log(Activator.class, Level.SEVERE, msg, ex);
         }
      }
   }

   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, StateDefinition stateDefinition,
      XModifiedListener xModListener, boolean isEditable) {
      // Check extension points for page creation
      if (art != null) {
         for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
            Result result = item.xWidgetCreating(xWidget, toolkit, stateDefinition, art, isEditable);
            if (result.isFalse()) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error in page creation => " + result.getText());
            }
         }
      }
   }

   public void generateLayoutDatas() {
      WidgetPageUtil.generateLayoutDatas(awa, stateDefinition.getLayoutItems(), dynamicXWidgetLayout);
   }

}
