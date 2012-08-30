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
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.config.AtsObjectsClient;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
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

      Collection<IAtsTeamDefinition> teamDefs = TeamDefinitions.getTeamDefinitionsNameStartsWith("CISv2");
      Assert.assertEquals(5, teamDefs.size());

      Collection<IAtsActionableItem> ais = getActionableItemsNameStartsWith("CISv2");
      Assert.assertEquals(5, ais.size());

      HashCollection<String, String> testNameToResultsMap = new HashCollection<String, String>();
      CountingMap<String> testNameToTimeSpentMap = new CountingMap<String>();
      Collection<Artifact> aiaArts = AtsObjectsClient.getArtifacts(ais);
      ValidateAtsDatabase.testActionableItemToTeamDefinition(aiaArts, testNameToResultsMap, testNameToTimeSpentMap);
      Assert.assertEquals(0, testNameToResultsMap.size());
   }

   public static Set<IAtsActionableItem> getActionableItemsNameStartsWith(String prefix) {
      Set<IAtsActionableItem> artifacts = new HashSet<IAtsActionableItem>();
      for (IAtsActionableItem aia : AtsConfigCache.instance.get(IAtsActionableItem.class)) {
         if (aia.getName().startsWith(prefix)) {
            artifacts.add(aia);
         }
      }
      return artifacts;
   }

}
