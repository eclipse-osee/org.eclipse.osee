/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.copy;

import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionableItemManager;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionManager;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CopyAtsConfigurationOperationTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest("CISv2");
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

      Collection<ActionableItemArtifact> ais = ActionableItemManager.getActionableItemsNameStartsWith("CISv2%");
      Assert.assertEquals(5, ais.size());

      HashCollection<String, String> testNameToResultsMap = new HashCollection<String, String>();
      Collection<Artifact> aiaArts = Collections.castAll(ais);
      ValidateAtsDatabase.testActionableItemToTeamDefinition(testNameToResultsMap, aiaArts);
      Assert.assertEquals(0, testNameToResultsMap.size());
   }
}
