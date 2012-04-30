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
package org.eclipse.osee.ats.core.client.config;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import org.eclipse.osee.ats.core.client.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionManagerCore;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsActionableItemToTeamDefinitionTest {

   @org.junit.Test
   public void testAtsActionableItemToTeamDefinition() throws Exception {
      boolean error = false;
      StringBuffer sb = new StringBuffer("Actionable Actionable Items with no Team Def associated:\n");
      for (Artifact artifact : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.ActionableItem,
         AtsUtilCore.getAtsBranch())) {
         ActionableItemArtifact aia = (ActionableItemArtifact) artifact;
         if (aia.isActionable()) {
            if (TeamDefinitionManagerCore.getImpactedTeamDefs(Arrays.asList(aia)).isEmpty()) {
               sb.append("[" + aia + "]");
               error = true;
            }
         }
      }
      assertFalse(sb.toString(), error);
   }
}
