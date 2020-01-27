/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config.tx;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.Csci;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxProgram;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigTxProgram extends AbstractAtsConfigTxObject<IAtsConfigTxProgram> implements IAtsConfigTxProgram {

   IAtsProgram program;

   public AtsConfigTxProgram(IAtsObject atsObject, AtsApi atsApi, IAtsChangeSet changes, IAtsConfigTx cfgTx) {
      super(atsObject, atsApi, changes, cfgTx);
      Conditions.assertTrue(atsObject instanceof IAtsProgram, "AtsObject must be of type IAtsProgram");
      program = (IAtsProgram) atsObject;

   }

   @Override
   public IAtsConfigTxProgram andTeamDef(IAtsTeamDefinitionArtifactToken teamDefTok) {
      IAtsTeamDefinition teamDef = cfgTx.getTeamDef(teamDefTok);
      if (teamDef == null || teamDef.isInvalid()) {
         teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(teamDefTok);
      }
      Conditions.assertNotNull(teamDef, "Team Definition must be created before Program %s", program);
      changes.relate(program, AtsRelationTypes.TeamDefinitionToProgram_TeamDefinition, teamDef);
      changes.setSoleAttributeValue(program, AtsAttributeTypes.TeamDefinitionReference, teamDef);
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.ProgramId, program);
      return this;
   }

   @Override
   public IAtsConfigTxProgram andCsci(Csci... cscis) {
      for (Csci csci : cscis) {
         changes.addAttribute(program, AtsAttributeTypes.CSCI, csci.name());
      }
      return this;
   }

}
