/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.config;

import junit.framework.TestCase;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
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
      for (Artifact artifact : ArtifactQuery.getArtifactsFromType(ActionableItemArtifact.ARTIFACT_NAME,
            BranchPersistenceManager.getAtsBranch())) {
         ActionableItemArtifact aia = (ActionableItemArtifact) artifact;
         if (aia.isActionable() && TeamDefinitionArtifact.getImpactedTeamDef(aia) == null) {
            System.err.println(aia + " has no Team Def associated and is Actionable.");
         }
      }
   }
}
