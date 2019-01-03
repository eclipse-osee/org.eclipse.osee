/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.Active;

/**
 * Provide AIs from given Team WF's team definition and all children matching active parameter.
 *
 * @author Donald G. Dunne
 */
public class WorkflowsActiveAisContentProvider implements ITreeContentProvider {

   private final Active active;
   private final TeamWorkFlowArtifact teamWf;

   public WorkflowsActiveAisContentProvider(TeamWorkFlowArtifact teamWf, Active active) {
      this.teamWf = teamWf;
      this.active = active;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      Collection<IAtsActionableItem> ais = new ArrayList<>();
      if (inputElement instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) inputElement;
         ais.addAll(teamWf.getTeamDefinition().getActionableItems());
      }
      return ais.toArray(new Object[ais.size()]);
   }

   @Override
   public Object[] getChildren(Object inputElement) {
      Collection<IAtsActionableItem> ais = new ArrayList<>();
      if (inputElement instanceof IAtsActionableItem) {
         IAtsActionableItem ai = (IAtsActionableItem) inputElement;
         for (IAtsActionableItem childAi : ai.getChildrenActionableItems()) {
            ais.addAll(getActiveChildrenForTeamAndAi(teamWf.getTeamDefinition(), active, childAi));
         }
      }
      return ais.toArray(new Object[ais.size()]);
   }

   @Override
   public Object getParent(Object element) {
      Object parent = null;
      if (element instanceof IAtsActionableItem) {
         if (teamWf.getActionableItems().contains(element)) {
            parent = teamWf;
         } else {
            IAtsActionableItem ai = (IAtsActionableItem) element;
            parent = ai.getParentActionableItem();
         }
      }
      return parent;
   }

   @Override
   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   /**
    * Add ai to results if owned by teamDef an matches active status
    */
   private static List<IAtsActionableItem> getActiveChildrenForTeamAndAi(IAtsTeamDefinition teamDef, Active active, IAtsActionableItem ai) {
      List<IAtsActionableItem> results = new ArrayList<>();
      if (ai.getTeamDefinition() == null || ai.getTeamDefinition().equals(teamDef)) {
         if (active == Active.Both) {
            results.add(ai);
         } else {
            // assume active unless otherwise specified
            boolean attributeActive = ai.isActive();
            if (active == Active.Active && attributeActive) {
               results.add(ai);
            } else if (active == Active.InActive && !attributeActive) {
               results.add(ai);
            }
         }
      }
      return results;
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

}
