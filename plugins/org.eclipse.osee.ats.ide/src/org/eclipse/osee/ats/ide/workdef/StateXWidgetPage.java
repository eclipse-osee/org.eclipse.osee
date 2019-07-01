/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workdef;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.ide.editor.tab.workflow.stateitem.AtsStateItemManager;
import org.eclipse.osee.ats.ide.editor.tab.workflow.stateitem.IAtsStateItem;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.widgets.commit.XCommitManager;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
   protected final IAtsStateDefinition stateDefinition;
   protected final IAtsWorkDefinition workDefinition;
   private final AbstractWorkflowArtifact awa;

   public StateXWidgetPage(IAtsWorkDefinition workDefinition, IAtsStateDefinition stateDefinition, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener, AbstractWorkflowArtifact awa) {
      this.awa = awa;
      this.workDefinition = workDefinition;
      this.stateDefinition = stateDefinition;
      if (dynamicWidgetLayoutListener == null) {
         dynamicXWidgetLayout = new SwtXWidgetRenderer(this, optionResolver);
      } else {
         dynamicXWidgetLayout = new SwtXWidgetRenderer(dynamicWidgetLayoutListener, optionResolver);
      }
   }

   public StateXWidgetPage(IAtsWorkDefinition workFlowDefinition, IAtsStateDefinition stateDefinition, String xWidgetsXml, IXWidgetOptionResolver optionResolver, AbstractWorkflowArtifact awa) {
      this(workFlowDefinition, stateDefinition, xWidgetsXml, optionResolver, null, awa);
   }

   /**
    * @param instructionLines input lines of WorkAttribute declarations
    */
   public StateXWidgetPage(IAtsWorkDefinition workDefinition, IAtsStateDefinition stateDefinition, String xWidgetsXml, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener, AbstractWorkflowArtifact awa) {
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
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      if (art != null) {
         for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
            item.xWidgetCreated(xWidget, toolkit, stateDefinition, art, isEditable);
         }
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      if (art != null) {
         for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
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

   public SwtXWidgetRenderer createBody(IManagedForm managedForm, Composite parent, Artifact artifact, XModifiedListener xModListener, boolean isEditable) {
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
         for (IAtsStateDefinition page : stateDefinition.getToStates()) {
            sb.append("-> " + page.getName() + (stateDefinition.getOverrideAttributeValidationStates().contains(
               page) ? " (return)" : "") + "\n");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return sb.toString();
   }

   public XWidgetRendererItem getLayoutData(String layoutName) {
      return dynamicXWidgetLayout.getLayoutData(layoutName);
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

   public IAtsStateDefinition getDefaultToPage() {
      if (stateDefinition.getDefaultToState() != null) {
         return stateDefinition.getDefaultToState();
      }
      return null;
   }

   public IAtsStateDefinition getStateDefinition() {
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
      return isCurrentState(sma) && !getStateType().isCompletedOrCancelledState();
   }

   @Override
   public void createXWidgetLayoutData(XWidgetRendererItem layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) {

      // If no tool tip, add global tool tip
      if (!Strings.isValid(xWidget.getToolTip())) {
         String description = "";
         if (layoutData.getXWidgetName().equals(XCommitManager.WIDGET_NAME)) {
            description = XCommitManager.DESCRIPTION;
         }
         AttributeTypeToken type = null;
         if (layoutData.getStoreId() > 0) {
            type = AtsAttributeTypes.getTypeById(layoutData.getStoreId());
         } else {
            type = AtsAttributeTypes.getTypeByName(layoutData.getStoreName());
         }
         if (type != null && Strings.isValid(type.getDescription())) {
            description = type.getDescription();
         }
         if (Strings.isValid(description)) {
            xWidget.setToolTip(description);
            layoutData.setToolTip(description);
         }
      }
      // Store workAttr in control for use by help
      if (xWidget.getControl() != null) {
         xWidget.getControl().setData(layoutData);
      }

   }

   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, IAtsStateDefinition stateDefinition, XModifiedListener xModListener, boolean isEditable) {
      // Check extension points for page creation
      if (art != null) {
         for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
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
