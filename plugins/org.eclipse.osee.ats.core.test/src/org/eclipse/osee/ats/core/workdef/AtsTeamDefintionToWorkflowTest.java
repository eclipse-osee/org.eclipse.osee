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
package org.eclipse.osee.ats.core.workdef;

import static org.junit.Assert.assertFalse;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsTeamDefintionToWorkflowTest {

   @org.junit.Test
   public void testTeamDefinitionToWorkflow() throws Exception {
      boolean error = false;
      StringBuffer sb = new StringBuffer("Actionable Team Definitions with no Work Definition:\n");
      for (Artifact artifact : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.TeamDefinition,
         AtsUtilCore.getAtsBranch())) {
         TeamDefinitionArtifact teamDef = (TeamDefinitionArtifact) artifact;
         if (teamDef.isActionable()) {
            WorkDefinitionMatch match = teamDef.getWorkDefinition();
            if (!match.isMatched()) {
               sb.append("[" + teamDef + "]");
            }
            error = true;
         }
      }
      assertFalse(sb.toString(), error);
   }

}
