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
package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;

/**
 * @author Donald G. Dunne
 */
public class AtsActionableItemToTeamDefinitionTest {

   @org.junit.Test
   public void testAtsActionableItemToTeamDefinition() throws Exception {
      boolean error = false;
      StringBuffer sb = new StringBuffer("Actionable Actionable Items with no Team Def associated:\n");
      for (IAtsActionableItem aia : AtsClientService.get().getQueryService().createQuery(
         AtsArtifactTypes.ActionableItem).getItems(IAtsActionableItem.class)) {
         if (aia.isActionable()) {
            Collection<IAtsTeamDefinition> impactedTeamDefs =
               AtsClientService.get().getTeamDefinitionService().getImpactedTeamDefs(Arrays.asList(aia));
            if (impactedTeamDefs.isEmpty()) {
               sb.append("[" + aia + "]");
               error = true;
            }
         }
      }
      assertFalse(sb.toString(), error);
   }
}
