/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.split.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Class to compute states of the workflow and fill the map
 * 
 * @author Chandan Bandemutt
 */
public class StateDistributionEntry {

   private final Artifact versionArtifact;
   private final Map<String, Double> stateSplitMap;

   /**
    * Constructor to set the version artifact and instantiate the map
    * 
    * @param ver : sets the version artifact
    */
   public StateDistributionEntry(final Artifact ver) {
      this.versionArtifact = ver;
      this.stateSplitMap = new HashMap<String, Double>();
   }

   /**
    * @return the map
    */
   public Map<String, Double> getStateSplitMap() {
      return this.stateSplitMap;
   }

   /**
    * Method to compute the states of the workflow and fill the map
    * 
    * @throws OseeCoreException :
    */
   public void computeStateSplit() throws OseeCoreException {
      for (AbstractWorkflowArtifact art : this.versionArtifact.getRelatedArtifactsOfType(
         AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow, TeamWorkFlowArtifact.class)) {
         IAtsWorkDefinition workDefinition = art.getWorkDefinition();
         List<IAtsStateDefinition> states = workDefinition.getStates();
         for (IAtsStateDefinition state : states) {
            double timeInState = 0;
            if (this.stateSplitMap.containsKey(state.getName())) {
               timeInState = getStateSplitMap().get(state.getName());
            }
            timeInState += art.getStateMgr().getHoursSpent(state.getName());
            this.stateSplitMap.put(state.getName(), timeInState);
         }
      }
   }

}
