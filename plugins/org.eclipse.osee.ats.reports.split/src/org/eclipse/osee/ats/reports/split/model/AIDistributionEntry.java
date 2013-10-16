/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.split.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Model class representing a single entry for a actionable item.
 * 
 * @author Chandan Bandemutt
 */
public class AIDistributionEntry {

   private final Artifact versionArtifact;
   private final Map<IAtsActionableItem, Integer> aiSplitMap;

   /**
    * Constructor to set the version artifact and instantiate map
    * 
    * @param verArtifact : set version artifact
    */
   public AIDistributionEntry(final Artifact verArtifact) {
      this.versionArtifact = verArtifact;
      this.aiSplitMap = new HashMap<IAtsActionableItem, Integer>();
   }

   /**
    * Method to compute the count on actionable items and fill the map
    * 
    * @throws OseeCoreException :
    */
   public void computeAISplit() throws OseeCoreException {
      Collection<TeamWorkFlowArtifact> teamWorkflows =
         this.versionArtifact.getRelatedArtifactsOfType(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow,
            TeamWorkFlowArtifact.class);
      for (TeamWorkFlowArtifact workflow : teamWorkflows) {
         Set<IAtsActionableItem> actionableItems = workflow.getActionableItemsDam().getActionableItems();
         for (IAtsActionableItem aiArtifact : actionableItems) {
            int count = 0;
            if (this.aiSplitMap.containsKey(aiArtifact)) {
               count = this.aiSplitMap.get(aiArtifact);
            }
            this.aiSplitMap.put(aiArtifact, count + 1);
         }
      }

   }

   /**
    * @return the map
    */
   public Map<IAtsActionableItem, Integer> getAiSplitMap() {
      return this.aiSplitMap;
   }

}
