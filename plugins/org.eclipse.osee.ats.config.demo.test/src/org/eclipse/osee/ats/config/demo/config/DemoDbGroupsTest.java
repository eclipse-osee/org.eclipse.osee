/*
 * Created on May 25, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.util.Collection;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.support.test.util.DemoArtifactTypes;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DemoDbGroupsTest {
   @BeforeClass
   public static void validateDbInit() throws OseeCoreException {
      DemoDbUtil.checkDbInitAndPopulateSuccess();
   }

   /**
    * Test method for {@link DemoDbGroups}.
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testCreateGroups() throws OseeCoreException {
      Artifact groupArt =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.UniversalGroup, DemoDbGroups.TEST_GROUP_NAME,
            AtsUtil.getAtsBranchToken());
      Assert.assertNotNull(groupArt);

      Collection<Artifact> members = groupArt.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members);
      Assert.assertEquals(23, members.size());

      Assert.assertEquals(2, Artifacts.getOfType(AtsArtifactTypes.Action, members).size());
      Assert.assertEquals(14, Artifacts.getOfType(AtsArtifactTypes.Task, members).size());
      Assert.assertEquals(2, Artifacts.getOfType(DemoArtifactTypes.DemoCodeTeamWorkflow, members).size());
      Assert.assertEquals(2, Artifacts.getOfType(DemoArtifactTypes.DemoTestTeamWorkflow, members).size());
      Assert.assertEquals(2, Artifacts.getOfType(DemoArtifactTypes.DemoReqTeamWorkflow, members).size());
      Assert.assertEquals(7, Artifacts.getOfType(AtsArtifactTypes.TeamWorkflow, members).size());
   }
}
