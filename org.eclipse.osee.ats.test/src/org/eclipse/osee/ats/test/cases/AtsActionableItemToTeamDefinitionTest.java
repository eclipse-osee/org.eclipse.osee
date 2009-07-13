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
package org.eclipse.osee.ats.test.cases;
import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsActionableItemToTeamDefinitionTest {

   @org.junit.Test
   public void testAtsActionableItemToTeamDefinition() throws Exception {
      boolean error = false;
      for (Artifact artifact : ArtifactQuery.getArtifactListFromType(ActionableItemArtifact.ARTIFACT_NAME,
            AtsUtil.getAtsBranch())) {
         ActionableItemArtifact aia = (ActionableItemArtifact) artifact;
         if (aia.isActionable()) {
            if (TeamDefinitionArtifact.getImpactedTeamDefs(Arrays.asList(aia)).size() == 0) {
               System.err.println("Actionable Item \"" + aia + "\" has no Team Def associated and is Actionable.");
               error = true;
            }
         }
      }
      assertFalse(error);
   }
}
