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

package org.eclipse.osee.ats.ide.workflow.hooks;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.editor.tab.bit.WfeBitTab;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemHookIde extends IAtsWorkItemHook {

   default public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, StateDefinition stateDefinition,
      Artifact art, boolean isEditable) {
      // provided for subclass implementation
   }

   default public void widgetModified(XWidget xWidget, FormToolkit toolkit, StateDefinition stateDefinition,
      Artifact art, boolean isEditable) {
      // provided for subclass implementation
   }

   default public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, StateDefinition stateDefinition,
      Artifact art, boolean isEditable) {
      return Result.TrueResult;
   }

   default public List<XWidget> getDynamicXWidgetsPostBody(AbstractWorkflowArtifact sma, String stateName) {
      return Collections.emptyList();
   }

   default public List<XWidget> getDynamicXWidgetsPreBody(AbstractWorkflowArtifact sma, String stateName) {
      return Collections.emptyList();
   }

   default public WfeBitTab createBitTab(WorkflowEditor wfeEditor, IAtsTeamWorkflow teamWf) {
      return null;
   }

   default public Collection<WfeAbstractTab> createTabs(WorkflowEditor wfeEditor, IAtsWorkItem workItem) {
      return Collections.emptyList();
   }

   /**
    * @return true if this hook created the sibling widget
    */
   default public boolean createSiblingWidget(IAtsWorkItem workItem, Composite composite, WorkflowEditor editor) {
      return false;
   }

}
