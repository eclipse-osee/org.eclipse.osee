/*
 * Created on Mar 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.config.copy;

import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemManager;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionManager;
import org.eclipse.osee.ats.config.copy.ConfigData;
import org.eclipse.osee.ats.config.copy.CopyAtsConfigurationOperation;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CopyAtsConfigurationOperationTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      DemoTestUtil.cleanupSimpleTest("CISv2");
   }

   @Test
   public void testDoWork() throws OseeCoreException {
      ConfigData data = CopyAtsValidationTest.getConfigData();

      XResultData rd = new XResultData(false);
      CopyAtsConfigurationOperation operation = new CopyAtsConfigurationOperation(data, rd);
      Operations.executeWork(operation);
      Assert.assertFalse(rd.isErrors());

      Collection<TeamDefinitionArtifact> teamDefs = TeamDefinitionManager.getTeamDefinitionsNameStartsWith("CISv2%");
      Assert.assertEquals(5, teamDefs.size());

      HashCollection<String, String> testNameToResultsMap = new HashCollection<String, String>();
      Collection<Artifact> teamDefArts = Collections.castAll(teamDefs);
      ValidateAtsDatabase.testTeamDefinitionHasWorkflow(testNameToResultsMap, teamDefArts);
      Assert.assertEquals(0, testNameToResultsMap.size());

      Collection<ActionableItemArtifact> ais = ActionableItemManager.getActionableItemsNameStartsWith("CISv2%");
      Assert.assertEquals(5, ais.size());

      testNameToResultsMap = new HashCollection<String, String>();
      Collection<Artifact> aiaArts = Collections.castAll(ais);
      ValidateAtsDatabase.testActionableItemToTeamDefinition(testNameToResultsMap, aiaArts);
      Assert.assertEquals(0, testNameToResultsMap.size());
   }
}
