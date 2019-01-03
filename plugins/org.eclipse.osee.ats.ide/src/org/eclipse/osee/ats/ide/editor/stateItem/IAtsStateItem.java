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
package org.eclipse.osee.ats.ide.editor.stateItem;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.ide.editor.WfeWorkflowSection;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStateItem {

   public String getName();

   public String getFullName();

   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable);

   public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable);

   public void widgetModified(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable);

   public String getOverrideTransitionToStateName(WfeWorkflowSection section);

   public Collection<IAtsUser> getOverrideTransitionToAssignees(AbstractWorkflowArtifact awa, String decision);

   public String getDescription();

   public String getBranchShortName(AbstractWorkflowArtifact sma);

   public boolean isAccessControlViaAssigneesEnabledForBranching();

   /**
    * @return Result of operation. If Result.isFalse(), commit will not continue and Result.popup will occur.
    */
   public Result committing(AbstractWorkflowArtifact sma);

   public List<XWidget> getDynamicXWidgetsPostBody(AbstractWorkflowArtifact sma, String stateName);

   public List<XWidget> getDynamicXWidgetsPreBody(AbstractWorkflowArtifact sma, String stateName);

}
