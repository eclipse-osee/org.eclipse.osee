/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.config;

import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsActionableItemToTeamDefinitionTest extends TestCase {

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
   }

   public void testAtsActionableItemToTeamDefinition() throws Exception {
      boolean error = false;
      for (Artifact artifact : ArtifactQuery.getArtifactsFromType(ActionableItemArtifact.ARTIFACT_NAME,
            AtsPlugin.getAtsBranch())) {
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
