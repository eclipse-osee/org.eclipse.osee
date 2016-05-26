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
package org.eclipse.osee.ats.core.client.internal.store;

import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactReader;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionArtifactReader implements IAtsArtifactReader<IAtsTeamDefinition> {

   private final ITeamDefinitionFactory teamDefFactory;

   public TeamDefinitionArtifactReader(ITeamDefinitionFactory teamDefFactory) {
      this.teamDefFactory = teamDefFactory;
   }

   @Override
   public IAtsTeamDefinition load(IAtsCache cache, Artifact teamDefArt) throws OseeCoreException {
      IAtsTeamDefinition teamDef =
         teamDefFactory.createTeamDefinition(teamDefArt.getGuid(), teamDefArt.getName(), teamDefArt.getUuid());
      teamDef.setStoreObject(teamDefArt);
      cache.cacheAtsObject(teamDef);

      teamDef.setName(teamDefArt.getName());
      teamDef.setActive(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Active, false));
      teamDef.setActionable(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Actionable, false));
      teamDef.setAllowCommitBranch(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false));
      teamDef.setAllowCreateBranch(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false));
      teamDef.setBaselineBranchUuid(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchUuid, ""));
      String workflowDefinition = teamDefArt.getSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, "");
      if (Strings.isValid(workflowDefinition)) {
         teamDef.setWorkflowDefinition(workflowDefinition);
      }
      String relatedTaskWorkDefinition =
         teamDefArt.getSoleAttributeValue(AtsAttributeTypes.RelatedTaskWorkDefinition, "");
      if (Strings.isValid(relatedTaskWorkDefinition)) {
         teamDef.setRelatedTaskWorkDefinition(relatedTaskWorkDefinition);
      }
      teamDef.setDescription(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
      teamDef.setFullName(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.FullName, ""));
      for (String ruleStr : teamDefArt.getAttributesToStringList(AtsAttributeTypes.RuleDefinition)) {
         teamDef.addRule(ruleStr);
      }
      return teamDef;
   }
}
