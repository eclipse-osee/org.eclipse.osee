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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.editor.WfeWorkflowSection;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsStateItem implements IAtsStateItem {

   public final static String ALL_STATE_IDS = "ALL";
   private final String name;

   public AtsStateItem(String name) {
      this.name = name;
   }

   @Override
   public Result committing(AbstractWorkflowArtifact sma)  {
      return Result.TrueResult;
   }

   @Override
   public String getBranchShortName(AbstractWorkflowArtifact sma)  {
      return null;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Collection<IAtsUser> getOverrideTransitionToAssignees(AbstractWorkflowArtifact awa, String decision)  {
      return null;
   }

   @Override
   public String getOverrideTransitionToStateName(WfeWorkflowSection section)  {
      return null;
   }

   @Override
   public List<XWidget> getDynamicXWidgetsPostBody(AbstractWorkflowArtifact sma, String stateName)  {
      return Collections.emptyList();
   }

   @Override
   public List<XWidget> getDynamicXWidgetsPreBody(AbstractWorkflowArtifact sma, String stateName)  {
      return Collections.emptyList();
   }

   @Override
   public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable)  {
      // provided for subclass implementation
   }

   @Override
   public void widgetModified(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable)  {
      // provided for subclass implementation
   }

   @Override
   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable)  {
      return Result.TrueResult;
   }

   @Override
   public boolean isAccessControlViaAssigneesEnabledForBranching()  {
      return false;
   }

   @Override
   public String getFullName() {
      return getClass().getName();
   }

   @Override
   public String toString() {
      return getName();
   }

   /**
    * Allows subclass to add changes to transition before commit.
    */
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees, IAtsChangeSet changes)  {
      // provided for subclass implementation
   }

   /**
    * Allows subclass to to operation after transition and persist.
    */
   public void transitionPersisted(Collection<? extends IAtsWorkItem> workItems, Map<IAtsWorkItem, String> workItemFromStateMap, String toStateName) {
      // provided for subclass implementation
   }

   public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees)  {
      // provided for subclass implementation
   }

}