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
package org.eclipse.osee.ats.ide.workflow.hooks;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkflowHook;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkflowHookIde extends IAtsWorkflowHook {

   default public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable) {
      // provided for subclass implementation
   }

   default public void widgetModified(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable) {
      // provided for subclass implementation
   }

   default public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable) {
      return Result.TrueResult;
   }

   default public List<XWidget> getDynamicXWidgetsPostBody(AbstractWorkflowArtifact sma, String stateName) {
      return Collections.emptyList();
   }

   default public List<XWidget> getDynamicXWidgetsPreBody(AbstractWorkflowArtifact sma, String stateName) {
      return Collections.emptyList();
   }

}
