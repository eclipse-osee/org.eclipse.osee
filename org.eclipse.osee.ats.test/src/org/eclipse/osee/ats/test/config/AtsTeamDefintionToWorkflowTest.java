/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.config;

import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsTeamDefintionToWorkflowTest extends TestCase {

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception {
      super.setUp();
   }

   public void testTeamDefinitionToWorkflow() throws Exception {
      boolean error = false;
      for (Artifact artifact : ArtifactQuery.getArtifactsFromType(TeamDefinitionArtifact.ARTIFACT_NAME,
            AtsPlugin.getAtsBranch())) {
         TeamDefinitionArtifact teamDef = (TeamDefinitionArtifact) artifact;
         if (teamDef.isActionable() && teamDef.getWorkFlowDefinition() == null) {
            System.err.println("Team Definition \"" + teamDef + "\" has no Work Flow associated and is Actionable.");
            error = true;
         }
      }
      assertFalse("See syserr message(s)", error);
   }
}
