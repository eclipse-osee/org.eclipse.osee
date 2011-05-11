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
package org.eclipse.osee.ats.editor.stateItem;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStateItem {

   public String getName();

   public String getFullName();

   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, StateDefinition stateDefinition, Artifact art, boolean isEditable) throws OseeCoreException;

   public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, StateDefinition stateDefinition, Artifact art, boolean isEditable) throws OseeCoreException;

   public void widgetModified(XWidget xWidget, FormToolkit toolkit, StateDefinition stateDefinition, Artifact art, boolean isEditable) throws OseeCoreException;

   public String getOverrideTransitionToStateName(SMAWorkFlowSection section) throws OseeCoreException;

   public Collection<User> getOverrideTransitionToAssignees(AbstractWorkflowArtifact awa, String decision) throws OseeCoreException;

   public String getDescription();

   public String getBranchShortName(AbstractWorkflowArtifact sma) throws OseeCoreException;

   public boolean isAccessControlViaAssigneesEnabledForBranching() throws OseeCoreException;

   /**
    * @return Result of operation. If Result.isFalse(), commit will not continue and Result.popup will occur.
    */
   public Result committing(AbstractWorkflowArtifact sma) throws OseeCoreException;

   public List<XWidget> getDynamicXWidgetsPostBody(AbstractWorkflowArtifact sma, String stateName) throws OseeCoreException;

   public List<XWidget> getDynamicXWidgetsPreBody(AbstractWorkflowArtifact sma, String stateName) throws OseeCoreException;

}
